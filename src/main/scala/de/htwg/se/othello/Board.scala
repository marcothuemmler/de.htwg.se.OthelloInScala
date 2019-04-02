package de.htwg.se.othello

class Board {

  val field: Array[Array[Cell]] = Array.tabulate(8, 8)((i, j) => {
    if ((i == 4 || i == 3) && i == j) Cell(2)
    else if (i == 4 && j == 3 || i == 3 && j == 4) Cell(1) else Cell(0)
  })

  def isSet(x: Int, y: Int): Boolean = field(x)(y).isSet

  def getValue(x: Int, y: Int): Int = field(x)(y).value

  def flip(x: Int, y: Int, newVal: Int): Unit = field(x)(y) = Cell(newVal)

  override def toString: String = {
    val sb = new StringBuilder("\n    A B C D E F G H\n    _______________\n")
    for (i <- field.indices) {
      sb ++= (i + 1) + "  |"
      for (j <- field.indices)
        sb ++= field(j)(i).toString
      sb ++= "\n"
    }
    sb.append("    ⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺").toString
  }
}
