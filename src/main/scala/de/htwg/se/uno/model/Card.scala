package de.htwg.se.uno.model

import scala.util.Random

sealed trait Card{def color: String} //Card as superclass 

val colors = List("red", "blue", "green", "yellow")

//different card types as subclasses
case class NumberCard(color: String, number: Int) extends Card
case class ActionCard(color: String, action: String) extends Card
case class WildCard(action: String) extends Card {
  override def color: String = "wild"
}

object Card {

  val colors = List("red", "blue", "green", "yellow")
  val actions = List("draw two", "reverse", "skip")
  val wildActions = List("wild", "wild draw four")

  def apply(kind: String) = kind match {
    case "number"  => createNumberCard()
    case "action"  => createActionCard()
    case "wild"    => createWildCard()
  }

  def createNumberCard(): NumberCard = {
    val randomColor = Random.shuffle(colors).head
    val randomNumber =  Random.nextInt(10)
    NumberCard(randomColor, randomNumber)
  }

  def createActionCard(): ActionCard = {
    val randomColor = Random.shuffle(colors).head
    val randomAction = Random.shuffle(actions).head
    ActionCard(randomColor, randomAction)
  }

  def createWildCard(): WildCard = {
    val randomAction = Random.shuffle(actions).head
    WildCard(randomAction)
  }
}
