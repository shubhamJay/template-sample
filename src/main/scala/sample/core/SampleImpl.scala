package sample.core

import sample.core.models.SampleResponse

class SampleImpl {
  def sayHello(): SampleResponse        = SampleResponse("Hello!!!")
  def securedSayHello(): SampleResponse = SampleResponse("Secured Hello!!!")
}
