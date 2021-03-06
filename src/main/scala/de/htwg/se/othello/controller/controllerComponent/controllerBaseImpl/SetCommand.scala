package de.htwg.se.othello.controller.controllerComponent.controllerBaseImpl

import de.htwg.se.othello.model.Player
import de.htwg.se.othello.model.boardComponent.BoardInterface
import de.htwg.se.othello.util.Command

case class SetCommand(toSquare: (Int, Int))(implicit val controller: Controller) extends Command {

  val player: Player = controller.currentPlayer
  implicit val playerValue: Int = player.value
  var board: BoardInterface = controller.getBoard
  val moves: Map[(Int, Int), Seq[(Int, Int)]] = controller.moves.filter(_._2.contains(toSquare))
  var memento: (BoardInterface, Player) = (board.deHighlight, player)

  override def doStep(): Unit = {
    moves.keys.foreach(fromSquare => board = board.flipLine(fromSquare, toSquare))
    controller.setBoard(board.deHighlight)
    controller.setCurrentPlayer(controller.nextPlayer)
  }

  override def undoStep(): Unit = step()

  override def redoStep(): Unit = step()

  def step(): Unit = {
    val new_memento = (controller.getBoard.deHighlight, controller.currentPlayer)
    controller.setBoard(memento._1)
    controller.setCurrentPlayer(memento._2)
    memento = new_memento
  }
}
