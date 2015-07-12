package services.cache

import java.io.{InputStream, OutputStream}

import scala.reflect.ClassTag

trait Serializer {

  def serialize[T: ClassTag](obj: T, out: OutputStream)

  def deserialize[T: ClassTag](in: InputStream): T

}
