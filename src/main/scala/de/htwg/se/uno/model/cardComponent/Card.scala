package de.htwg.se.uno.model.cardComponent

import scala.util.Random

sealed trait Card { def color: String }

val colors = List("red", "blue", "green", "yellow")

case class NumberCard(color: String, number: Int) extends Card
case class ActionCard(color: String, action: String) extends Card
case class WildCard(action: String) extends Card {
  override def color: String = "wild"
}

object Card {
  val colors: List[String] = List("red", "blue", "green", "yellow")
  val actions: List[String] = List("draw two", "reverse", "skip")
  val wildActions: List[String] = List("wild", "wild draw four")

  def apply(kind: String): Card = kind match {
    case "number" => createNumberCard()
    case "action" => createActionCard()
    case "wild"   => createWildCard()
  }

  private def createNumberCard(): NumberCard = {
    val randomColor = Random.shuffle(colors).head
    val randomNumber = Random.nextInt(10)
    NumberCard(randomColor, randomNumber)
  }

  private def createActionCard(): ActionCard = {
    val randomColor = Random.shuffle(colors).head
    val randomAction = Random.shuffle(actions).head
    ActionCard(randomColor, randomAction)
  }

  def createWildCard(): WildCard = {
    val randomAction = Random.shuffle(actions).head
    WildCard(randomAction)
  }
}
