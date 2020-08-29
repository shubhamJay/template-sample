package sample.cli

import caseapp.{CommandName, HelpMessage}

sealed trait SampleCliCommand

object SampleCliCommand {

  @CommandName("start")
  final case class StartCommand(
      @HelpMessage("port on which the app is to be started with prefix of the app")
      port: Option[Int],
      prefix : Option[String]
  ) extends SampleCliCommand
}
