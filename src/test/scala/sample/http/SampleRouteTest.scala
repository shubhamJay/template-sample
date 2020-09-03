package sample.http

import java.net.URI

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.BasicDirectives
import akka.http.scaladsl.testkit.ScalatestRouteTest
import csw.aas.core.token.AccessToken
import csw.aas.http.AuthorizationPolicy.RealmRolePolicy
import csw.aas.http.SecurityDirectives
import csw.location.api.models.ComponentType.Service
import csw.location.api.models.Connection.HttpConnection
import csw.location.api.models.{ComponentId, Connection, HttpLocation, Location, Metadata}
import csw.prefix.models.{Prefix, Subsystem}
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

  private val sampleImpl: SampleImpl                 = mock[SampleImpl]
  private val securityDirectives: SecurityDirectives = mock[SecurityDirectives]
  private val token: AccessToken                     = mock[AccessToken]
  private val accessTokenDirective                   = BasicDirectives.extract(_ => token)

  private val route: Route = new SampleRoute(sampleImpl, securityDirectives).route

  override protected def beforeEach(): Unit = reset(sampleImpl, securityDirectives)

  "SampleRoute" must {
    "sayHello must delegate to sampleImpl.sayHello" in {
      val response = SampleResponse(Random.nextString(10))
      when(sampleImpl.sayHello()).thenReturn(Future.successful(response))

      Get("/sayHello") ~> route ~> check {
        verify(sampleImpl).sayHello()
        responseAs[SampleResponse] should ===(response)
      }
    }

    "securedSayHello must check for ESw-user role and delegate to sampleImpl.securedSayHello" in {
      val response = SampleResponse(Random.nextString(10))
      val policy   = RealmRolePolicy("Esw-user")
      val john     = Person("John")
      when(securityDirectives.sPost(policy)).thenReturn(accessTokenDirective)
      when(sampleImpl.securedSayHello(john)).thenReturn(Future.successful(Some(response)))

      Post("/securedSayHello", john) ~> route ~> check {
        verify(sampleImpl).securedSayHello(Person("John"))
        verify(securityDirectives).sPost(policy)
        responseAs[Option[SampleResponse]] should ===(Some(response))
      }
    }
  }

  val connection: Connection.HttpConnection = HttpConnection(ComponentId(Prefix(randomSubsystem, "sample"), Service))

  "get locations must delegate to sampleImpl.locations" in {
    val httpLocation              = HttpLocation(connection, new URI(""), Metadata.empty)
    val locations: List[Location] = List(httpLocation)
    val policy                    = RealmRolePolicy("Esw-admin")
    when(securityDirectives.sGet(policy)).thenReturn(accessTokenDirective)
    when(sampleImpl.locations()).thenReturn(Future.successful(locations))

    Get("/locations") ~> route ~> check {
      verify(sampleImpl).locations()
      verify(securityDirectives).sGet(policy)

      responseAs[List[Location]] should ===(locations)
    }
  }
}
