import sbt._

object Libs {
  val `esw-http-template-wiring` = "com.github.tmtsoftware.esw" %% "esw-http-template-wiring" % "4e6293a"

  //testing
  val `scalatest` = "org.scalatest" %% "scalatest" % "3.1.2"
  val `akka-http-testkit` = "com.typesafe.akka" %% "akka-http-testkit" % "10.2.0"
  val `mockito-scala` = "org.mockito" %% "mockito-scala" % "1.14.8"
  val `akka-actor-testkit-typed` = "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.8"
  val `embedded-keycloak` = "com.github.tmtsoftware.embedded-keycloak" %% "embedded-keycloak" % "7fd5623"
}
