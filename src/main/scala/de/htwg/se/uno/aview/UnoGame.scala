package de.htwg.se.uno.aview

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import de.htwg.se.uno.model.*
import de.htwg.se.uno.controller.GameBoard

object UnoGame {
  def runUno(numberPlayers: Option[Int] = None, cardsPerPlayer: Int = 7): GameBoard = {
    println("Welcome to UNO!")

    val players = numberPlayers.getOrElse(readValidInt("How many players? (2-10): ", min = 2, max = 10))

    // Initialize the game with a full shuffled deck
    val initialGameBoard = GameBoard(
      GameState(
        players = List.empty[PlayerHand],
        currentPlayerIndex = 0,
        allCards = List.empty[Card],
        isReversed = false,
        drawPile = List.empty[Card],
        discardPile = List.empty[Card],
        selectedColor = None
      ),
      drawPile = List.empty[Card],
      discardPile = List.empty[Card]
    ).shuffleDeck()

    // Deal cards to players
    val (newDrawPile, playerHands) = dealCards(initialGameBoard.drawPile, players, cardsPerPlayer)

    // Put first card to discard pile
    val (newDrawPileAfterFirstCard, firstCard) = newDrawPile match {
      case head :: tail => (tail, head)
      case Nil => throw new IllegalStateException("No cards left in draw pile")
    }

    val gameState = GameState(
      players = playerHands,
      currentPlayerIndex = 0,
      allCards = List.empty[Card],
      isReversed = false,
      drawPile = newDrawPileAfterFirstCard,
      discardPile = List(firstCard)
    )

    val gameBoard = new GameBoard(gameState, newDrawPileAfterFirstCard, List(firstCard))
    println("Let's start the Game!")
    Thread.sleep(2000)

    val tui = new UnoTui(gameBoard)
    tui.display()

    gameBoard
  }

  private def dealCards(drawPile: List[Card], playerCount: Int, cardsPerPlayer: Int): (List[Card], List[PlayerHand]) = {
    @tailrec
    def dealHelper(remainingPile: List[Card], players: List[PlayerHand], cardsToDeal: Int): (List[Card], List[PlayerHand]) = {
      if (cardsToDeal == 0) (remainingPile, players)
      else {
        val (newPile, newPlayers) = players.foldLeft((remainingPile, List.empty[PlayerHand])) {
          case ((pile, acc), player) =>
            pile match {
              case card :: rest =>
                (rest, acc :+ PlayerHand(player.cards :+ card))
              case Nil =>
                throw new IllegalStateException("Not enough cards in draw pile")
            }
        }
        dealHelper(newPile, newPlayers, cardsToDeal - 1)
      }
    }

    val initialPlayers = List.fill(playerCount)(PlayerHand(List.empty[Card]))
    dealHelper(drawPile, initialPlayers, cardsPerPlayer)
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
        tui.handleInput(input)
        if (!tui.shouldExit) inputLoop(tui)
        else {
          System.exit(0)
        }
    }
  }

  private def readValidInt(prompt: String, min: Int, max: Int): Int = {
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