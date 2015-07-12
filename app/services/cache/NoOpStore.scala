package services.cache

class NoOpStore[T] extends Store[T] {

  override def get(key: String) = Option.empty

  override def set(key: String, value: T) = {
    // Do nothing
  }

  override def remove(key: String) = {
    // Do nothing
  }

  override def has(key: String): Boolean = false

  override def all: Seq[String] = Seq.empty
}
