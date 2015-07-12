package controllers

import java.net.URL

import com.rometools.rome.io.{SyndFeedOutput, XmlReader, SyndFeedInput}
import play.api._
import play.api.mvc._
import services.processors._

class FeedController extends Controller {

  val beautify = Action { request =>
    val url = request.getQueryString("url").map(new URL(_)).get

    val input = new SyndFeedInput()
    val output = new SyndFeedOutput()

    val feed = input.build(new XmlReader(url))

    val pipeline = new PipelineProcessor(Seq(
    ))

    Ok(output.outputString(pipeline.process(feed))).withHeaders(
      CONTENT_TYPE -> "text/xml; charset=UTF-8"
    )
  }

}
