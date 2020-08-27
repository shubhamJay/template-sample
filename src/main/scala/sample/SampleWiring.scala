package sample

import akka.Done
import akka.actor.CoordinatedShutdown.UnknownReason
import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.typesafe.config.Config
import csw.aas.http.SecurityDirectives
import csw.location.api.models.Metadata
import csw.location.api.scaladsl.{LocationService, RegistrationResult}
import csw.location.client.ActorSystemFactory
import csw.location.client.scaladsl.HttpLocationServiceFactory
import csw.prefix.models.Prefix
import csw.prefix.models.Subsystem.ESW
import esw.http.core.wiring.{HttpService, ServerWiring}
import sample.core.SampleImpl
import sample.http.SampleRoutes

import scala.concurrent.Future

class SampleWiring {
  lazy val actorSystem: ActorSystem[SpawnProtocol.Command] = ActorSystemFactory.remote(SpawnProtocol(), "sample-app")
  lazy val config: Config                                  = actorSystem.settings.config
  // todo: fix port reading, subsystem and compoName
  lazy val wiring = new ServerWiring(Some(8085), Some(Prefix(ESW, "sample_app")), actorSystem)
  import wiring.actorRuntime.{ec, typedSystem}

  lazy val locationService: LocationService       = HttpLocationServiceFactory.makeLocalClient
  lazy val securityDirectives: SecurityDirectives = SecurityDirectives(config, locationService)
  lazy val sampleImpl                             = new SampleImpl
  lazy val routes: Route                          = new SampleRoutes(sampleImpl, securityDirectives).routes

  lazy val service = new HttpService(wiring.logger, locationService, routes, wiring.settings, wiring.actorRuntime)

  def start(): Future[(Http.ServerBinding, RegistrationResult)] =
    service.startAndRegisterServer(Metadata.empty) // todo : fix metadata

  def stop(): Future[Done] = wiring.actorRuntime.shutdown(UnknownReason)
}
