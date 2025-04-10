import model._
//this worksheet is used to test the methods from PlayerHand.scala

val redThree = NumberCard("red", 3)
val redSkip = ActionCard("red", "skip")
val redReverse = ActionCard("red", "reverse")
val wildDrawFour = WildCard("wild draw four")
val blueTwo = NumberCard("blue", 2)
val drawTwoBlue = ActionCard("blue","draw two")
val greenFour = NumberCard("green", 4)
val yellowSeven = NumberCard("yellow", 7)

// PlayerHands
var player1Hand = PlayerHand(List(redThree))
val player2Hand = PlayerHand(List(redReverse, wildDrawFour, redSkip))
val player3Hand = PlayerHand(List(redThree, redReverse, drawTwoBlue))

//--------------------- testing some methods ---------------------------------------------------------------------------

player1Hand.displayHand()  // expected: "red-5", "blue-draw two"
player1Hand.isEmpty        // expected: false

// adding a new card to the player1Hand
player1Hand = player1Hand + wildDrawFour
player1Hand.cards.length //expected: 2
player1Hand.removeCard(wildDrawFour).cards.length //expected: 1
player1Hand.hasUno //expected: true
player1Hand.sayUno()
player1Hand.resetUnoStatus()

