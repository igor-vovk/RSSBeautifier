package modules

import com.google.inject.AbstractModule
import de.jetwick.snacktory.ArticleTextExtractor
import net.codingwell.scalaguice.ScalaModule

class SnacktoryModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[ArticleTextExtractor]
  }
}
