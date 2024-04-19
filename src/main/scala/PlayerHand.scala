
case class PlayerHand(cards: List[Card])

  //add a card to the players hand
def addCard(card: Card): PlayerHand = {
    new PlayerHand(card :: cards) //add card to the head of list cards

    //val newCard = Card(createRandomCard())
    //playerHand.addCard(newCard)
}

  //remove a card from players hand
 def removeCard(card : Card): PlayerHand = {
    new PlayerHand(cards.filterNot(_ == card)) //check each element in list cards, if equals card ->remove
 }

  //if hand is empty
 def isEmpty: Boolean = cards.isEmpty

  //display cards on players hand
 def displayHand(): Unit = {
    println("Player's Hand: ")
    cards.foreach(card => println(s"${card.color}-${card.number}")) //function is applied to each element of the cards list
 }

