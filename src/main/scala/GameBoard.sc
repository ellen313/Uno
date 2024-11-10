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
//==============================================================================
//test playCard - method

// Karten definieren
val redDrawTwo = ActionCard("red", "draw two")
val greenSkip = ActionCard("green", "skip")
val yellowReverse = ActionCard("yellow", "reverse")
val wildDrawFour = WildCard("wild draw four")
val redNumberCard3 = NumberCard("red", 3)


// Spielerhände definieren
val player1Hand = PlayerHand(List(redNumberCard3, redDrawTwo))
val player2Hand = PlayerHand(List(greenSkip, yellowReverse, wildDrawFour))
val player3Hand =  PlayerHand(List(redNumberCard3, yellowReverse, wildDrawFour))
// Spielfeld und Spielstatus definieren
val initialDeck = List(redDrawTwo, redNumberCard3, wildDrawFour, NumberCard("green", 2), greenSkip, NumberCard("blue", 2), wildDrawFour)
val initialGameBoard = GameBoard(initialDeck, List(NumberCard("red", 3)))
val initialGameState = GameState(List(player1Hand, player2Hand, player3Hand), initialGameBoard, 0, initialGameBoard.drawPile)

// Aktuelle Karte auf dem Ablagestapel prüfen
val topCard = initialGameState.gameBoard.discardPile.lastOption
initialGameState.currentPlayerIndex //expected: 0 (Player 1 )
initialGameState.players(1).cards.size //anzahl der Karten des Spielers 2
initialGameState.players(1).cards
initialGameState.gameBoard.drawPile


// Draw-Two-Karte testen
val stateAfterDrawTwo = initialGameBoard.playCard(redDrawTwo, initialGameState)
val isDrawTwoOnDiscardPile = stateAfterDrawTwo.gameBoard.discardPile.last == redDrawTwo //expected: true
val player1HandContainsDrawTwo = stateAfterDrawTwo.players(0).cards.contains(redDrawTwo)
val player2HandAfter = stateAfterDrawTwo.players(1).cards.size //expected: 5
stateAfterDrawTwo.gameBoard.drawPile
stateAfterDrawTwo.players(1).displayHand()

val currentPlayerIndexAfter = stateAfterDrawTwo.currentPlayerIndex //expected: 2 (Player 3)

stateAfterDrawTwo.gameBoard.discardPile.lastOption
stateAfterDrawTwo.players(2).displayHand()
stateAfterDrawTwo.players(0).displayHand()
stateAfterDrawTwo.players(2).cards.size //expected: 3
stateAfterDrawTwo.players(0).cards.size  //expected: 1
stateAfterDrawTwo.gameBoard.drawPile

// Teste die 'wild draw four' Karte
val stateAfterWildDrawFour = initialGameBoard.playCard(wildDrawFour, stateAfterDrawTwo)
val currentPlayerIndexAfterWildDrawFour = stateAfterWildDrawFour.currentPlayerIndex //expected: 0 (Player 1)
val player3HandAfterWildDrawFour = stateAfterWildDrawFour.players(2).cards.size //expected: 2
val player1HandAfterWildDrawFour = stateAfterWildDrawFour.players(0).cards.size //expected: 5

stateAfterWildDrawFour.players(0).displayHand()
stateAfterWildDrawFour.gameBoard.drawPile
stateAfterWildDrawFour.currentPlayerIndex

// Teste die 'skip' Karte
val stateAfterSkip = initialGameBoard.playCard(greenSkip, stateAfterWildDrawFour)
stateAfterSkip.currentPlayerIndex









//=============================================================================================
val testHand = PlayerHand(List(numCard, actionCard))
val testGameBoard = GameBoard(List.empty[Card], List.empty[Card])
val testGameState = GameState(List(testHand), testGameBoard, 0, List(numCard, actionCard, wildCard))

//test isValidPlay - method
val test1 = testGameBoard.isValidPlay(NumberCard("red", 5), Some(NumberCard("red", 3)))  // true
val test2 = testGameBoard.isValidPlay(NumberCard("red", 5), Some(NumberCard("blue", 5)))  // true
val test3 = testGameBoard.isValidPlay(NumberCard("red", 5), Some(ActionCard("blue", "skip")))  // false
val test4 = testGameBoard.isValidPlay(WildCard("wild draw four"), Some(ActionCard("blue", "draw two")))  //true
val test5 = testGameBoard.isValidPlay(WildCard("wild draw four"), Some(WildCard("wild draw four")))  //false

//test nextPlayer - method
val players = List(player1Hand, player2Hand, player3Hand) //3 players
val testGameState2 = GameState(players, gameBoard1, 2, gameBoard1.drawPile)

println(s"Start: current player-index: ${testGameState2.currentPlayerIndex}")

val stateAfterNext = testGameState2.nextPlayer(testGameState2)
println(s"after using method nextPlayer: current player-index: ${stateAfterNext.currentPlayerIndex}") // expected: 0

// reverse
val reversedState = stateAfterNext.copy(isReversed = true)
val stateAfterReverseNext = reversedState.nextPlayer(reversedState)
println(s"after using reversed method nextPlayer: current player-index: ${stateAfterReverseNext.currentPlayerIndex}") // expected: 0








