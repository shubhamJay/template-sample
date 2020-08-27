package sample.core

import sample.models.SampleResponse

class SampleImpl {
  def sayHello(): SampleResponse        = SampleResponse("Hello!!!")
  def securedSayHello(): SampleResponse = SampleResponse("Secured Hello!!!")
}
