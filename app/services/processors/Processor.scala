package services.processors

import com.rometools.rome.feed.synd.SyndFeed

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

trait Processor extends AsyncProcessor {


  override def processAsync(feed: SyndFeed)(implicit ec: ExecutionContext): Future[SyndFeed] = {
    try {
      Future.successful(process(feed))
    } catch {
      case NonFatal(e) => Future.failed(e)
    }
  }

  def process(feed: SyndFeed): SyndFeed

}
