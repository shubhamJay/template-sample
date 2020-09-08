package org.tmt.sample.integration

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import csw.aas.core.commons.AASConnection
import csw.location.api.models.Connection.HttpConnection
import csw.location.api.models._
import csw.location.api.scaladsl.LocationService
import csw.network.utils.Networks
import csw.prefix.models.Prefix
import csw.prefix.models.Subsystem.{CSW, ESW}
import csw.testkit.scaladsl.ScalaTestFrameworkTestKit
import io.bullet.borer.Json
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.tmt.embedded_keycloak.KeycloakData.{ApplicationUser, Client, Realm}
import org.tmt.embedded_keycloak.impl.StopHandle
import org.tmt.embedded_keycloak.utils.BearerToken
import org.tmt.embedded_keycloak.{EmbeddedKeycloak, KeycloakData, Settings}
import org.tmt.sample.SampleWiring
import org.tmt.sample.core.models.{Person, SampleResponse}
import org.tmt.sample.http.HttpCodecs

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}

class SampleAppIntegrationTest extends ScalaTestFrameworkTestKit with AnyWordSpecLike with Matchers with HttpCodecs {

  implicit val actorSystem: ActorSystem[SpawnProtocol.Command] = frameworkTestKit.actorSystem
  implicit val ec: ExecutionContext                            = actorSystem.executionContext
  override implicit val patienceConfig: PatienceConfig         = PatienceConfig(10.seconds)

  val locationService: LocationService = frameworkTestKit.frameworkWiring.locationService
  val hostname: String                 = Networks().hostname
  val keycloakPort                     = 8081
  val sampleAppPort                    = 8085
  val sampleWiring                     = new SampleWiring(Some(sampleAppPort))
  val appConnection: HttpConnection    = sampleWiring.settings.httpConnection

  var appLocation: HttpLocation  = _
  var appUri: Uri                = _
  var keycloakHandle: StopHandle = _

  protected override def beforeAll(): Unit = {
    super.beforeAll()
    keycloakHandle = startAndRegisterKeycloak(keycloakPort)
    sampleWiring.start(Metadata.empty).futureValue
    appLocation = locationService.resolve(appConnection, 5.seconds).futureValue.get
    appUri = Uri(appLocation.uri.toString)
  }

  protected override def afterAll(): Unit = {
    keycloakHandle.stop()
    locationService.unregister(AASConnection.value)
    sampleWiring.stop().futureValue
    super.afterAll()
  }

