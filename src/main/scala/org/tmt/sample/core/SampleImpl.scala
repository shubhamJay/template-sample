package org.tmt.sample.core

import akka.NotUsed
import akka.stream.scaladsl.Source
import csw.location.api.models.Location
import esw.http.template.wiring.CswServices
import org.tmt.sample.core.models.{Person, SampleResponse}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class SampleImpl(cswServices: CswServices) {
  def sayHello(): Future[SampleResponse] = Future.successful(SampleResponse("Hello!!!"))

  def securedSayHello(person: Person): Future[Option[SampleResponse]] =
    Future.successful(Some(SampleResponse(s"Secured Hello!!! ${person.name}")))

  def locations(): Future[List[Location]] = cswServices.locationService.list

  def sayHelloStream(person: Person): Source[SampleResponse, NotUsed] = {
    Source.tick(0.seconds, 500.millis, SampleResponse("Hello!!! " + person.name)).mapMaterializedValue(_ => NotUsed)
  }
}
