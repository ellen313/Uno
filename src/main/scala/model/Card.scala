package model
import scala.util.Random

sealed trait Card{def color: String} //Card as superclass 

val colors = List("red", "blue", "green", "yellow")

//different card types as subclasses
case class NumberCard(color: String, number: Int) extends Card
case class ActionCard(color: String, action: String) extends Card
case class WildCard(action: String) extends Card {
  override def color: String = "wild"
}

object NumberCard{

  def createNumberCard():
    NumberCard = {
    val randomColor = Random.shuffle(colors).head
    val randomNumber =  Random.nextInt(10) //0 to 9
    NumberCard(randomColor, randomNumber)
  }
}

object ActionCard{

  def createActionCard():
    ActionCard = {
    val randomColor = Random.shuffle(colors).head
    val actions = List("draw two", "reverse", "skip")
    val randomAction = Random.shuffle(actions).head
    ActionCard(randomColor, randomAction)
  }
}

object WildCard{

  def createWildCard():
  WildCard = {
    val actions = List("wild", "wild draw four") //wild: player can change color
    val randomAction = Random.shuffle(actions).head
    WildCard(randomAction)
  }
}
