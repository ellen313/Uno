import scala.util.Random

object Main {
  case class GameBoard(cards: List[Card],
    playerStacks: List[List[Card]],
    centerStack: List[Card],
    drawPile: List[Card],
    discardPile: List[Card]
    ) {
    def shuffle(): GameBoard = {
      GameBoard(Random.shuffle(cards), playerStacks, centerStack, drawPile, discardPile)
    }
    def draw(): (Card, GameBoard) = {
      val drawnCard = drawPile.head //draw first card from pile
      val updatedDrawPile = drawPile.tail //tail has all elements except the first in its list; returns list after the first element
      val updatedGameBoard = copy(drawPile = updatedDrawPile) //actualize gameboard
      (drawnCard, updatedGameBoard) // return tuple
    }
    def getDrawPile: List[Card] = drawPile
  }

  case class PlayerHand(cards: List[Card]) {}

  case class GameState(
      players: List[PlayerHand],
      gameBoard: GameBoard,
      currentPlayerIndex: Int
  )

  /** Function to print the game board */
  /*def printGameBoard(gameState: GameState): Unit = {
    println("Player Hand:")
    for (card <- gameState.players.head.cards) {
      // print color and number
      println(card.color + "-" + card.number)
    }

    println("\nPlayer Stacks:")
    for (stack <- gameState.gameBoard.playerStacks) {
      // run through the cards in each playerstack
      for (card <- stack) {
        // print color and number
        println(card.color + "-" + card.number)
      }
      // separate player stacks
      println("---")
    }

    println("\nCenter Stack:")
    for (card <- gameState.gameBoard.centerStack) {
      println(card.color + "-" + card.number)
    }
    println("\nDraw Pile:")
    for (card <- gameState.gameBoard.drawPile) {
      println(card.color + "-" + card.number)
    }
    println("\nDiscard Pile:")
    for (card <- gameState.gameBoard.discardPile) {
      println(card.color + "-" + card.number)
    }
  }*/

  // main method
  def main(args: Array[String]): Unit = {
    val playerHand = PlayerHand(List(Card("red", 3), Card("green", 5)))
    val playerStacks =
      List(Card("blue", 8), Card("red", 10))
    val centerStack = List(List(Card("green", 1), Card("red", 6)))
    val drawPile = List(Card("blue", 5))
    val discardPile = List(Card("red", 7))

    val gameBoard = GameBoard(playerStacks, centerStack, drawPile, discardPile)
    val gameState =
      GameState(List(playerHand), gameBoard, currentPlayerIndex = 0)

    //printGameBoard(gameState)
  }
}
