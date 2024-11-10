// testing the card class
val numCard = NumberCard("red", 5)
val actionCard = ActionCard("blue", "draw two")
val wildCard = WildCard("wild draw four")

numCard.color      // expected: "red"
numCard.number     // expected: 5
actionCard.action  // expected: "draw two"
wildCard.color     // expected: "wild"

//------------------------------------------

// testing the playerhand class
val playerHand1 = PlayerHand(List(numCard, actionCard))
playerHand1.displayHand()  // expected: "red-5", "blue-draw two"
playerHand1.hasUno         // expected: false
playerHand1.isEmpty        // expected: false

// adding a new card to the playerhand
val updatedHand1 = playerHand1.addCard(wildCard)
updatedHand1.displayHand()  // expected: "wild draw four", "red-5", "blue-draw two"
