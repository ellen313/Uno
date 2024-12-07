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

//====================================== test hasUno and resetUno in playCard ==========================================

val redFive = NumberCard("red", 5)
val greenTwo = NumberCard("green", 2)
val blueSeven = NumberCard("blue", 7)
val yellowThree = NumberCard("yellow", 3)
val redSkip2 = ActionCard("red", "skip")
val wildDrawFour2 = WildCard("wild draw four")

// PlayerHands
val player1 = PlayerHand(List(redFive, greenTwo))
val player2 = PlayerHand(List(blueSeven, yellowThree))
val player3 = PlayerHand(List(NumberCard("yellow", 5), redSkip))
val player4 = PlayerHand(List(NumberCard("blue", 4), wildDrawFour2))
val player5 = PlayerHand(List(NumberCard("blue", 4)))


// GameBoard
val initialDiscardPile = List(NumberCard("red", 9))
val initialDrawPile = List(NumberCard("green", 8), NumberCard("blue", 6),greenTwo, yellowThree,redSkip)
val gameBoard = GameBoard(initialDrawPile, initialDiscardPile)

// GameState
var gameStatus = GameState(
  players = List(player1, player2, player3, player4, player5),
  gameBoard = gameBoard,
  currentPlayerIndex = 0,
  allCards = initialDrawPile ++ initialDiscardPile ++ player1.cards ++ player2.cards ++ player3.cards ++ player4.cards
)

println("Initial GameState:")
gameStatus.players.zipWithIndex.foreach { case (hand, index) =>
  println(s"Player ${index + 1} Hand: ${hand.cards}")
}
println(s"Top of Discard Pile: ${gameStatus.gameBoard.discardPile.lastOption.getOrElse("None")}\n")

//----------------- test a simple Card ----------------------
val normalCard = redFive
println(s"Player 1 plays: $normalCard")
val gameStatusNormalCard = gameStatus.gameBoard.playCard(normalCard, gameStatus)

println("\nAfter Player 1 plays:")
gameStatusNormalCard.players.zipWithIndex.foreach { case (hand, index) =>
  println(s"Player ${index + 1} Hand: ${hand.cards}")
}
println(s"Top of Discard Pile: ${gameStatusNormalCard.gameBoard.discardPile.lastOption.getOrElse("None")}")
println(s"Player 1 Uno Status: ${gameStatusNormalCard.players.head.hasUno}") //expected: true
println(s"Player 1 has said Uno: ${gameStatusNormalCard.players.head.hasSaidUno}\n") //expected: true

//--------------- test ActionCard (RedSkip) --------------------
println(s"Player 3 plays: $redSkip2")
val gameStatusRedSkip = gameStatus
  .copy(currentPlayerIndex = 2)
  .gameBoard
  .playCard(redSkip2, gameStatus.copy(currentPlayerIndex = 2))

println("\nAfter Player 3 plays:")
gameStatusRedSkip.players.zipWithIndex.foreach { case (hand, index) =>
  println(s"Player ${index + 1} Hand: ${hand.cards}")
}
println(s"Top of Discard Pile: ${gameStatusRedSkip.gameBoard.discardPile.lastOption.getOrElse("None")}")
println(s"Player 3 Uno Status: ${gameStatusRedSkip.players(2).hasUno}") //expected: true
println(s"Player 3 has said Uno: ${gameStatusRedSkip.players(2).hasSaidUno}\n") //expected: true

//--------------- test Wildcard and resetUnoStatus -----------------

println(s"Player 4 plays: $wildDrawFour2")

println("\nBefore Player 4 plays (hasUno status of Player 5):")
println(s"Player 5 Uno Status: ${gameStatus.players(4).hasUno}") //expected: true
println(s"Player 5 has said Uno: ${gameStatus.players(4).hasSaidUno}\n") //expected: false (because not set yet)

val gameStatusWildCard = gameStatus
  .copy(currentPlayerIndex = 3)
  .gameBoard
  .playCard(wildDrawFour2, gameStatus.copy(currentPlayerIndex = 3))

gameStatusWildCard.gameBoard.drawPile

println("\nAfter Player 4 plays:")
gameStatusWildCard.players.zipWithIndex.foreach { case (hand, index) =>
  println(s"Player ${index + 1} Hand: ${hand.cards}")
}
println(s"Top of Discard Pile: ${gameStatusWildCard.gameBoard.discardPile.lastOption.getOrElse("None")}")
println(s"Player 4 Uno Status: ${gameStatusWildCard.players(3).hasUno}") //expected: true
println(s"Player 4 has said Uno: ${gameStatusWildCard.players(3).hasSaidUno}\n") //expected: true
println(s"Player 5 Uno Status: ${gameStatusWildCard.players(4).hasUno}") //expected: false
println(s"Player 5 has said Uno: ${gameStatusWildCard.players(4).hasSaidUno}\n") //expected: false

//######################################################################################################################

val testHand = PlayerHand(List(redThree, redSkip))
val testGameBoard = GameBoard(List.empty[Card], List.empty[Card])
val testGameState = GameState(List(testHand), testGameBoard, 0, List(redThree, redSkip, wildDrawFour))

//============================================= isValidPlay method =====================================================
val test1 = testGameBoard.isValidPlay(NumberCard("red", 5), Some(NumberCard("red", 3)))  ////expected: true
val test2 = testGameBoard.isValidPlay(NumberCard("red", 5), Some(NumberCard("blue", 5)))  ////expected: true
val test3 = testGameBoard.isValidPlay(NumberCard("red", 5), Some(ActionCard("blue", "skip")))  ////expected: false
val test4 = testGameBoard.isValidPlay(WildCard("wild draw four"), Some(ActionCard("blue", "draw two")))  ////expected: true
val test5 = testGameBoard.isValidPlay(WildCard("wild draw four"), Some(WildCard("wild draw four")))  ////expected: false








