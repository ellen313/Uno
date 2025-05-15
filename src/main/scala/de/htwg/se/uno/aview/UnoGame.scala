package de.htwg.se.uno.aview

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import de.htwg.se.uno.model.*
import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.controller.command.UnoCalledCommand

object UnoGame {
  def runUno(numberPlayers: Option[Int] = None, cardsPerPlayer: Int = 7): UnoTui = {
    println("Welcome to UNO!")

    val players = numberPlayers.getOrElse(readValidInt("How many players? (2-10): ", min = 2, max = 10))

    val fullDeck = scala.util.Random.shuffle(GameBoard.createDeckWithAllCards())
    val (newDrawPile, playerHands) = dealCards(fullDeck, players, cardsPerPlayer)

    // Put first card to discard pile
    val (firstCard, remainingDrawPile) = newDrawPile match {
      case head :: tail => (head, tail)
      case Nil => throw new IllegalStateException("No cards left in draw pile")
    }

    val gameState = GameState(
      players = playerHands,
      currentPlayerIndex = 0,
      allCards = fullDeck,
      isReversed = false,
      drawPile = remainingDrawPile,
      discardPile = List(firstCard)
    )

    GameBoard.initGame(gameState)
    println("Let's start the Game!")
    Thread.sleep(2000)

    val tui = new UnoTui()
    tui.display()
    inputLoop(tui)
    tui
  }

  private def dealCards(drawPile: List[Card], playerCount: Int, cardsPerPlayer: Int): (List[Card], List[PlayerHand]) = {
    @tailrec
    def dealHelper(remainingPile: List[Card], players: List[PlayerHand], cardsToDeal: Int): (List[Card], List[PlayerHand]) = {
      if (cardsToDeal == 0) (remainingPile, players)
      else {
        val (newPile, newPlayers) = players.foldLeft((remainingPile, List.empty[PlayerHand])) {
          case ((pile, acc), player) =>
            pile match {
              case card :: tail =>
                (tail, acc :+ PlayerHand(player.cards :+ card))
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
    val currentState = GameBoard.gameState
    if (currentState.players.exists(_.cards.isEmpty)) {
      println("Game over. GG!")
      System.exit(0)
      return
    }

    val name = GameBoard.gameState.currentPlayerIndex
    val currentPlayer = GameBoard.gameState.players(GameBoard.gameState.currentPlayerIndex)
    if (currentPlayer.cards.length == 1 && !currentPlayer.hasSaidUno) {
      GameBoard.executeCommand(UnoCalledCommand(null))
      println(s"${name} said UNO!")
    }

    val input = readLine().trim
    input match {
      case "exit" =>
        println("Game exited.")
        System.exit(0)

      case _ =>
        tui.handleInput(input)

        tui.display()
        inputLoop(tui)
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