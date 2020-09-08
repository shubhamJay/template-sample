package org.tmt.sample.http

import org.tmt.sample.core.JSampleImpl
import org.tmt.sample.core.models.SampleResponse

import scala.compat.java8.FutureConverters.CompletionStageOps
import scala.concurrent.Future

class JSampleImplWrapper(jSampleImpl: JSampleImpl) {
  def sayBye(): Future[SampleResponse] = jSampleImpl.sayBye().toScala
}
