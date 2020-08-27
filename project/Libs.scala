import sbt._

object Libs {
  val `esw-http-core` = "com.github.tmtsoftware.esw" %% "esw-http-core" % "dde3414"
  val `csw-aas-http`  = "com.github.tmtsoftware.csw" %% "csw-aas-http"  % "bcf2c8e"

  //testing
  val `scalatest` = "org.scalatest" %% "scalatest" % "3.1.2"
}
