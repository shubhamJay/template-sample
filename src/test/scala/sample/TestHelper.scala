package sample

import csw.prefix.models.Subsystem

import scala.util.Random

object TestHelper {

  def randomFrom[T](values: List[T]): T = values(Random.nextInt(values.size))

  def randomSubsystem: Subsystem = TestHelper.randomFrom(Subsystem.values.toList)

}