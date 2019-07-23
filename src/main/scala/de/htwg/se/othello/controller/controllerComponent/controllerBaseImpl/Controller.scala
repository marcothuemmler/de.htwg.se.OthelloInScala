package de.htwg.se.othello.controller.controllerComponent.controllerBaseImpl

import com.google.inject.{Guice, Injector}
import de.htwg.se.othello.OthelloModule
import de.htwg.se.othello.controller.controllerComponent.ControllerInterface
import de.htwg.se.othello.controller.controllerComponent.GameStatus._
import de.htwg.se.othello.model.boardComponent.boardBaseImpl.CreateBoardStrategy
import de.htwg.se.othello.model.boardComponent.BoardInterface
import de.htwg.se.othello.model.fileIOComponent.FileIOInterface
import de.htwg.se.othello.model.{Bot, Player}
import de.htwg.se.othello.util.UndoManager

import scala.concurrent.{ExecutionContext, Future}

class Controller extends ControllerInterface {

  val injector: Injector = Guice.createInjector(new OthelloModule)
  val fileIo: FileIOInterface = injector.getInstance(classOf[FileIOInterface])
  private val undoManager = new UndoManager
  var gameStatus: GameStatus = IDLE
  var difficulty = 2
  var board: BoardInterface = (new CreateBoardStrategy).createNewBoard(8)
  var players: Vector[Player] = Vector(Player(1), Bot(2))
  var player: Player = players(0)

  def resizeBoard(op: String): Unit = {
    player = players(0)
    op match {
      case "+" => createBoard(size + 2)
      case "-" => if (size > 4) createBoard(size - 2)
      case "." => if (size != 8) createBoard(8)
    }
  }

  def size: Int = board.size

  def createBoard(size: Int): Unit = {
    board = (new CreateBoardStrategy).createNewBoard(size)
    notifyObservers()
  }

  def setupPlayers: String => Unit = {
    case "0" => players = Vector(Bot(1), Bot(2))
    case "1" => players = Vector(Player(1), Bot(2))
    case "2" => players = Vector(Player(1), Player(2))
  }

  def moveSelector: MoveSelector = difficulty match {
    case 1 => new EasyBot(this)
    case 2 => new MediumBot(this)
    case 3 => new HardBot(this)
  }

  def setDifficulty(value: String): Unit = {
    difficulty = value match {
      case "e" => 1
      case "m" => 2
      case "d" => 3
    }
    gameStatus = DIFFICULTY_CHANGED
    notifyObservers()
  }

  def newGame: Future[Unit] = {
    createBoard(size)
    player = players(0)
    Future(selectAndSet())(ExecutionContext.global)
  }

  def save(): Unit = fileIo.save(board, player, difficulty)

  def load(): Unit = {
    fileIo.load match {
      case scala.util.Success(save) =>
        board = save._1
        player = save._2
        difficulty = save._3
        gameStatus = LOAD_SUCCESS
      case _ => gameStatus = LOAD_FAIL
    }
    notifyObservers()
  }

  def set(square: (Int, Int)): Unit = {
    if (!moves.exists(o => o._2.contains(square))) gameStatus = ILLEGAL
    else if (player.isBot) {
      moves.filter(o => o._2.contains(square)).keys.foreach(fromSquare =>
        board = board.flipLine(fromSquare, square, player.value))
      player = nextPlayer
    } else undoManager.doStep(new SetCommand(square, player.value, this))
    if (gameOver) gameStatus = GAME_OVER
    notifyObservers()
    if (!gameOver && moves.isEmpty) omitPlayer() else selectAndSet()
  }

  def selectAndSet(): Unit = if (player.isBot && !gameOver) {
    if (moves.nonEmpty) set(moveSelector.selection) else omitPlayer()
    selectAndSet()
  }

  def omitPlayer(): Unit = {
    player = nextPlayer
    gameStatus = OMITTED
    notifyObservers()
  }

  def undo(): Unit = {
    undoManager.undoStep()
    notifyObservers()
  }

  def redo(): Unit = {
    undoManager.redoStep()
    notifyObservers()
  }

  def highlight(): Unit = {
    board = board.changeHighlight(player.value)
    notifyObservers()
  }

  def suggestions: String = {
    options.map(o => (o._1 + 65).toChar.toString + (o._2 + 1)).mkString(" ")
  }

  def options: Seq[(Int, Int)] = moves.values.flatten.toSet.toList.sorted

  def moves: Map[(Int, Int), Seq[(Int, Int)]] = board.moves(player.value)

  def gameOver: Boolean = board.gameOver

  def nextPlayer: Player = if (player == players(0)) players(1) else players(0)

  def playerCount: Int = players.count(o => !o.isBot)

  def boardToString: String = board.toString

  def valueOf(col: Int, row: Int): Int = board.valueOf(col, row)

  def score: String = {
    val (win, lose) = (count(1) max count(2), count(1) min count(2))
    val winner = if (win == count(1)) players(0) else players(1)
    if (win != lose) f"$winner wins by $win:$lose!" else f"Draw. $win:$lose"
  }

  def count(value: Int): Int = board.count(value)
}
