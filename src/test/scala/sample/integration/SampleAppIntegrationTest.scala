package sample.integration

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, StatusCode, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import csw.aas.core.commons.AASConnection
import csw.location.api.models.Connection.HttpConnection
import csw.location.api.models.HttpRegistration
import csw.location.api.scaladsl.LocationService
import csw.prefix.models.Prefix
import csw.testkit.scaladsl.ScalaTestFrameworkTestKit
import io.bullet.borer.compat.AkkaHttpCompat
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.tmt.embedded_keycloak.KeycloakData.{ApplicationUser, Client, Realm}
import org.tmt.embedded_keycloak.impl.StopHandle
import org.tmt.embedded_keycloak.{EmbeddedKeycloak, KeycloakData, Settings}
import sample.SampleWiring
import sample.models.SampleResponse

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}

class SampleAppIntegrationTest extends ScalaTestFrameworkTestKit with AnyWordSpecLike with Matchers with AkkaHttpCompat {

  implicit val actorSystem: ActorSystem[SpawnProtocol.Command] = frameworkTestKit.actorSystem
  implicit val ec: ExecutionContext                            = actorSystem.executionContext
  override implicit val patienceConfig: PatienceConfig         = PatienceConfig(10.seconds)

  val locationService: LocationService = frameworkTestKit.frameworkWiring.locationService
  var keycloakHandle: StopHandle       = _
  val keycloakPort                     = 8081
  val serverWiring                     = new SampleWiring(Some(8085), Some(Prefix("ESW.sample_app")))
  val httpConnection: HttpConnection   = serverWiring.settings.httpConnection

  protected override def beforeAll(): Unit = {
    super.beforeAll()
    keycloakHandle = startAndRegisterKeycloak(keycloakPort)
    serverWiring.start().futureValue
  }

  protected override def afterAll(): Unit = {
    keycloakHandle.stop()
    locationService.unregister(AASConnection.value)
    serverWiring.stop().futureValue
    super.afterAll()
  }
  "SampleWiring" must {

    "start the sample app and register with location service" in {
      val resolvedLocation = locationService.resolve(httpConnection, 5.seconds).futureValue
      resolvedLocation.get.connection should ===(httpConnection)

    }

    "should call sayHello and return SampleResponse as a result" in {
      import SampleResponse._
      val resolvedAppLocation = locationService.resolve(httpConnection, 5.seconds).futureValue
      val appUri              = Uri(resolvedAppLocation.get.uri.toString)
      val request             = HttpRequest(HttpMethods.GET, uri = appUri.withPath(Path / "sayHello"))
      val response            = Http().singleRequest(request).futureValue
      response.status should ===(StatusCode.int2StatusCode(200))
      println(Unmarshal(response).to[SampleResponse])
    }
  }

  private def startAndRegisterKeycloak(port: Int): StopHandle = {
    val AdminRole = "location-admin"
    val locationServerClient =
      Client(name = "tmt-frontend-app", clientType = "public", passwordGrantEnabled = true)
    val keycloakData = KeycloakData(
      realms = Set(
        Realm(
          name = "TMT",
          users = Set(
            ApplicationUser("admin", "password1", realmRoles = Set(AdminRole)),
            ApplicationUser("nonAdmin", "password2")
          ),
          clients = Set(locationServerClient),
          realmRoles = Set(AdminRole)
        )
      )
    )
    val embeddedKeycloak = new EmbeddedKeycloak(keycloakData, Settings(port = port, printProcessLogs = false))
    val stopHandle       = Await.result(embeddedKeycloak.startServer(), 1.minute)
    locationService.register(HttpRegistration(AASConnection.value, keycloakPort, "auth")).futureValue
    stopHandle
  }

}
