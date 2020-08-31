package sample.cli

import caseapp.{CommandName, ExtraName, HelpMessage}

sealed trait SampleCliCommand

object SampleCliCommand {

  @CommandName("start")
  final case class StartCommand(
      @HelpMessage("port of the app")
      @ExtraName("p")
      port: Option[Int],
      @HelpMessage("prefix of app. For eg: tcs.sample_app, etc")
      @ExtraName("s")
      prefix: Option[String]
  ) extends SampleCliCommand
}
