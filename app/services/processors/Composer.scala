package services.processors

import com.rometools.rome.feed.synd.{SyndEntry, SyndFeedImpl, SyndFeed}
import scala.collection.JavaConverters._

class Composer {

  def compose(feeds: Seq[SyndFeed], resultingFeed: SyndFeed = new SyndFeedImpl()): SyndFeed = {
    val composedEntries = feeds.foldLeft(Seq.empty[SyndEntry]) { case (entries, feed) =>
      entries ++ feed.getEntries.asScala
    }

    resultingFeed.setEntries(composedEntries.asJava)

    resultingFeed
  }

}
