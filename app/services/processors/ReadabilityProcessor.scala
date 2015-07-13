package services.processors

import com.google.inject.Inject
import com.rometools.rome.feed.synd.{SyndContentImpl, SyndEntry}
import de.jetwick.snacktory.ArticleTextExtractor
import play.api.http.MimeTypes
import play.api.libs.ws.WSClient

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

/**
 * Processor fetches original entry content and passes it through snacktory readability-like service
 */
class ReadabilityProcessor @Inject() (extractor: ArticleTextExtractor, ws: WSClient) extends AsyncEntriesProcessor {

  override def processEntries(entries: Seq[SyndEntry])(implicit ec: ExecutionContext): Seq[Future[SyndEntry]] = {
    entries.map(processEntry)
  }

  def processEntry(entry: SyndEntry)(implicit ec: ExecutionContext): Future[SyndEntry] = {
    ws.url(entry.getUri).get().map(resp => {
      val result = extractor.extractContent(resp.body)

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
    })


    Future.successful(entry)
  }

}
