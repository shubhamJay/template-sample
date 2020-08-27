package sample.core

// todo: add sample model and (de)serialisation for it.
case class Box(a: String)

class SampleImpl {
  def public()   = "Hello!!!"
  def secured()  = "Secured Hello!!!"
  def secured1() = Box("Secured Hello!!!")
}
