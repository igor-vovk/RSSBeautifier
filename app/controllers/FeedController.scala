package controllers

import com.google.inject.Inject
import com.rometools.rome.io.SyndFeedOutput
import modules.KryoModule.KryoProvider
import play.api.libs.ws.WSClient
import play.api.mvc._
import services.{Instantiator, FeedReader}
import services.cache.{FileStoreConfig, KryoSerializer, FileStore}
import services.processors._
import play.api.libs.concurrent.Execution.Implicits._


class FeedController @Inject()(kryoProvider: KryoProvider, client: WSClient) extends Controller {

  val beautify = Action.async { request =>
    val url = request.getQueryString("url").get

    val reader = new FeedReader(
      new FileStore[String](
        new KryoSerializer(Instantiator.factory(kryoProvider.get())),
        FileStoreConfig("/tmp/rss")
      ),
      client
    )

    val pipeline = Processors.combine()

    val output = new SyndFeedOutput()
    reader.read(url).flatMap(pipeline).map(feed => {
      Ok(output.outputString(feed)).withHeaders(
        CONTENT_TYPE -> "text/xml; charset=UTF-8"
      )
    })
  }

}
