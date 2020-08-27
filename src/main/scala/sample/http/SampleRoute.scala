package sample.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import csw.aas.http.AuthorizationPolicy.RealmRolePolicy
import csw.aas.http.SecurityDirectives
import io.bullet.borer.compat.AkkaHttpCompat
import sample.core.SampleImpl

class SampleRoute(sampleImpl: SampleImpl, securityDirectives: SecurityDirectives) extends AkkaHttpCompat {
  import sample.models.SampleResponse._

  val route: Route =
    path("sayHello") {
      complete(sampleImpl.sayHello())
    } ~
      path("securedSayHello") {
        securityDirectives.sGet(RealmRolePolicy("Esw-user")) { token =>
          complete(sampleImpl.securedSayHello())
        }
      }

}
