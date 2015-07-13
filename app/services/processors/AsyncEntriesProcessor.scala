package services.processors

import com.rometools.rome.feed.synd.{SyndEntry, SyndFeed}

import scala.concurrent.{Future, ExecutionContext}

import scala.collection.JavaConverters._

trait AsyncEntriesProcessor extends AsyncProcessor {

  def processEntries(entries: Seq[SyndEntry])(implicit ec: ExecutionContext): Seq[Future[SyndEntry]]

  override def processAsync(feed: SyndFeed)(implicit ec: ExecutionContext): Future[SyndFeed] = {
    Future.sequence(processEntries(feed.getEntries.asScala.toSeq)).map(e => {
      feed.setEntries(e.asJava)
      feed
    })
  }
}
