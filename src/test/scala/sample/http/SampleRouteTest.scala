package sample.http

import java.net.URI

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.BasicDirectives
import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import akka.stream.scaladsl.Source
import csw.aas.core.token.AccessToken
import csw.aas.http.AuthorizationPolicy.RealmRolePolicy
import csw.aas.http.SecurityDirectives
import csw.location.api.models.ComponentType.Service
import csw.location.api.models.Connection.HttpConnection
import csw.location.api.models._
import csw.prefix.models.Prefix
import io.bullet.borer.Json
import io.bullet.borer.compat.AkkaHttpCompat
import org.mockito.MockitoSugar.{mock, reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AnyWordSpec
import sample.TestHelper
import sample.TestHelper.randomSubsystem
import sample.core.SampleImpl
import sample.core.models.{Person, SampleResponse}

import scala.concurrent.Future
import scala.util.Random

class SampleRouteTest extends AnyWordSpec with ScalatestRouteTest with AkkaHttpCompat with BeforeAndAfterEach with HttpCodecs {

  private val service1: SampleImpl                   = mock[SampleImpl]
  private val service2                               = mock[JSampleImplWrapper]
  private val securityDirectives: SecurityDirectives = mock[SecurityDirectives]
  private val token: AccessToken                     = mock[AccessToken]
  private val accessTokenDirective                   = BasicDirectives.extract(_ => token)

  private val route: Route = new SampleRoute(service1, service2, securityDirectives).route

  override protected def beforeEach(): Unit = reset(service1, securityDirectives)

  "SampleRoute" must {
    "sayHello must delegate to service1.sayHello" in {
      val response = SampleResponse(Random.nextString(10))
      when(service1.sayHello()).thenReturn(Future.successful(response))

      Get("/sayHello") ~> route ~> check {
        verify(service1).sayHello()
        responseAs[SampleResponse] should ===(response)
      }
    }

    "sayBye must delegate to service2.sayBye" in {
      val response = SampleResponse(Random.nextString(10))
      when(service2.sayBye()).thenReturn(Future.successful(response))

      Get("/sayBye") ~> route ~> check {
        verify(service2).sayBye()
        responseAs[SampleResponse] should ===(response)
      }
    }

    "securedSayHello must check for Esw-user role and delegate to service1.securedSayHello" in {
      val response = SampleResponse(Random.nextString(10))
      val policy   = RealmRolePolicy("Esw-user")
      val john     = Person("John")
      when(securityDirectives.sPost(policy)).thenReturn(accessTokenDirective)
      when(service1.securedSayHello(john)).thenReturn(Future.successful(Some(response)))

      Post("/securedSayHello", john) ~> route ~> check {
        verify(service1).securedSayHello(Person("John"))
        verify(securityDirectives).sPost(policy)
        responseAs[Option[SampleResponse]] should ===(Some(response))
      }
    }
  }

  val connection: Connection.HttpConnection = HttpConnection(ComponentId(Prefix(randomSubsystem, "sample"), Service))

  "get locations must check for Esw-admin role and delegate to service1.locations" in {
    val httpLocation              = HttpLocation(connection, new URI(""), Metadata.empty)
    val locations: List[Location] = List(httpLocation)
    val policy                    = RealmRolePolicy("Esw-admin")
    when(securityDirectives.sGet(policy)).thenReturn(accessTokenDirective)
    when(service1.locations()).thenReturn(Future.successful(locations))

    Get("/locations") ~> route ~> check {
      verify(service1).locations()
      verify(securityDirectives).sGet(policy)

      responseAs[List[Location]] should ===(locations)
    }
  }

  "delegate to websocket greeter" in {
    val person            = Person("Peter")
    val sampleResponseMsg = TestHelper.randomString(10)
    val sampleResponse    = SampleResponse(sampleResponseMsg)
    when(service1.sayHelloStream(person)).thenReturn(Source.single(sampleResponse))
    val wsClient = WSProbe()

    WS("/greeter", wsClient.flow) ~> route ~>
    check {
      isWebSocketUpgrade shouldEqual true

      wsClient.sendMessage(Json.encode(person).toUtf8String)
      wsClient.expectMessage(Json.encode(sampleResponse).toUtf8String)

      wsClient.sendCompletion()
      wsClient.expectCompletion()
    }
  }
}
