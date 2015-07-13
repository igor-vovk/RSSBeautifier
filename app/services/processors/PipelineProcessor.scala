package services.processors

import com.rometools.rome.feed.synd.SyndFeed

import scala.concurrent.{ExecutionContext, Future}

/**
 * Processor combines all underlying processors in single processing pipeline
 */
class PipelineProcessor(pipeline: AsyncProcessor*) extends AsyncProcessor {

  override def processAsync(feed: SyndFeed)(implicit ec: ExecutionContext): Future[SyndFeed] = {
    pipeline.foldLeft(Future.successful(feed)) { case (fut, proc) =>
      fut.flatMap(proc.processAsync)(ec)
    }
  }

}
