package de.htwg.se.othello.model.database.slick

import de.htwg.se.othello.model.database.Dao
import de.htwg.se.othello.model.boardComponent.BoardInterface
import de.htwg.se.othello.model.boardComponent.boardBaseImpl.CreateBoardStrategy
import play.api.libs.json.Json
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class Slick() extends Dao {

  val db = Database.forURL(
    url = "jdbc:mysql://127.0.0.1:3306/othello?serverTimezone=UTC",
    driver = "com.mysql.cj.jdbc.Driver",
    user = "root",
    password = "othello1"
  )

  val boardTable = TableQuery[BoardTable]
  val schema = boardTable.schema

  db.run(DBIO.seq(schema.createIfNotExists))

  override def save(board: BoardInterface): Unit = {
    val myArray: Array[Byte] = board.toJson.toString.getBytes
    val id: Int = 1
    db.run(DBIO.seq(boardTable.insertOrUpdate(id, myArray)))
  }

  override def load(): BoardInterface = {
    val id: Int = Await.result(db.run(boardTable.length.result), Duration.Inf)
    val tableQuery = boardTable.take(id).sortBy(_.id)

    val queryResult = db.run(tableQuery.result)
    val boardJson = Await.result(queryResult, Duration.Inf).reverse.map {
      case (_, boardJson) => Json.parse(boardJson)
    }.head
    (new CreateBoardStrategy).fill(boardJson)
  }
}
