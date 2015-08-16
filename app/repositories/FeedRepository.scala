package repositories

import com.google.inject.{Inject, Singleton}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONObjectID

import scala.concurrent.Future

case class ProcessorConfig(`type`: String, config: JsValue)

case class FeedConfig(sources: Seq[String], processors: Seq[ProcessorConfig])

case class Feed(id: BSONObjectID,
                config: FeedConfig = FeedConfig(Seq.empty, Seq.empty),
                createdAt: Long,
                acctok: String)

object FeedRepositoryJsonFormats {
  implicit val processorConfigFormat = OFormat[ProcessorConfig](
    (j: JsValue) => JsSuccess(ProcessorConfig(
      (j \ "type").as[String],
      (j \ "config").getOrElse(JsNull)
    )),
    (o: ProcessorConfig) => Json.obj(
      "type" -> o.`type`,
      "config" -> o.config
    )
  )

  implicit val feedConfigFormat = Json.format[FeedConfig]

  implicit val feedFormat = OFormat[Feed](
    (j: JsValue) => JsSuccess(Feed(
      (j \ "_id").as[BSONObjectID],
      (j \ "config").as[FeedConfig],
      (j \ "createdAt").as[Long],
      (j \ "acctok").as[String]
    )),
    (o: Feed) => Json.obj(
      "_id" -> o.id,
      "config" -> Json.toJson(o.config),
      "createdAt" -> o.createdAt,
      "acctok" -> o.acctok
    )
  )
}

@Singleton
class FeedRepository @Inject()(val reactiveMongoApi: ReactiveMongoApi) {

  import FeedRepositoryJsonFormats._

  lazy val collection = reactiveMongoApi.db.collection[JSONCollection]("feeds")

  def insert(feed: Feed) = collection.insert(feed)

  def update(feed: Feed)(implicit w: Writes[Feed]) = {
    val find = Json.obj("_id" -> feed.id)
    val modifier = Json.obj(
      "$set" -> feed
    )

    collection.update(find, modifier)
  }

  def find(id: BSONObjectID): Future[Option[Feed]] = {
    collection.find(Json.obj("_id" -> id)).cursor[Feed]().headOption
  }

}