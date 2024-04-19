import scala.util.Random


case class Card(color: String, number: Int) //value means the card is set

case class SkipBoCard(card: String)

private object SkipBoCard {
   def apply(): SkipBoCard = SkipBoCard("SkipBo")
}

 object CardFactory {
   def createRandomCard(): Card = {
     val randomNumber = generateCardNumber()
     val randomColor = generateCardColor()

     Card(randomColor, randomNumber)
   }
 }

 private def generateCardNumber(): Int = Random.shuffle(1 to 12).head

 private def generateCardColor(): String = {
   val colors = List("red", "blue", "green")
   Random.shuffle(colors).head
 }


