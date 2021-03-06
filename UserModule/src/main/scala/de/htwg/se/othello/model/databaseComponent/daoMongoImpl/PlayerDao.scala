package de.htwg.se.othello.model.databaseComponent.daoMongoImpl

import de.htwg.se.othello.model.Player
import de.htwg.se.othello.model.databaseComponent.PlayerDaoInterface
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observer}
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.ReplaceOptions
import org.mongodb.scala.result.UpdateResult
import play.api.libs.json.{JsBoolean, Json}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class PlayerDao extends PlayerDaoInterface {

  val dbUrl: String = if (sys.env.contains("DOCKER_ENV")) "mongo" else "localhost"

  val mongoClient: MongoClient = MongoClient(s"mongodb://$dbUrl")

  val database: MongoDatabase = mongoClient.getDatabase("othello")

  val collection: MongoCollection[Document] = database.getCollection("players")

  override def save(currentPlayer: Player, otherPlayer: Player): Unit = {

    val current = Document((currentPlayer.toJson + ("currentPlayer" -> JsBoolean(true))).toString)
    val other = Document((otherPlayer.toJson + ("currentPlayer" -> JsBoolean(false))).toString)

    val insertCurrentObservable = collection.replaceOne(equal("currentPlayer", true), current, ReplaceOptions().upsert(true))
    val insertOtherObservable = collection.replaceOne(equal("currentPlayer", false), other, ReplaceOptions().upsert(true))
    val observer = new Observer[UpdateResult] {
      override def onNext(result: UpdateResult): Unit = println(s"inserted: $result")
      override def onError(e: Throwable): Unit = println(s"failed: $e")
      override def onComplete(): Unit = println("completed")
    }

    insertCurrentObservable.subscribe(observer)
    insertOtherObservable.subscribe(observer)
  }

  override def load(): Vector[Player] = {
    val documents = Await.result(collection.find().batchSize(2).toFuture, Duration.Inf)

    documents.map(document => {

      val playerJson = Json.parse(document.toJson)
      val player = Player.fromJson(playerJson)
      val index = if ((playerJson \ "currentPlayer").as[Boolean]) 0 else 1

      (index, player)

    }).sortBy{case (index, _) => index}
      .map{case (_, player) => player}
      .toVector
  }
}
