package de.htwg.se.othello.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec with Matchers {
  val player: Player = Player("Otto", 1)

  "A player" should {
    "have a toString method" in {
      player.toString should be("Otto")
    }
  }
  "A Player without name parameter" should {
    "have a default name" in {
      new Player(2).toString should be("White")
    }
  }
  "isBot" should {
    "return false if the player is a human player" in {
      player.isBot should be(false)
    }
    "return true if the player is a bot" in {
      new Bot(2).isBot should be(true)
    }
  }
  "toJson" should {
    "return a Json representation of the player" in {
      val playerJson = Player("John", 1).toJson
      (playerJson \ "name").as[String] should be("John")
      (playerJson \ "value").as[Int] should be(1)
      (playerJson \ "isBot").as[Boolean] should be(false)
    }
  }
}
