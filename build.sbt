name := "sample"

version := "0.1"

scalaVersion := "2.13.3"

resolvers += "jitpack" at "https://jitpack.io"
resolvers += "bintray" at "https://jcenter.bintray.com"

libraryDependencies ++= Seq(
  Libs.`esw-http-core`,
  Libs.`csw-aas-http`,
  Libs.`scalatest` % Test
)
