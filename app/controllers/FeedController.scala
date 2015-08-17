package controllers

import java.util.UUID

import com.google.inject.Inject
import com.rometools.rome.io.SyndFeedOutput
import modules.KryoModule.KryoProvider
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{Reads, JsValue, JsError, Json}
import play.api.libs.ws.WSClient
import play.api.mvc._
import reactivemongo.bson.BSONObjectID
import repositories.{Feed, FeedConfig, FeedRepository}
import services.cache.{FileStore, FileStoreConfig, KryoSerializer}
import services.processors.ProcessorResolver
import services.processors.Processors._
import services.{FeedReader, Instantiator}

import scala.concurrent.Future

object FeedController {


}

class FeedController @Inject()(kryoProvider: KryoProvider,
                               ws: WSClient,
                               feedRepo: FeedRepository,
                               presolver: ProcessorResolver) extends Controller {

  import repositories.FeedRepositoryJsonFormats._


  val reader = new FeedReader(
    new FileStore[String](
      new KryoSerializer(Instantiator.factory(kryoProvider.get())),
      FileStoreConfig("/tmp/rss")
    ),
    ws
  )

  val output = new SyndFeedOutput()

  val preview = Action.async(parse.json) { request =>
    parseBody(request, mkFeed)
  }

  def feed(feedId: String) = Action.async {
    feedRepo.find(BSONObjectID(feedId)).flatMap {
      case Some(f) => mkFeed(f.config)
      case None => Future.successful(NotFound)
    }
  }

  val createFeed = Action.async {
    val feed = new Feed(
      id = BSONObjectID.generate,
      createdAt = System.currentTimeMillis() / 1000l,
      acctok = UUID.randomUUID().toString
    )

    feedRepo.insert(feed).map(_ => Ok(Json.obj(
      "id" -> feed.id.stringify,
      "acctok" -> feed.acctok
    )))
  }

  def feedConfig(feedId: String) = Action.async {
    feedRepo.find(BSONObjectID(feedId)).map {
      case Some(f) => Ok(Json.toJson(f.config))
      case None => NotFound
    }
  }

  def updateFeedConfig(feedId: String, acctok: String) = Action.async(parse.json) { request =>
    feedRepo.find(BSONObjectID(feedId))
      .map(_.toRight(NotFound))
      .map(_.right.map(f => Either.cond(f.acctok == acctok, f, Forbidden)).joinRight)
      .map(_.left.map(Future.successful))
      .map(_.right.map(feed => {
        parseBody(request, (fc: FeedConfig) => {
          feedRepo.update(feed.copy(config = fc)).map(_ => Ok(Json.obj("status" -> "OK")))
        })
      }))
      .flatMap(_.merge)
  }

  private def parseBody[T: Reads](req: Request[JsValue], succ: T => Future[Result]) = {
    req.body.validate[T].fold(
      errors => {
        Future.successful(BadRequest(Json.obj(
          "status" -> "KO",
          "message" -> JsError.toJson(errors)
        )))
      },
      succ
    )
  }

  private def mkFeed(fc: FeedConfig) = {
    val processFeed = pipe(fc.processors.map(presolver(_)): _*)

    Future.sequence(fc.sources.map(reader.read))
      .map(combine(_))
      .flatMap(processFeed)
      .map(feed => {
        Ok(output.outputString(feed)).withHeaders(
          CONTENT_TYPE -> "text/xml; charset=UTF-8"
        )
      })
  }

}
