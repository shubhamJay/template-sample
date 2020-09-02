package sample.http

import akka.http.scaladsl.model.ws.TextMessage.Strict
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Source}
import csw.aas.http.AuthorizationPolicy.RealmRolePolicy
import csw.aas.http.SecurityDirectives
import io.bullet.borer.Json
import sample.core.SampleImpl
import sample.core.models.{Person, SampleResponse}

import scala.concurrent.duration.DurationInt

class SampleRoute(sampleImpl: SampleImpl, securityDirectives: SecurityDirectives) extends HttpCodecs {

  val route: Route =
    path("sayHello") {
      complete(sampleImpl.sayHello())
    } ~
      path("securedSayHello") {
        securityDirectives.sPost(RealmRolePolicy("Esw-user")) { token =>
          entity(as[Person]) { person => complete(sampleImpl.securedSayHello(person)) }
        }
      } ~
      path("securedSayHello") {
        securityDirectives.sGet(RealmRolePolicy("Esw-admin")) { token =>
          complete(sampleImpl.locations())
        }
      } ~ path("greeter") {
      handleWebSocketMessages(greeter)
    }

  def greeter: Flow[Message, Message, Any] = {
    Flow[Message].flatMapConcat {
      case message: Strict =>
        val pe = Json.decode(message.text.getBytes()).to[Person].value
        Source.tick(1.second, 1.seconds, TextMessage(Json.encode(SampleResponse(pe.name)).toUtf8String))
      case _ =>
        Source.empty
    }
  }
}


