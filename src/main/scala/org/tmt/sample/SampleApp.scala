package org.tmt.sample

import caseapp.core.RemainingArgs
import csw.location.api.models.Metadata
import esw.http.template.wiring.ServerApp
import SampleAppCommand.StartCommand

object SampleApp extends ServerApp[SampleAppCommand] {
  override def run(command: SampleAppCommand, remainingArgs: RemainingArgs): Unit =
    command match {
      case StartCommand(port) =>
        val wiring = new SampleWiring(port)
        start(wiring, Metadata.empty)
    }
}
