package sample.http

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.BasicDirectives
import akka.http.scaladsl.testkit.ScalatestRouteTest
import csw.aas.core.token.AccessToken
import csw.aas.http.AuthorizationPolicy.RealmRolePolicy
import csw.aas.http.SecurityDirectives
import io.bullet.borer.compat.AkkaHttpCompat
import org.mockito.MockitoSugar.{mock, reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.Future
import scala.util.Random

class SampleRouteTest extends AnyWordSpec with ScalatestRouteTest with AkkaHttpCompat with BeforeAndAfterEach with HttpCodecs {

  private val sampleImplWrapper: SampleImplWrapper   = mock[SampleImplWrapper]
  private val securityDirectives: SecurityDirectives = mock[SecurityDirectives]
  private val token: AccessToken                     = mock[AccessToken]
  private val accessTokenDirective                   = BasicDirectives.extract(_ => token)

  private val route: Route = new SampleRoute(sampleImplWrapper, securityDirectives).route

  override protected def beforeEach(): Unit = reset(sampleImplWrapper, securityDirectives)

  "SampleRoute" must {
    "sayHello must delegate to sampleImpl.sayHello" in {
      val response = SampleResponse(Random.nextString(10))
      when(sampleImplWrapper.sayHello()).thenReturn(response)

      Get("/sayHello") ~> route ~> check {
        verify(sampleImplWrapper).sayHello()
        responseAs[SampleResponse] should ===(response)
      }
    }

    "securedSayHello must check for ESw-user role and delegate to sampleImpl.securedSayHello" in {
      val response = SampleResponse(Random.nextString(10))
      val policy   = RealmRolePolicy("Esw-user")
      when(securityDirectives.sGet(policy)).thenReturn(accessTokenDirective)
      when(sampleImplWrapper.securedSayHello()).thenReturn(Future.successful(response))

      Get("/securedSayHello") ~> route ~> check {
        verify(sampleImplWrapper).securedSayHello()
        verify(securityDirectives).sGet(policy)
        responseAs[SampleResponse] should ===(response)
      }
    }
  }

}
