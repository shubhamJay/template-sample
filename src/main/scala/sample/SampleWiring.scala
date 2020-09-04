package sample

import akka.http.scaladsl.server.Route
import esw.http.template.wiring.ServerWiring
import sample.core.{JSampleImpl, SampleImpl}
import sample.http.{JSampleImplWrapper, SampleRoute}

class SampleWiring(val port: Option[Int]) extends ServerWiring {
  override val actorSystemName: String = "Sample-actor-system"

  lazy val jSampleImpl: JSampleImpl = new JSampleImpl(jCswServices)
  lazy val sampleImpl               = new SampleImpl(cswServices)
  lazy val sampleImplWrapper        = new JSampleImplWrapper(jSampleImpl)

  import actorRuntime.ec
  override lazy val routes: Route = new SampleRoute(sampleImpl, sampleImplWrapper, securityDirectives).route
}
