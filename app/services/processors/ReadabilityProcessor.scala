package services.processors

import com.rometools.rome.feed.synd.{SyndContentImpl, SyndEntry}
import de.jetwick.snacktory.HtmlFetcher
import play.api.http.MimeTypes

import scala.collection.JavaConverters._

/**
 * Processor fetches original entry content and passes it through snacktory readability-like service
 */
class ReadabilityProcessor extends EntriesProcessor {

  override def processEntries(entries: Seq[SyndEntry]): Seq[SyndEntry] = {
    entries.map(processEntry)
  }

  def processEntry(entry: SyndEntry): SyndEntry = {
    val fetcher = new HtmlFetcher()

    val result = fetcher.fetchAndExtract(entry.getUri, 1000, true)

    entry.setTitle(result.getTitle)

    val b = new StringBuilder

    val images = result.getImages.asScala
    if (images.nonEmpty) {
      b ++= "<img src=\"" ++= images.head.src ++= "\"/>"
    }

    result.getText.split("\r").map(s => s"<p>$s</p>").foreach(s => b ++= s)

    val description = new SyndContentImpl
    description.setType(MimeTypes.HTML)
    description.setValue(b.toString())

    entry.setDescription(description)

    entry
  }

}
