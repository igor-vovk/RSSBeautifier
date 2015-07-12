package services.cache

import java.io.{InputStream, OutputStream}

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}
import com.google.inject.{Inject, Singleton}
import services.Instantiator

import scala.reflect.ClassTag


@Singleton
class KryoSerializer @Inject()(kryo: Instantiator[Kryo]) extends Serializer {

  def serialize[T: ClassTag](obj: T, os: OutputStream): Unit = {
    val out = new Output(os)
    kryo.instance.writeClassAndObject(out, obj)
    out.close()
  }

  def deserialize[T: ClassTag](is: InputStream): T = {
    val in = new Input(is)
    val obj = kryo.instance.readClassAndObject(in)
    in.close()
    obj.asInstanceOf[T]
  }
}
