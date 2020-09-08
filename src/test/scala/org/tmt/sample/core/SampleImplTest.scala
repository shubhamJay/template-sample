package org.tmt.sample.core

import esw.http.template.wiring.CswServices
import org.mockito.MockitoSugar.mock
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.tmt.sample.core.models.{Person, SampleResponse}

class SampleImplTest extends AnyWordSpec with Matchers {
  val cswServices: CswServices = mock[CswServices]
  val jSampleImpl: JSampleImpl = mock[JSampleImpl]

  "SampleImpl" must {
    "sayHello should return sample response of 'Hello!!!'" in {
      val sampleImpl = new SampleImpl(cswServices)
      sampleImpl.sayHello().futureValue should ===(SampleResponse("Hello!!!"))
    }

    "securedSayHello should return sample response of 'Secured Hello!!!'" in {
      val sampleImpl = new SampleImpl(cswServices)
      sampleImpl.securedSayHello(Person("John")).futureValue should ===(Some(SampleResponse("Secured Hello!!! John")))
    }
  }
}
