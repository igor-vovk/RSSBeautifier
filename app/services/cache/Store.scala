package services.cache

trait Store[T] {

  def all: Seq[String]

  def get(key: String): Option[T]

  def has(key: String): Boolean

  def set(key: String, value: T): Unit

  def remove(key: String): Unit

}
