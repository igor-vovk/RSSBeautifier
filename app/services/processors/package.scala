package services

import com.rometools.rome.feed.synd.{SyndEntry, SyndFeed}

import scala.concurrent.Future

package object processors {

  type Processor = SyndFeed => SyndFeed
  type AsyncProcessor = (SyndFeed) => Future[SyndFeed]
  val pass: AsyncProcessor = Future.successful

  type MapF = SyndEntry => Future[SyndEntry]
  type FilterF = SyndEntry => Boolean

}
