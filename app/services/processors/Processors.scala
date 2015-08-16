package services.processors

import com.rometools.rome.feed.synd.{SyndFeedImpl, SyndFeed, SyndEntry}

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object Processors {

  implicit def syncToAsyncProcessor(p: Processor): AsyncProcessor = feed => Future.successful(p(feed))

  implicit class AsyncProcessorOps(val proc: AsyncProcessor) extends AnyVal {

    /**
     * Combine with other processor
     */
    def ~(other: AsyncProcessor): AsyncProcessor = (feed) => proc(feed).flatMap(other)

  }

  def emptyFeed: SyndFeed = {
    val feed = new SyndFeedImpl()
    feed.setFeedType("atom_1.0")

    feed
  }

  @tailrec
  def combine(feeds: Seq[SyndFeed], result: SyndFeed = emptyFeed): SyndFeed = feeds match {
    case Nil => result
    case head :: Nil => head
    case head :: tail =>
      head.getEntries.asScala.foreach(result.getEntries.add)

      combine(tail, result)
  }

  def mapEntries(f: SyndEntry => SyndEntry): Processor = (feed) => {
    feed.setEntries(feed.getEntries.asScala.map(f).asJava)
    feed
  }

  def mapEntriesA(f: MapF): AsyncProcessor = (feed) => {
    Future.sequence(feed.getEntries.asScala.map(f)).map(e => {
      feed.setEntries(e.asJava)
      feed
    })
  }

  def filterEntries(f: FilterF): Processor = (feed) => {
    feed.setEntries(feed.getEntries.asScala.filter(f).asJava)
    feed
  }

  /**
   * Combine all underlying processors in single processor
   */
  def pipe(procs: AsyncProcessor*): AsyncProcessor = procs match {
    case Nil => pass
    case head :: Nil => head
    case head :: tail => head ~ pipe(tail: _*)
  }

}