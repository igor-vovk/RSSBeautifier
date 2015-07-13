package services

import java.io.StringReader

import com.google.inject.Inject
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WSClient
import services.cache.Store

import scala.concurrent.Future
import scala.util.Success

class FeedReader @Inject()(store: Store[String], client: WSClient) {

  def read(url: String): Future[SyndFeed] = {
    store.get(url) match {
      case Some(feed) =>
        Future.successful(buildSyndFeed(feed))
      case None =>
        client.url(url).get()
          .map(r => (r.body, buildSyndFeed(r.body)))
          .andThen { case Success((f, _)) => store.set(url, f) }
          .map(_._2)
    }
  }

  def buildSyndFeed(content: String): SyndFeed = {
    val input = new SyndFeedInput()
    input.build(new StringReader(content))
  }

}
