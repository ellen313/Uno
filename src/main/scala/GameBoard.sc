//this worksheet is used to test the methods from GameBoard.scala

val numCard = NumberCard("red", 5)
val actionCard = ActionCard("blue", "draw two")
val wildCard = WildCard("wild draw four")
val playerHand1 = PlayerHand(List(numCard, actionCard))
val playerHand2 = playerHand1.addCard(wildCard)
val playerHand3 = PlayerHand(List(numCard, actionCard))

val gameBoard1 = GameBoard(List(numCard, actionCard, wildCard), List.empty[Card])
val (drawnCard1, updatedPlayerHand1, updatedBoard1) = gameBoard1.drawCard(playerHand1)

drawnCard1  // expected: NumberCard("red", 5)
updatedPlayerHand1.displayHand() // expected:  "red-5", "red-5", "blue-draw two"
updatedBoard1.drawPile.size // expected: 2 (because one card was drawn)

//test playCard - method
//alte version
val testHand = PlayerHand(List(numCard, actionCard))
val testGameBoard = GameBoard(List.empty[Card], List.empty[Card])
val testGameState = GameState(List(testHand), testGameBoard, 0, List(numCard, actionCard, wildCard))

val updatedState = testGameBoard.playCard(numCard, testGameState)
updatedState.players(0).displayHand()    // expected: "blue-draw two"
updatedState.gameBoard.discardPile       // expected: List(numCard)

//test isValidPlay - method
val test1 = testGameBoard.isValidPlay(NumberCard("red", 5), Some(NumberCard("red", 3)))  // true
val test2 = testGameBoard.isValidPlay(NumberCard("red", 5), Some(NumberCard("blue", 5)))  // true
val test3 = testGameBoard.isValidPlay(NumberCard("red", 5), Some(ActionCard("blue", "skip")))  // false
val test4 = testGameBoard.isValidPlay(WildCard("wild draw four"), Some(ActionCard("blue", "draw two")))  //true
val test5 = testGameBoard.isValidPlay(WildCard("wild draw four"), Some(WildCard("wild draw four")))  //false

//test nextPlayer - method
val players = List(playerHand1, playerHand2, playerHand3) //3 players
val testGameState2 = GameState(players, gameBoard1, 2, gameBoard1.drawPile)

println(s"Start: current player-index: ${testGameState2.currentPlayerIndex}")

val stateAfterNext = testGameState2.nextPlayer(testGameState2)
println(s"after using method nextPlayer: current player-index: ${stateAfterNext.currentPlayerIndex}") // expected: 1

// reverse
val reversedState = stateAfterNext.copy(isReversed = true)
val stateAfterReverseNext = reversedState.nextPlayer(reversedState)
println(s"after using reversed method nextPlayer: current player-index: ${stateAfterReverseNext.currentPlayerIndex}") // expected: 0








