package services.processors

import com.rometools.rome.feed.synd.SyndEntry

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Processors {

  def entryMapper(map: SyndEntry => SyndEntry): Processor = (feed) => {
    feed.setEntries(feed.getEntries.asScala.map(map).asJava)
    feed
  }

  def entryMapperA(map: SyndEntry => Future[SyndEntry]): AsyncProcessor = (feed) => {
    Future.sequence(feed.getEntries.asScala.map(map)).map(e => {
      feed.setEntries(e.asJava)
      feed
    })
  }

  def entryFilter(filter: SyndEntry => Boolean): Processor = (feed) => {
    feed.setEntries(feed.getEntries.asScala.filter(filter).asJava)
    feed
  }

  /**
   * Combine all underlying processors in single pipeline
   */
  def combine(procs: AsyncProcessor*): AsyncProcessor = (feed) => {
    procs.foldLeft(Future.successful(feed)) { case (fut, proc) =>
      fut.flatMap(proc)
    }
  }

}
