package de.htwg.se.othello.controller.controllerComponent

import de.htwg.se.othello.controller.controllerComponent.GameStatus.GameStatus
import de.htwg.se.othello.model.Player
import play.api.libs.json.JsValue

import scala.concurrent.Future
import scala.swing.Publisher

trait ControllerInterface extends Publisher {

  var gameStatus: GameStatus
  var difficulty: String
  def currentPlayer: Player
  def nextPlayer: Player
  def size: Int
  def suggestions: String
  def score: String
  def boardToString: String
  def boardToHtml: String
  def moves: Map[(Int, Int), Seq[(Int, Int)]]
  def gameOver: Boolean
  def canUndo: Boolean
  def canRedo: Boolean
  def playerCount: Int
  def count(value: Int): Int
  def valueOf(col: Int, row: Int): Int
  def newGame: Future[Unit]
  def illegalAction(): Unit
  def resizeBoard(op: String): Unit
  def setupPlayers: String => Unit
  def setDifficulty(value: String): Unit
  def set(square: (Int, Int)): Unit
  def undo(): Unit
  def redo(): Unit
  def highlight(): Unit
  def save(dirOption: Option[String]): Unit
  def load(dirOption: Option[String]): Unit
  def boardJson: JsValue
  def setPlayerName(index: Int, name: String): Unit

}
