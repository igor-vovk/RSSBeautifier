package services.processors

import com.rometools.rome.feed.synd.SyndFeed

class SetTitleProcessor(title: String) extends Processor {
  override def process(feed: SyndFeed): SyndFeed = {
    feed.setTitle(title)
    feed
  }
}
