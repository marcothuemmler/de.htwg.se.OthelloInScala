package de.htwg.se.othello.controller.controllerComponent.controllerBaseImpl

import de.htwg.se.othello.controller.controllerComponent.BoardControllerInterface
import de.htwg.se.othello.model.boardComponent.BoardInterface
import de.htwg.se.othello.model.boardComponent.boardBaseImpl.CreateBoardStrategy
import play.api.libs.json.{JsObject, Json}

class BoardController extends BoardControllerInterface {

  var board: BoardInterface = (new CreateBoardStrategy).createNewBoard(8)

  def size: Int = board.size

  def resizeBoard(op: String): Unit = {
    op match {
      case "+" => createBoard(size + 2)
      case "-" => if (size > 4) createBoard(size - 2)
      case "." => if (size != 8) createBoard(8)
    }
  }

  def createBoard(size: Int): Unit = {
    board = (new CreateBoardStrategy).createNewBoard(size)
  }

  def changeHighlight(implicit value: Int): Unit = board = board.changeHighlight

  def moves(implicit value: Int): Map[(Int, Int), Seq[(Int, Int)]] = board.moves

  def gameOver: Boolean = board.gameOver

  def valueOf(col: Int, row: Int): Int = board.valueOf(col, row)

  def count(value: Int): Int = board.count(value)

  def boardToString: String = board.toString

  def boardToHtml: String = board.toHtml

  def toJson: JsObject = board.toJson

  def movesToJson(value: Int): JsObject = Json.obj( "values" -> Json.toJson(moves(value)))
}