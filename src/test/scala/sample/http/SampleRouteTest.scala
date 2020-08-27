package sample.http

import akka.http.scaladsl.testkit.ScalatestRouteTest
import csw.aas.http.SecurityDirectives
import org.mockito.MockitoSugar.mock
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import sample.core.SampleImpl

class SampleRouteTest extends AnyWordSpec with ScalatestRouteTest with Matchers {

  private val sampleImpl: SampleImpl                 = mock[SampleImpl]
  private val securityDirectives: SecurityDirectives = mock[SecurityDirectives]

  private val route = new SampleRoute(sampleImpl, securityDirectives)

  "SampleRoute" must {
    "asfd" in {
      Get("/sayHello") ~> route ~> check {}

    }

    Get("/config/test.conf") ~> route ~> check {}

  }

}
