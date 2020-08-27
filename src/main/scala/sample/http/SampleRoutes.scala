package sample.http

import akka.http.scaladsl.server.RouteConcatenation._
import akka.http.scaladsl.server.{Directives, Route}
import csw.aas.http.AuthorizationPolicy.RealmRolePolicy
import csw.aas.http.SecurityDirectives
import sample.core.SampleImpl

class SampleRoutes(sampleImpl: SampleImpl, securityDirectives: SecurityDirectives) {
  val routes: Route = Directives.path("sample") {
    Directives.complete(sampleImpl.public())
  } ~ {
    securityDirectives.sGet(RealmRolePolicy("ESW-user")) { token =>
      Directives.path("/secured") {
        Directives.complete(sampleImpl.secured())
      }
    }
  }
}
