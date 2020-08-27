package sample.http

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.BasicDirectives
import akka.http.scaladsl.testkit.ScalatestRouteTest
import csw.aas.core.token.AccessToken
import csw.aas.http.AuthorizationPolicy.RealmRolePolicy
import csw.aas.http.SecurityDirectives
import io.bullet.borer.compat.AkkaHttpCompat
import org.mockito.MockitoSugar.{mock, verify, when}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AnyWordSpec
import sample.core.SampleImpl
import sample.models.SampleResponse

import scala.util.Random

class TestApp extends AnyWordSpec with ScalatestRouteTest with AkkaHttpCompat {

  private val sampleImpl: SampleImpl                 = mock[SampleImpl]
  private val securityDirectives: SecurityDirectives = mock[SecurityDirectives]
  private val route: Route                           = new SampleRoute(sampleImpl, securityDirectives).route
  private val token: AccessToken                     = mock[AccessToken]
  private val accessTokenDirective                   = BasicDirectives.extract(_ => token)

  import SampleResponse._

  "SampleRoute" must {
    "sayHello must delegate to sampleImpl.sayHello" in {
      val response = SampleResponse(Random.nextString(10))
      when(securityDirectives.sGet(RealmRolePolicy("ESW-user"))).thenReturn(accessTokenDirective)
      when(sampleImpl.sayHello()).thenReturn(response)
      Get("sayHello") ~> route ~> check {
        verify(sampleImpl).sayHello()
        responseAs[SampleResponse] should ===(response)
      }
    }
  }

}
