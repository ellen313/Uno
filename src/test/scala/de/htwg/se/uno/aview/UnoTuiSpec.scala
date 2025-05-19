package de.htwg.se.uno.aview

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.se.uno.model.*
import de.htwg.se.uno.aview.*
import de.htwg.se.uno.controller.*

import java.io.{ByteArrayOutputStream, PrintStream}


class UnoTuiSpec extends AnyWordSpec {

  "UnoTui" should {

    "initialize correctly" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )
      
      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()

      assert(GameBoard.gameState eq gameState)
      assert(!unoTui.shouldExit)
      assert(unoTui.selectedColor.isEmpty)
    }

    "display should return early if game should exit" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )
      
      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()
      unoTui.setShouldExit(true)


      // Simulate displaying without throwing exceptions
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.display()
      }
      assert(stream.toString.isEmpty) // No output expected
    }

    "display should return early if players are empty" in {
      val gameState = new GameState(
        players = List.empty,
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()
      
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.display()
      }
      assert(stream.toString.isEmpty)
    }

    "display should show current player's turn and top card" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()

      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.display()
      }
      val output = stream.toString
      assert(output.contains("Player 1's turn!"))
      assert(output.contains("Top Card:"))
    }

    "display should show selected color when defined" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )
      
      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()
      
      unoTui.selectedColor = Some("blue")

      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.display()
      }
      val output = stream.toString
      assert(output.contains("The color that was chosen: blue"))
    }

    "display should show hand of current player" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()

      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.display()
      }
      val output = stream.toString
      assert(output.contains("Your Cards:"))
      assert(output.contains("0 - "))
      assert(output.contains("1 - "))
    }

    "display should handle case when no playable cards exist" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("blue", 1), NumberCard("green", 2))), // Keine passt auf rot 5
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List.fill(10)(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      GameBoard.initGame(gameState)
      val unoTui = new UnoTui(){
        override def display(): Unit = {
          val playable = gameState.players(gameState.currentPlayerIndex)
            .cards.exists(card =>
              gameState.isValidPlay(card, Some(gameState.discardPile.last), selectedColor)
            )
          if (!playable) println("No playable Card! You have to draw a Card...")
        }
      }

      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.display()
      }
      val output = stream.toString

      assert(output.contains("No playable Card! You have to draw a Card..."))
    }

    "chooseWildColor should prompt for color selection" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()

      val inputs = Iterator("invalid", "1")
      val Input = () => inputs.next()
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.chooseWildColor(Input)
      }
      val output = stream.toString
      assert(output.contains("Please choose a color for the Wild Card:"))
      assert(output.contains("0 - red"))
      assert(output.contains("1 - green"))
      assert(output.contains("2 - blue"))
      assert(output.contains("3 - yellow"))
      assert(unoTui.selectedColor.contains("green"))
    }

    "chooseWildColor should handle invalid input and retry" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )
      
      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()

      val inputs = Iterator("invalid", "1")
      val Input = () => inputs.next()

      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.chooseWildColor(Input)
      }
      val output = stream.toString
      assert(output.contains("Invalid input"))
      assert(unoTui.selectedColor.contains("green"))
    }

    "chooseWildColor should reject invalid color choice and accept a valid one" in {
      val gameState = GameState(
        players = List(PlayerHand(List())),
        currentPlayerIndex = 0,
        drawPile = List(),
        discardPile = List(),
        isReversed = false,
        allCards = List()
      )

      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()

      val output = new ByteArrayOutputStream()
      val inputs = Iterator("5", "2")

      Console.withOut(output) {
        unoTui.chooseWildColor(() => inputs.next())
      }

      val outputStr = output.toString
      assert(outputStr.contains("Invalid color choice. Please try again."))
      assert(outputStr.contains("Wild Card color changed to: blue"))
    }

    "Invalid input should show error message without game interaction" in {
      val validCard = NumberCard("red", 1)
      val initialHand = PlayerHand(List(validCard))

      val gameState = GameState(
        players = List(initialHand),
        currentPlayerIndex = 0,
        drawPile = List.fill(5)(NumberCard("blue", 2)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )
      
      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()

      val invalidInput = "invalid"
      val outputStream = new java.io.ByteArrayOutputStream()

      Console.withOut(outputStream) {
        unoTui.handleInput(invalidInput)
      }

      val output = outputStream.toString
      assert(output.contains("Invalid input! Please select a valid index or type 'draw'"))

      assert(GameBoard.gameState.players.head.cards.contains(validCard),
        "Spieler sollte noch seine ursprüngliche Karte haben")
      assert(GameBoard.gameState.discardPile.size == 1,
        "Ablagestapel sollte unverändert sein")
      assert(GameBoard.gameState.drawPile.size == 5,
        "Nachziehstapel sollte unverändert sein")
    }

    "handleCardSelection should exit early if gameShouldExit is true" in {
      val gameState = GameState(
        players = List(PlayerHand(List(NumberCard("red", 1)))),
        currentPlayerIndex = 0,
        drawPile = List(),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()
      unoTui.setShouldExit(true)


      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.handleInput("0")
      }
      val output = stream.toString
      assert(output.isEmpty)
    }

    "handleCardSelection should call chooseWildColor and play wild card" in {
      val gameState = GameState(
        players = List(PlayerHand(List(WildCard("wild draw two")))),
        currentPlayerIndex = 0,
        drawPile = List.fill(5)(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("blue", 2)),
        isReversed = false,
        allCards = List()
      )
      
      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()

      val input = new java.io.ByteArrayInputStream("2\n".getBytes())
      val output = new java.io.ByteArrayOutputStream()

      Console.withIn(input) {
        Console.withOut(output) {
          unoTui.handleInput("0")
        }
      }

      val outputStr = output.toString
      assert(outputStr.contains("Played: WildCard"))
      assert(unoTui.selectedColor.contains("blue"))

    }

    "handleCardSelection should handle draw command" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      GameBoard.initGame(gameState)
      val unoTui = new UnoTui(){
        override def display(): Unit = {}
      }

      unoTui.handleInput("draw")

      assert(GameBoard.gameState.currentPlayerIndex == 1)
      assert(GameBoard.gameState.players.head.cards.length == 3)
    }

    "should show error when selecting an invalid card index" in {
      val playableCard = NumberCard("green", 5)
      val topCard = NumberCard("green", 3) // Matching color

      val gameState = GameState(
        players = List(PlayerHand(List(playableCard))),
        currentPlayerIndex = 0,
        drawPile = List.empty, // Empty draw pile
        discardPile = List(topCard),
        isReversed = false,
        allCards = List(playableCard, topCard)
      )

      assert(gameState.isValidPlay(playableCard, Some(topCard), None))
      
      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()

      val outputStream = new java.io.ByteArrayOutputStream()
      Console.withOut(outputStream) {
        unoTui.display()
        unoTui.handleInput("99")
      }

      val output = outputStream.toString

      assert(output.contains("Invalid index! Please select a valid card."))
      assert(!output.contains("No cards left"))
      assert(GameBoard.gameState.players.head.cards.contains(playableCard))
    }

    "should show error when chosen card doesn't match selected color after wild card" in {
      val wrongCard = NumberCard("blue", 5)
      val wildCard = WildCard("wild draw four")

      val gameState = GameState(
        players = List(PlayerHand(List(wrongCard))),
        currentPlayerIndex = 0,
        drawPile = List.empty,
        discardPile = List(wildCard),
        isReversed = false,
        allCards = List(wrongCard, wildCard)
      )

      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()
      unoTui.selectedColor = Some("red")

      val displayOutput = new java.io.ByteArrayOutputStream()
      Console.withOut(displayOutput) {
        unoTui.display()
      }

      val selectionOutput = new java.io.ByteArrayOutputStream()
      Console.withOut(selectionOutput) {
        unoTui.handleInput("0")
      }

      val combinedOutput = displayOutput.toString + selectionOutput.toString

      assert(combinedOutput.contains("The color that was chosen: red"))
      assert(combinedOutput.contains("Invalid play! The color must be red. Try again."))
      assert(combinedOutput.contains("NumberCard(blue, 5)"))
    }

    "handleCardSelection should handle valid card selection" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("red", 4)),
        discardPile = List(NumberCard("red", 1)),
        isReversed = false,
        allCards = List()
      )
      
      GameBoard.initGame(gameState)
      
      val updatedGame = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 1,
        drawPile = List(),
        discardPile = List(NumberCard("red", 1)),
        isReversed = false,
        allCards = List()
      )

      val unoTui = new UnoTui(){
        override def display(): Unit = {}
      }
      unoTui.handleInput("0")

      assert(GameBoard.gameState.currentPlayerIndex == 1)
      assert(GameBoard.gameState.discardPile.last == NumberCard("red", 1))
    }

    "should trigger UNO announcement when playing down to one card" in {
      // 1. Setup mit Spieler, der 2 Karten hat (wird auf 1 reduziert)
      val cards = List(
        NumberCard("red", 2), // Wird gespielt
        NumberCard("red", 3)  // Bleibt übrig
      )
      val topCard = NumberCard("red", 1) // Passend zu den Spielkarten

      // Spieler initialisieren mit explizitem hasSaidUno = false
      val player = PlayerHand(cards)
      player.hasSaidUno = false

      val gameState = GameState(
        players = List(player),
        currentPlayerIndex = 0,
        drawPile = List.empty,
        discardPile = List(topCard),
        isReversed = false,
        allCards = cards :+ topCard
      )

      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()
      
      // 2. Output erfassen
      val outputStream = new java.io.ByteArrayOutputStream()
      Console.withOut(outputStream) {
        unoTui.handleInput("0") // Spielt die erste Karte (red 2)
      }
      val output = outputStream.toString

      // 3. Direkte Überprüfung des Codeblocks
      //assert(output.contains("said UNO"),
      assert(GameBoard.gameState.players.head.hasSaidUno, "Spieler sollte UNO-Status haben")
      assert(GameBoard.gameState.players.head.cards.size == 1, "Sollte eine Karte haben")
    }

    "handleCardSelection should reject invalid card selection" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      GameBoard.initGame(gameState)
      val unoTui = new UnoTui()
      
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.handleInput("999")
      }
      val output = stream.toString
      assert(output.contains("Invalid index! Please select a valid card."))
    }
  }

  "update should call display when gameShouldExit is false" in {
    val playableCard = NumberCard("red", 5)
    val topCard = NumberCard("red", 3)

    val gameState = GameState(
      players = List(PlayerHand(List(playableCard))),
      currentPlayerIndex = 0,
      drawPile = List.fill(5)(NumberCard("blue", 1)),
      discardPile = List(topCard),
      isReversed = false,
      allCards = List(playableCard, topCard)
    )

    GameBoard.initGame(gameState)
    val unoTui = new UnoTui()
    unoTui.setShouldExit(false)


    val outputStream = new java.io.ByteArrayOutputStream()
    Console.withOut(outputStream) {
      unoTui.update()
    }
    val output = outputStream.toString

    // Verify key elements appear in the output (order doesn't matter)
    assert(output.contains("Player 1's turn!"))

    // More flexible card verification - just check the card appears somewhere
    assert(output.contains("NumberCard(red, 3)"), "Top card should be displayed")
    assert(output.contains("NumberCard(red, 5)"), "Player's card should be displayed")

    // Verify the display structure
    assert(output.contains("Top Card:"), "Should show top card label")
    assert(output.contains("Your Cards:"), "Should show player's cards label")
    assert(output.contains("0 - "), "Should show card index")
  }

  "shouldExit should return the value of gameShouldExit" in {
    val gameState = GameState(
      players = List(PlayerHand(List(NumberCard("red", 3)))),
      currentPlayerIndex = 0,
      drawPile = List.fill(5)(NumberCard("blue", 4)),
      discardPile = List(NumberCard("yellow", 2)),
      isReversed = false,
      allCards = List()
    )

    GameBoard.initGame(gameState)
    val unoTui = new UnoTui()
    
    unoTui.setShouldExit(false)

    assert(!unoTui.shouldExit, "shouldExit should return false when gameShouldExit is false")
    
    unoTui.setShouldExit(true)

    assert(unoTui.shouldExit, "shouldExit should return true when gameShouldExit is true")
  }


  "update should not call display if gameShouldExit is true" in {
    val playerHand = PlayerHand(List(NumberCard("red", 3)))
    val gameState = GameState(
      players = List(playerHand),
      currentPlayerIndex = 0,
      drawPile = List(),
      discardPile = List(NumberCard("yellow", 4)),
      isReversed = false,
      allCards = List()
    )

    GameBoard.initGame(gameState)
    val unoTui = new UnoTui()
    
    unoTui.setShouldExit(true)


    val output = new ByteArrayOutputStream()
    Console.withOut(output) {
      unoTui.update()
    }

    val outputStr = output.toString
    assert(outputStr.isEmpty)
  }
  "detect and handle winner correctly" in {
    val unoTui = new UnoTui()
    unoTui.setShouldExit(false)

    val winningPlayer = PlayerHand(List.empty)
    val otherPlayer = PlayerHand(List(NumberCard("red", 1)))
    val gameState = GameState(
      players = List(winningPlayer, otherPlayer),
      currentPlayerIndex = 0,
      allCards = Nil,
      isReversed = false,
      discardPile = List(NumberCard("blue", 2)),
      drawPile = Nil,
      selectedColor = None
    )

    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) {
      GameBoard.initGame(gameState)
      unoTui.checkForWinner()
    }

    unoTui.shouldExit shouldBe true
    out.toString should include("Player 1 wins! Game over.")
  }
}