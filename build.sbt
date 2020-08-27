name := "sample"

version := "0.1"

scalaVersion := "2.13.3"

resolvers += "jitpack" at "https://jitpack.io"
resolvers += "bintray" at "https://jcenter.bintray.com"

libraryDependencies ++= Seq(
  Csw.`csw-aas-http`,
  Csw.`csw-alarm-api`,
  Csw.`csw-alarm-client`,
  Csw.`csw-command-api`,
  Csw.`csw-command-client`,
  Csw.`csw-config-client`,
  Csw.`csw-event-api`,
  Csw.`csw-event-client`,
  Csw.`csw-time-scheduler`,
  Libs.`esw-http-core`,
  Libs.`scalatest`         % Test,
  Libs.`akka-http-testkit` % Test,
  Libs.`mockito-scala`     % Test
)
