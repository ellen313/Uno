//this worksheet is used to test the methods from GameBoard.scala

//============================================= playCard method ========================================================
val redThree = NumberCard("red", 3)
val redSkip = ActionCard("red", "skip")
val redReverse = ActionCard("red", "reverse")
val wildDrawFour = WildCard("wild draw four")
val blueTwo = NumberCard("blue", 2)
val drawTwoBlue = ActionCard("blue","draw two")
val greenFour = NumberCard("green", 4)
val yellowSeven = NumberCard("yellow", 7)

// PlayerHands
val player1Hand = PlayerHand(List(redThree, redSkip, blueTwo))
val player2Hand = PlayerHand(List(redReverse, wildDrawFour, redSkip))
val player3Hand = PlayerHand(List(redThree, redReverse, drawTwoBlue))

// GameBoard Piles
val drawPile1 = List(wildDrawFour, blueTwo, redThree, redSkip, greenFour, yellowSeven)
val discardPile1 = List(NumberCard("red", 3))

val allCards = drawPile1 ++ discardPile1 ++ player1Hand.cards ++ player2Hand.cards ++ player3Hand.cards

// GameBoard
val initialGameBoard = GameBoard(drawPile1, discardPile1)

// GameState with all three Players
val initialGameState = GameState(
  players = List(player1Hand, player2Hand, player3Hand),
  gameBoard = initialGameBoard,
  currentPlayerIndex = 0,
  allCards = allCards.distinct
)

// -------------------------- test skip --------------------------------------------------------------------------------
val currentPlayerIdx = initialGameState.currentPlayerIndex //expected: 0 (Player 1)
val stateAfterSkip = initialGameBoard.playCard(redSkip, initialGameState)
val currentPlayerAfterSKip = stateAfterSkip.currentPlayerIndex //expected: 2 (Player 3)

//checks
val skipOnDiscardPile = stateAfterSkip.gameBoard.discardPile.last == redSkip //expected: true
val skipOnPlayerHand = stateAfterSkip.players(0) == redSkip //expected: false

// -------------------------- test reverse -----------------------------------------------------------------------------
val currentPlayerIdx2 = stateAfterSkip.currentPlayerIndex //expected: 2 (Player 3)
val reversedStatus = stateAfterSkip.isReversed //expected: false

val stateAfterReverse = initialGameBoard.playCard(redReverse, stateAfterSkip)
val currentPlayerAfterReverse = stateAfterReverse.currentPlayerIndex //expected: 1 (Player 2)
val reversedStatus2 = stateAfterReverse.isReversed //expected: true

val reverseOnDiscardPile = stateAfterSkip.gameBoard.discardPile.last == redSkip //expected: true
val reverseOnPlayerHand = stateAfterSkip.players(2).containsCard(redSkip) //expected: false

// -------------------------- test draw four ---------------------------------------------------------------------------
stateAfterReverse.currentPlayerIndex
val cardNumBefore1 = stateAfterReverse.players(1).cards.length //expected: 3
val cardNumBefore0 = stateAfterReverse.players(0).cards.length //expected: 2 (Player who will draw)
val stateAfterDrawFour = initialGameBoard.playCard(wildDrawFour,stateAfterReverse)

val currentPlayerAfterDrawFour = stateAfterDrawFour.currentPlayerIndex //expected: 0
val cardNumAfter1 = stateAfterDrawFour.players(1).cards.length //expected: 2
val cardNumAfter0 = stateAfterDrawFour.players(0).cards.length //expected: 6 (Player who draws)

val drawFourOnDiscardPile = stateAfterDrawFour.gameBoard.discardPile.last == wildDrawFour //expected:true
val drawFourOnPlayerHand = stateAfterDrawFour.players(1).containsCard(wildDrawFour) //expected: false (Player who discarded)

// -------------------------- test other card --------------------------------------------------------------------------
stateAfterDrawFour.currentPlayerIndex //expected: 0
val cardNumBefore = stateAfterDrawFour.players(0).cards.length //expected: 6
val cardNumBefore2 = stateAfterDrawFour.players(2).cards.length //expected: 2

val stateAfterOtherCard = initialGameBoard.playCard(blueTwo,stateAfterDrawFour)

stateAfterOtherCard.currentPlayerIndex //expected: 2
val cardNumAfter = stateAfterOtherCard.players(0).cards.length //expected: 5
val twoBlueOnDiscardPile = stateAfterOtherCard.gameBoard.discardPile.last == blueTwo //expected: true

// -------------------------- test draw two ----------------------------------------------------------------------------
stateAfterOtherCard.currentPlayerIndex
val cardNumBefore20 = stateAfterOtherCard.players(2).cards.length //expected: 2
val cardNumBefore10 = stateAfterOtherCard.players(1).cards.length //expected: 2 (Player who will draw)
val stateAfterDrawTwo = initialGameBoard.playCard(drawTwoBlue,stateAfterOtherCard)

val currentPlayerAfterDrawTwo = stateAfterDrawTwo.currentPlayerIndex //expected: 1
val cardNumAfter20 = stateAfterDrawTwo.players(2).cards.length //expected: 1
val cardNumAfter10 = stateAfterDrawTwo.players(1).cards.length //expected: 4 (Player who draws)

val drawTwoOnDiscardPile = stateAfterDrawTwo.gameBoard.discardPile.last == drawTwoBlue //expected: true
val drawTwoOnPlayerHand = stateAfterDrawTwo.players(2).containsCard(drawTwoBlue) //expected: false

//######################################################################################################################
val testHand = PlayerHand(List(redThree, redSkip))
val testGameBoard = GameBoard(List.empty[Card], List.empty[Card])
val testGameState = GameState(List(testHand), testGameBoard, 0, List(redThree, redSkip, wildDrawFour))

//============================================= isValidPlay method ========================================================
val test1 = testGameBoard.isValidPlay(NumberCard("red", 5), Some(NumberCard("red", 3)))  ////expected: true
val test2 = testGameBoard.isValidPlay(NumberCard("red", 5), Some(NumberCard("blue", 5)))  ////expected: true
val test3 = testGameBoard.isValidPlay(NumberCard("red", 5), Some(ActionCard("blue", "skip")))  ////expected: false
val test4 = testGameBoard.isValidPlay(WildCard("wild draw four"), Some(ActionCard("blue", "draw two")))  ////expected: true
val test5 = testGameBoard.isValidPlay(WildCard("wild draw four"), Some(WildCard("wild draw four")))  ////expected: false








