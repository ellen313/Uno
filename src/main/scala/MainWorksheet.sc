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

//------------------------------------------

// testing the gameboard
val gameBoard1 = GameBoard(List(numCard, actionCard, wildCard), List.empty[Card])
val (drawnCard1, updatedPlayerHand1, updatedBoard1) = gameBoard1.drawCard(playerHand1)

drawnCard1  // expected: NumberCard("red", 5)
updatedPlayerHand1.displayHand() // expected:  "red-5", "red-5", "blue-draw two"
updatedBoard1.drawPile.size // expected: 2 (because one card was drawn)

//------------------------------------------

//test playCard - method
val testHand = PlayerHand(List(numCard, actionCard))
val testGameBoard = GameBoard(List.empty[Card], List.empty[Card])
val testGameState = GameState(List(testHand), testGameBoard, 0, List(numCard, actionCard, wildCard))

val updatedState = testGameBoard.playCard(numCard, testGameState)
updatedState.players(0).displayHand()    // expected: "blue-draw two"
updatedState.gameBoard.discardPile       // expected: List(numCard)

//test isValidPlay - method
testGameBoard.isValidPlay(numCard,Some(wildCard)) // expected: true
testGameBoard.isValidPlay(numCard,Some(actionCard)) // expected: false
testGameBoard.isValidPlay(numCard,Some(numCard))

//test nextPlayer - method
val playerHand2 = PlayerHand(List(numCard, actionCard))
val players = List(playerHand1, updatedHand1, playerHand2) //3 players
val testGameState2 = GameState(players, gameBoard1, 2, gameBoard1.drawPile)

println(s"Start: current player-index: ${testGameState2.currentPlayerIndex}")

val stateAfterNext = testGameState2.nextPlayer(testGameState2)
println(s"after using method nextPlayer: current player-index: ${stateAfterNext.currentPlayerIndex}") // expected: 1

// reverse
val reversedState = stateAfterNext.copy(isReversed = true)
val stateAfterReverseNext = reversedState.nextPlayer(reversedState)
println(s"after using reversed method nextPlayer: current player-index: ${stateAfterReverseNext.currentPlayerIndex}") // expected: 0