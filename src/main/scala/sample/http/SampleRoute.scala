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
import sample.core.models.Person

import scala.concurrent.ExecutionContext

class SampleRoute(sampleImpl: SampleImpl, securityDirectives: SecurityDirectives)(implicit ec: ExecutionContext)
    extends HttpCodecs {

  val route: Route = path("sayHello") {
    complete(sampleImpl.sayHello())
  } ~
    path("securedSayHello") {
      securityDirectives.sPost(RealmRolePolicy("Esw-user")) { token =>
        entity(as[Person]) { person => complete(sampleImpl.securedSayHello(person)) }
      }
    } ~
    path("locations") {
      securityDirectives.sGet(RealmRolePolicy("Esw-admin")) { token =>
        complete(sampleImpl.locations())
      }
    } ~
    path("greeter") {
      handleWebSocketMessages(greeter)
    } ~
    path("getFile" / Segment) { name =>
      getFromResource(s"$name")
    }

  def greeter: Flow[Message, Message, Any] = {
    Flow[Message].flatMapConcat {
      case message: Strict =>
        val person = Json.decode(message.text.getBytes()).to[Person].value
        sampleImpl.sayHelloStream(person).map(s => TextMessage(Json.encode(s).toUtf8String))
      case _ =>
        Source.empty
    }
  }
}
