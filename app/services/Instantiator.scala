package services

object Instantiator {

  def singleton[T](underlying: => T): Instantiator[T] = new Instantiator[T] {
    override lazy val instance: T = underlying
  }

  def factory[T](factoryMethod: => T): Instantiator[T] = new Instantiator[T] {
    override def instance: T = factoryMethod
  }

}

trait Instantiator[+T] {

  def instance: T

}
