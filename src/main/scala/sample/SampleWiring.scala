package sample

import akka.Done
import akka.actor.CoordinatedShutdown.UnknownReason
import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.typesafe.config.Config
import csw.aas.http.SecurityDirectives
import csw.location.api.models.{ComponentType, Metadata}
import csw.location.api.scaladsl.RegistrationResult
import csw.location.client.ActorSystemFactory
import csw.logging.api.scaladsl.Logger
import csw.prefix.models.Prefix
import csw.prefix.models.Subsystem.ESW
import esw.http.core.commons.ServiceLogger
import esw.http.core.wiring.{ActorRuntime, HttpService, Settings}
import sample.core.SampleImpl
import sample.http.SampleRoute

import scala.concurrent.Future

class SampleWiring {
  lazy val actorSystem: ActorSystem[SpawnProtocol.Command] = ActorSystemFactory.remote(SpawnProtocol(), "sample-app")
  lazy val config: Config                                  = actorSystem.settings.config
  lazy val logger: Logger                                  = new ServiceLogger(settings.httpConnection).getLogger
  // todo: fix port reading, subsystem and compoName
  lazy val settings     = new Settings(Some(8085), Some(Prefix(ESW, "sample_app")), config, ComponentType.Service)
  lazy val actorRuntime = new ActorRuntime(actorSystem)
  import actorRuntime.{ec, typedSystem}

  lazy val cswWiring = new CswWiring()
  import cswWiring.locationService

  lazy val securityDirectives: SecurityDirectives = SecurityDirectives(config, locationService)
  lazy val sampleImpl                             = new SampleImpl
  lazy val routes: Route                          = new SampleRoute(sampleImpl, securityDirectives).routes

  lazy val service = new HttpService(logger, locationService, routes, settings, actorRuntime)

  def start(): Future[(Http.ServerBinding, RegistrationResult)] =
    service.startAndRegisterServer(Metadata.empty) // todo : fix metadata

  def stop(): Future[Done] = actorRuntime.shutdown(UnknownReason)
}
