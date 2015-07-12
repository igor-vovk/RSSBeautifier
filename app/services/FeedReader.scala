package services

import java.io.StringReader

import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import play.api.libs.ws.WS
import services.cache.Store
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import scala.util.Success

class FeedReader(store: Store[String]) {

  def read(url: String): Future[SyndFeed] = {
    store.get(url) match {
      case Some(feed) =>
        Future.successful(buildSyndFeed(feed))
      case None =>
        WS.url(url).get()
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