  "SampleWiring" must {

    "start the sample app and register with location service" in {
      val resolvedLocation = locationService.resolve(appConnection, 5.seconds).futureValue
      resolvedLocation.get.connection should ===(appConnection)
    }

    "should call sayHello and return SampleResponse as a result" in {
      val token = getToken("admin", "password1")()
      val request = HttpRequest(
        HttpMethods.GET,
        uri = appUri.withPath(Path / "sayHello"),
        headers = token.map(x => Seq(Authorization(OAuth2BearerToken(x)))).getOrElse(Nil)
      )

      val response: HttpResponse = Http().singleRequest(request).futureValue
      response.status should ===(StatusCode.int2StatusCode(200))
      Unmarshal(response).to[SampleResponse].futureValue should ===(SampleResponse("Hello!!!"))
    }

    "should call securedSayHello and return SampleResponse as a result" in {
      val token  = getToken("admin", "password1")()
      val person = Person("John")
      val request = HttpRequest(
        HttpMethods.POST,
        uri = appUri.withPath(Path / "securedSayHello"),
        headers = token.map(x => Seq(Authorization(OAuth2BearerToken(x)))).getOrElse(Nil),
        entity = HttpEntity(ContentTypes.`application/json`, Json.encode(person).toUtf8String.getBytes())
      )

      val response: HttpResponse = Http().singleRequest(request).futureValue

      response.status should ===(StatusCode.int2StatusCode(200))
      Unmarshal(response).to[Option[SampleResponse]].futureValue should ===(
        Some(SampleResponse(s"Secured Hello!!! ${person.name}"))
      )
    }

    "should call locations and return 401 as a result without valid token" in {
      val person = Person("John")
      val request = HttpRequest(
        HttpMethods.GET,
        uri = appUri.withPath(Path / "locations"),
        headers = Nil
      )

      val response: HttpResponse = Http().singleRequest(request).futureValue

      response.status should ===(StatusCode.int2StatusCode(401))
    }

    "should call securedSayHello and return 403 as a result without required role" in {
      val token  = getToken("nonAdmin", "password2")()
      val person = Person("John")
      val request = HttpRequest(
        HttpMethods.POST,
        uri = appUri.withPath(Path / "securedSayHello"),
        headers = token.map(x => Seq(Authorization(OAuth2BearerToken(x)))).getOrElse(Nil),
        entity = HttpEntity(ContentTypes.`application/json`, Json.encode(person).toUtf8String.getBytes())
      )

      val response: HttpResponse = Http().singleRequest(request).futureValue

      response.status should ===(StatusCode.int2StatusCode(403))
    }

    "should call locations and return Locations as a result" in {
      val token     = getToken("admin", "password1")()
      val aasPrefix = Prefix(CSW, "AAS")
      val appPrefix = Prefix(ESW, "sample_app")
      val request = HttpRequest(
        HttpMethods.GET,
        uri = appUri.withPath(Path / "locations"),
        headers = token.map(x => Seq(Authorization(OAuth2BearerToken(x)))).getOrElse(Nil)
      )

      val response: HttpResponse = Http().singleRequest(request).futureValue

      response.status should ===(StatusCode.int2StatusCode(200))
      Unmarshal(response).to[List[Location]].futureValue.map(_.prefix) should ===(List(aasPrefix, appPrefix))
    }

    "should call greeter and return stream response as a result" in {
      val person    = Person("John")
      val uri       = appLocation.uri
      val wsRequest = WebSocketRequest(uri = s"ws://${uri.getHost}:${uri.getPort}/greeter")

      val (connectionSink, connectionSource) =
        Source.asSubscriber[Message].mapMaterializedValue(Sink.fromSubscriber).preMaterialize()

      val requestSource = Source.single(TextMessage.Strict(encode(person))).concat(Source.maybe) // adding person request
      val flow          = Flow.fromSinkAndSourceCoupled(connectionSink, requestSource)

      Http().singleWebSocketRequest(wsRequest, flow) // send request

      // collect responses
      val (_, responsesF) = connectionSource
        .take(3) // to limit the infinite source
        .collect { case msg: TextMessage.Strict => decodeSampleRes(msg) }
        .toMat(Sink.seq)(Keep.both)
        .run()

      // assert on response
      val expectedRes = SampleResponse("Hello!!! " + person.name)
      eventually(Timeout(1600.millis)) { // 3 msg with 500 millis interval
        val responses = responsesF.futureValue
        responses.size shouldBe 3
        responses should ===(Seq.fill(3)(expectedRes))
      }

    }
  }

  private def encode(person: Person) = Json.encode(person).toUtf8String

  private def decodeSampleRes(msg: TextMessage.Strict) = Json.decode(msg.text.getBytes()).to[SampleResponse].value

  private def startAndRegisterKeycloak(port: Int): StopHandle = {
    val eswUserRole  = "Esw-user"
    val eswAdminRole = "Esw-admin"
    val locationServerClient =
      Client(name = "tmt-frontend-app", clientType = "public", passwordGrantEnabled = true)
    val keycloakData = KeycloakData(
      realms = Set(
        Realm(
          name = "TMT",
          users = Set(
            ApplicationUser("admin", "password1", realmRoles = Set(eswUserRole, eswAdminRole)),
            ApplicationUser("nonAdmin", "password2")
          ),
          clients = Set(locationServerClient),
          realmRoles = Set(eswUserRole, eswAdminRole)
        )
      )
    )
    val embeddedKeycloak = new EmbeddedKeycloak(keycloakData, Settings(port = port, printProcessLogs = false))
    val stopHandle       = Await.result(embeddedKeycloak.startServer(), 1.minute)
    locationService.register(HttpRegistration(AASConnection.value, keycloakPort, "auth")).futureValue
    stopHandle
  }

  private def getToken(userName: String, password: String): () => Some[String] = { () =>
    Some(
      BearerToken
        .fromServer(
          realm = "TMT",
          client = "tmt-frontend-app",
          host = hostname,
          port = keycloakPort,
          username = userName,
          password = password
        )
        .token
    )
  }

}
