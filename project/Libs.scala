import sbt._

object Csw {
  private val Org     = "com.github.tmtsoftware.csw"
  private val Version = "bcf2c8e"

  val `csw-aas-http`       = Org %% "csw-aas-http"       % Version
  val `csw-event-api`      = Org %% "csw-event-api"      % Version
  val `csw-event-client`   = Org %% "csw-event-client"   % Version
  val `csw-command-api`    = Org %% "csw-command-api"    % Version
  val `csw-command-client` = Org %% "csw-command-client" % Version
  val `csw-config-client`  = Org %% "csw-config-client"  % Version
  val `csw-alarm-api`      = Org %% "csw-alarm-api"      % Version
  val `csw-alarm-client`   = Org %% "csw-alarm-client"   % Version
  val `csw-time-scheduler` = Org %% "csw-time-scheduler" % Version
  val `csw-testkit`        = Org %% "csw-testkit"        % Version
}

object Libs {
  val `esw-http-core` = "com.github.tmtsoftware.esw" %% "esw-http-core" % "dde3414"

  val `borer-core`        = "io.bullet" %% "borer-core"        % "1.6.1"
  val `borer-derivation`  = "io.bullet" %% "borer-derivation"  % "1.6.1"
  val `borer-compat-akka` = "io.bullet" %% "borer-compat-akka" % "1.6.1"
  val `case-app`           = "com.github.alexarchambault" %% "case-app" % "2.0.3"

  //testing
  val `scalatest`                = "org.scalatest"     %% "scalatest"                % "3.1.2"
  val `akka-http-testkit`        = "com.typesafe.akka" %% "akka-http-testkit"        % "10.2.0"
  val `mockito-scala`            = "org.mockito"       %% "mockito-scala"            % "1.14.8"
  val `akka-actor-testkit-typed` = "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.8"
  val `embedded-keycloak` = "com.github.tmtsoftware.embedded-keycloak" %% "embedded-keycloak" % "7fd5623"
}
