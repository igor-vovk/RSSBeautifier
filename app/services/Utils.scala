package services

import java.io.File
import java.security.MessageDigest

object Utils {

  def md5(text: String): String = {
    val digest = MessageDigest.getInstance("MD5")

    digest.digest(text.getBytes).map("%02x".format(_)).mkString
  }

  def recursiveListFiles(f: File): Array[File] = {
    val (dirs, files) = f.listFiles.span(_.isDirectory)

    dirs.flatMap(recursiveListFiles) ++ files
  }

}
