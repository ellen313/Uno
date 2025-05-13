import scala.annotation.tailrec
import scala.io.StdIn.readLine
import de.htwg.se.uno.aview.*
import de.htwg.se.uno.model.*
import de.htwg.se.uno.controller.GameBoard

object Main {
  def main(args: Array[String]): Unit = {
    val game = runUno()
    inputLoop(new UnoTui(game))
  }

  def runUno(numberPlayers: Option[Int] = None, cardsPerPlayer: Int = 7): GameState = {
    println("Welcome to UNO!")

    val players = numberPlayers.getOrElse(readValidInt("How many players? (2-10): ", min = 2, max = 10))

    val tempGameState = GameState(
      players = List.empty[PlayerHand],
      currentPlayerIndex = 0,
      allCards = List.empty[Card],
      isReversed = false,
      drawPile = List.empty[Card],
      discardPile = List.empty[Card]
    )

    // Initialize empty GameBoard, PlayerHands, and GameState
    GameBoard.resetInstance()
    val initialGameBoard = GameBoard(tempGameState, List.empty[Card], List.empty[Card])
    
    val playerHands = List.fill(players)(PlayerHand(List.empty[Card]))
    var gameState = GameState(players = playerHands,
      currentPlayerIndex = 0,
      allCards = initialGameBoard.drawPile,
      isReversed = false,
      drawPile = initialGameBoard.drawPile,
      discardPile = initialGameBoard.discardPile,
    )

    initialGameBoard.gameState = gameState

    gameState = gameState.dealInitialCards(cardsPerPlayer)
    println("Let's start the Game!")
    Thread.sleep(2000)

    val tui = new UnoTui(initialGameBoard)
    tui.display()

    gameState
  }

  @tailrec
  def inputLoop(tui: UnoTui): Unit = {

    if (tui.shouldExit) {
      println("Game over. GG!")
      System.exit(0)
      return
    }

    val input = readLine().trim
    input match {
      case "exit" =>
        println("Game exited.")
      case _ =>
        tui.handleCardSelection(input)
        if (!tui.shouldExit) inputLoop(tui)
        else {
            System.exit(0)
          }
    }
  }

  def readValidInt(prompt: String, min: Int, max: Int): Int = {
    var valid = false
    var number = 0
    while (!valid) {
      print(prompt)
      val input = readLine()
      try {
        val parsed = input.toInt
        if (parsed >= min && parsed <= max) {
          number = parsed
          valid = true
        } else {
          println(s"Please enter a number between $min and $max.")
        }
      } catch {
        case _: NumberFormatException =>
          println("Invalid input. Please enter a number.")
      }
    }
    number
  }
}
