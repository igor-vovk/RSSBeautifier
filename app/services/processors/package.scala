package services

import com.rometools.rome.feed.synd.SyndFeed

import scala.concurrent.Future

package object processors {

  type Processor = SyndFeed => SyndFeed

  type AsyncProcessor = (SyndFeed) => Future[SyndFeed]

  implicit def syncToAsyncProcessor(p: Processor): AsyncProcessor = feed => Future.successful(p(feed))

}
