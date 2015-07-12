package services.cache

import java.io.{FileOutputStream, FileInputStream, File}

import com.google.inject.Inject
import services.Utils

import scala.reflect.ClassTag

case class FileStoreConfig(root: String) {
  lazy val rootFile = new File(root)
}

class FileStore[T: ClassTag] @Inject()(serializer: Serializer, config: FileStoreConfig) extends Store[T] {

  if (!config.rootFile.exists()) {
    config.rootFile.mkdir()
  }

  require(config.rootFile.isDirectory && config.rootFile.canWrite)

  override def all: Seq[String] = {
    Utils.recursiveListFiles(config.rootFile).map(pathToKey)
  }

  override def get(key: String): Option[T] = {
    val f = keyToPath(key)
    if (f.exists()) {
      Option(serializer.deserialize(new FileInputStream(f)))
    } else {
      Option.empty
    }
  }

  override def set(key: String, value: T): Unit = {
    val f = keyToPath(key)
    if (f.exists()) {
      f.delete()
    } else {
      f.getParentFile.mkdirs()
    }

    serializer.serialize(value, new FileOutputStream(f))
  }

  override def remove(key: String): Unit = {
    keyToPath(key).delete()
  }

  override def has(key: String): Boolean = {
    keyToPath(key).exists()
  }


  private def keyToPath(suff: String): File = {
    val path = Utils.md5(suff)
    // Split path on parts with 2-symbols, making directory-tree
    val pathParts = path.grouped(path.length / 2).map(p => s"/$p").mkString + ".cache"

    new File(config.rootFile.getAbsolutePath + pathParts)
  }

  private def pathToKey(file: File): String = {
    config.rootFile.getAbsolutePath.replace(file.getAbsolutePath, "").replace("/", "")
  }
}
