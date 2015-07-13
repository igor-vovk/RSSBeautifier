package services.processors

import com.rometools.rome.feed.synd.SyndFeed

import scala.concurrent.{ExecutionContext, Future}

/**
 * Base trait for all feed processors
 */
trait AsyncProcessor {

  def processAsync(feed: SyndFeed)(implicit ec: ExecutionContext): Future[SyndFeed]

}
