package services.processors

import com.rometools.rome.feed.synd.{SyndEntry, SyndFeed}

import scala.collection.JavaConverters._


/**
 * Base trait for processors, that want to change feed entries
 */
trait EntriesProcessor extends Processor {

  def processEntries(entries: Seq[SyndEntry]): Seq[SyndEntry]

  override def process(feed: SyndFeed): SyndFeed = {
    feed.setEntries(processEntries(feed.getEntries.asScala.toSeq).asJava)

    feed
  }

}
