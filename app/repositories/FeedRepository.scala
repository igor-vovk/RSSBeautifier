package repositories

import com.google.inject.Inject
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONObjectID

case class Feed(id: Option[BSONObjectID] = None, url: String, content: String, createdAt: Long)

class FeedRepository @Inject()(val reactiveMongoApi: ReactiveMongoApi) {

  def collection = reactiveMongoApi.db.collection[JSONCollection]("feeds")

  implicit object FeedFormat extends Format[Feed] {

    import play.modules.reactivemongo.json.BSONFormats._

    override def reads(j: JsValue): JsResult[Feed] = {
      JsSuccess(Feed(
        (j \ "_id").asOpt[BSONObjectID],
        (j \ "url").as[String],
        (j \ "content").as[String],
        (j \ "createdAt").as[Long]
      ))
    }

    override def writes(o: Feed): JsValue = Json.obj(
      "_id" -> o.id,
      "url" -> o.url,
      "content" -> o.content,
      "createdAt" -> o.createdAt
    )
  }

}