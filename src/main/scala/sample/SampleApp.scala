package sample

import caseapp.core.RemainingArgs
import csw.location.api.models.Metadata
import esw.http.template.wiring.ServerApp
import sample.SampleAppCommand.StartCommand

// todo: not getting unregistered when kill control+C.
object SampleApp extends ServerApp[SampleAppCommand] {
  override def run(command: SampleAppCommand, remainingArgs: RemainingArgs): Unit =
    command match {
      case StartCommand(port) =>
        val wiring = new SampleWiring(port)
        start(wiring, Metadata.empty)
    }
}
