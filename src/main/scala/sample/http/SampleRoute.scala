package sample.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import csw.aas.http.AuthorizationPolicy.RealmRolePolicy
import csw.aas.http.SecurityDirectives

class SampleRoute(sampleImplWrapper: SampleImplWrapper, securityDirectives: SecurityDirectives) extends HttpCodecs {

  val route: Route =
    path("sayHello") {
      complete(sampleImplWrapper.sayHello())
    } ~
      path("securedSayHello") {
        securityDirectives.sGet(RealmRolePolicy("Esw-user")) { token =>
          complete(sampleImplWrapper.securedSayHello())
        }
      } ~
      path("locations") {
        complete(sampleImplWrapper.locations())
      }
}
