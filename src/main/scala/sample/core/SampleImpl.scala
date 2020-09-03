package sample.core

import akka.actor.Cancellable
import akka.stream.scaladsl.Source
import csw.location.api.models.Location
import esw.http.template.wiring.CswContext
import sample.core.models.{Person, SampleResponse}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class SampleImpl(cswContext: CswContext) {
  def sayHello(): Future[SampleResponse] = Future.successful(SampleResponse("Hello!!!"))

  def securedSayHello(person: Person): Future[Option[SampleResponse]] =
    Future.successful(Some(SampleResponse(s"Secured Hello!!! ${person.name}")))

  def locations(): Future[List[Location]] = cswContext.locationService.list

  def sayHelloStream(person: Person): Source[SampleResponse, Cancellable] = {
    Source.tick(1.seconds, 1.seconds, SampleResponse("Hello!!! " + person.name))
  }

}
