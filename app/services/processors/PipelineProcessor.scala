package services.processors

import com.rometools.rome.feed.synd.SyndFeed

/**
 * Processor combines all underlying processors in single processing pipeline
 */
class PipelineProcessor(pipeline: Seq[Processor]) extends Processor {

  override def process(feed: SyndFeed): SyndFeed = {
    pipeline.foldLeft(feed) { case (f, proc) => proc.process(f) }
  }

}
