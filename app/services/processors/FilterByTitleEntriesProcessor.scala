package services.processors

import com.rometools.rome.feed.synd.SyndEntry



class FilterByTitleEntriesProcessor(titlePart: String) extends EntriesProcessor {

  override def processEntries(entries: Seq[SyndEntry]): Seq[SyndEntry] = {
    entries.filter(_.getTitle.contains(titlePart))
  }
}
