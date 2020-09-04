package sample.http

import sample.core.JSampleImpl
import sample.core.models.SampleResponse

import scala.compat.java8.FutureConverters.CompletionStageOps
import scala.concurrent.Future

class JSampleImplWrapper(jSampleImpl: JSampleImpl) {
  def sayBye(): Future[SampleResponse] = jSampleImpl.sayBye().toScala
}
