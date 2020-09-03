package sample

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.http.scaladsl.server.Route
import csw.location.client.ActorSystemFactory
import esw.http.template.wiring.ServerWiring
import sample.core.SampleImpl
import sample.http.SampleRoute

class SampleWiring(val port: Option[Int]) extends ServerWiring {
  lazy val actorSystem: ActorSystem[SpawnProtocol.Command] =
    ActorSystemFactory.remote(SpawnProtocol(), "sample-actor-system") //TODO giterify actor system name
  lazy val sampleImpl = new SampleImpl(cswContext)
  import actorSystem.executionContext
  lazy val routes: Route = new SampleRoute(sampleImpl, securityDirectives).route
}
