package services.processors

import com.rometools.rome.feed.synd.SyndFeed

/**
 * Base trait for all feed processors
 */
trait Processor {

  def process(feed: SyndFeed): SyndFeed

}
