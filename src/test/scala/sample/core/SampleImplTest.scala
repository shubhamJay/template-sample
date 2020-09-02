package sample.core

import esw.http.template.wiring.CswContext
import org.mockito.MockitoSugar.mock
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import sample.core.models.{Person, SampleResponse}

class SampleImplTest extends AnyWordSpec with Matchers {
  val mockCswContext = mock[CswContext]

  "SampleImpl" must {
    "sayHello should return sample response of 'Hello!!!'" in {
      val sampleImpl = new SampleImpl(mockCswContext)
      sampleImpl.sayHello() should ===(SampleResponse("Hello!!!"))
    }

    "securedSayHello should return sample response of 'Secured Hello!!!'" in {
      val sampleImpl = new SampleImpl(mockCswContext)
      sampleImpl.securedSayHello(Person("John")) should ===(SampleResponse("Secured Hello!!!"))
    }
  }
}
