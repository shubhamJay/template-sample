package sample.core

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import sample.core.models.SampleResponse

class SampleImplTest extends AnyWordSpec with Matchers {

  "SampleImpl" must {
    "sayHello should return sample response of 'Hello!!!'" in {
      val sampleImpl = new SampleImpl()
      sampleImpl.sayHello() should ===(SampleResponse("Hello!!!"))
    }

    "securedSayHello should return sample response of 'Secured Hello!!!'" in {
      val sampleImpl = new SampleImpl()
      sampleImpl.securedSayHello() should ===(SampleResponse("Secured Hello!!!"))
    }
  }
}
