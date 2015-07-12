package modules

import com.esotericsoftware.kryo.Kryo
import com.google.inject.{Provider, AbstractModule}
import net.codingwell.scalaguice.ScalaModule

object KryoModule {

  class KryoProvider extends Provider[Kryo] {
    def get() = new Kryo()
  }

}

class KryoModule extends AbstractModule with ScalaModule {

  import KryoModule._

  def configure(): Unit = {
    bind[Kryo].toProvider[KryoProvider]
  }
}
