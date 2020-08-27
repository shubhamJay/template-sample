package sample

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object SampleApp extends App {
  private val wiring = new SampleWiring()

  Await.result(wiring.start(), 10.seconds)
  // todo: not getting unregistered when kill control+C.
}
