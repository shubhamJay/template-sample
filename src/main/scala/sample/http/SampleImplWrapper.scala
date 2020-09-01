package sample.http

import sample.core.JSampleImpl

import scala.compat.java8.FutureConverters.CompletionStageOps
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala

class SampleImplWrapper(jSampleImpl: JSampleImpl)(implicit ec: ExecutionContext) {

  def sayHello(): SampleResponse = jSampleImpl.sayHello()

  def securedSayHello(): Future[SampleResponse] = jSampleImpl.securedSayHello().toScala

  def locations(): Future[ServiceModel] = {
    jSampleImpl.getLocations.toScala.map { x =>
      ServiceModel(x.getLocs.asScala.toList, x.getSampleResponse)
    }
  }
}
