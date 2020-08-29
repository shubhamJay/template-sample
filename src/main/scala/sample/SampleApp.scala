package sample

import caseapp.core.RemainingArgs
import caseapp.core.app.CommandApp
import csw.prefix.models.Prefix
import sample.cli.SampleCliCommand
import sample.cli.SampleCliCommand.StartCommand

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.control.NonFatal

// todo: not getting unregistered when kill control+C.
object SampleApp extends CommandApp[SampleCliCommand] {

  override def appName: String    = getClass.getSimpleName.dropRight(1) // remove $ from class name
  override def appVersion: String = "0.1.0-SNAPSHOT"
  override def progName: String   = "sample-app"

  override def run(command: SampleCliCommand, remainingArgs: RemainingArgs): Unit =
    command match {
      case StartCommand(port, prefixString) =>
      start(port, prefixString)
    }

  private def start(port: Option[Int], prefixString: Option[String]): Unit = {
    val prefix = prefixString.map(Prefix(_))
    val wiring = new SampleWiring(port, prefix)
    start(wiring)
  }

  private def start(wiring: SampleWiring): Unit = {
    import wiring._
    try {
      actorRuntime.startLogging(progName, appVersion)
      logger.debug("starting sample-app")
      Await.result(wiring.start(), 10.seconds)
      logger.info("sample app started")
    }
    catch {
      case NonFatal(ex) =>
      ex.printStackTrace()
        logger.error("sample-app crashed")
        exit(1)
    }
  }
}
