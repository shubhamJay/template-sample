name := "sample"

version := "0.1"

scalaVersion := "2.13.3"

resolvers += "jitpack" at "https://jitpack.io"
resolvers += "bintray" at "https://jcenter.bintray.com"
fork := true
fork in Test := false

libraryDependencies ++= Seq(
  Libs.`esw-http-template-wiring` % "compile->compile;test->test",
  Libs.`embedded-keycloak`        % Test,
  Libs.`scalatest`                % Test,
  Libs.`akka-http-testkit`        % Test,
  Libs.`mockito-scala`            % Test,
  Libs.`akka-actor-testkit-typed` % Test,
  Libs.`akka-stream-testkit`      % Test
)
