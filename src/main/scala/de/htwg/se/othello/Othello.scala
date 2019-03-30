package de.htwg.se.othello

object Othello {

  def main(args: Array[String]): Unit = {

    val game = new Board
    val player1 = Player("Player1", 1)
    val player2 = Player("Player2", 2)
    println(game)
    println(f"$player1%s \n$player2%s")
    game.flip(5, 3, player1)
    println(game)
  }
}
