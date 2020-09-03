package sample

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.http.scaladsl.server.Route
import csw.location.client.ActorSystemFactory
import esw.http.template.wiring.ServerWiring
import sample.core.{JSampleImpl, SampleImpl}
import sample.http.SampleRoute

class SampleWiring(val port: Option[Int]) extends ServerWiring {
  lazy val actorSystem: ActorSystem[SpawnProtocol.Command] =
    ActorSystemFactory.remote(SpawnProtocol(), "sample-actor-system") //TODO giterify actor system name

  val jSampleImpl: JSampleImpl = new JSampleImpl(cswContext.asJava())

  lazy val sampleImpl = new SampleImpl(jSampleImpl, cswContext)
  import actorSystem.executionContext
  lazy val routes: Route = new SampleRoute(sampleImpl, securityDirectives).route
}
