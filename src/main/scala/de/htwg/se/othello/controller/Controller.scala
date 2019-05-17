package de.htwg.se.othello.controller

import de.htwg.se.othello.controller.GameStatus._
import de.htwg.se.othello.model.{Board, Bot, Player}
import de.htwg.se.othello.util.{Observable, UndoManager}

import scala.util.Random.nextInt

class Controller(var board: Board, var p: Vector[Player]) extends Observable {

  var player: Player = p(0)
  var gameStatus: GameStatus = IDLE
  private val undoManager = new UndoManager
  var boardList: Vector[Board] = Vector.empty

  def this(p: Vector[Player]) = this(new Board, p)

  def setupPlayers(number: String): Unit = {
    number match {
      case "0" => p = Vector(new Bot(1), new Bot(2))
      case "1" => p = Vector(new Player(1), new Bot(2))
      case "2" => p = Vector(new Player(1), new Player(2))
    }
  }

  def newGame(): Unit = {
    board = new Board
    player = p(0)
    notifyObservers()
    if (player.isInstanceOf[Bot]) selectAndSet()
  }

  def nextPlayer: Player = if (player == p(0)) p(1) else p(0)

  def selectAndSet(): Unit = {
    if (!board.gameOver && player.isInstanceOf[Bot]) {
      Thread.sleep(0)
      select match {
        case Some(selection) => set(selection)
        case None =>
          player = nextPlayer
          gameStatus = OMITTED
          notifyObservers()
      }
      selectAndSet()
    }
  }

  def set(square: (Int, Int)): Unit = {
    boardList = boardList :+ board
    undoManager.doStep(new SetCommand(square, player.value, this))
    if (board.gameOver) gameStatus = GAME_OVER
    notifyObservers()
  }

  def redo(): Unit = {
    undoManager.redoStep()
    notifyObservers()
  }

  def undo(): Unit = {
    undoManager.undoStep()
    notifyObservers()
  }

  def highlight(): Unit = {
    if (!board.isHighlighted) {
      for {
        (col, row) <- moves.values.flatten
      } board = board.highlight(col, row)
    } else board = board.deHighlight
    notifyObservers()
  }

  def select: Option[(Int, Int)] = {
    try {
      val move = moves.toList(nextInt(moves.keySet.size))
      Some(move._2(nextInt(move._2.size)))
    } catch {
      case _: IllegalArgumentException => None
    }
  }

  def mapToBoard(input: String): (Int, Int) = {
    (input(0).toUpper.toInt - 65, input(1).asDigit - 1)
  }

  def suggestions: String = {
    (for {
      (col, row) <- moves.values.flatten.toSet.toList.sorted
    } yield (col + 65).toChar.toString + (row + 1)).mkString(" ")
  }

  def moves: Map[(Int, Int), Seq[(Int, Int)]] = board.moves(player.value)

  def boardToString: String = board.toString
}
