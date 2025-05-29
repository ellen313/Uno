package de.htwg.se.uno.aview.gui

import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.controller.GameBoard.fullDeck
import de.htwg.se.uno.controller.command.{DrawCardCommand, PlayCardCommand, UnoCalledCommand}
import de.htwg.se.uno.model.{ActionCard, Card, CardFactory, GameState, NumberCard, PlayerHand, WildCard}
import scalafx.animation.{FadeTransition, PauseTransition}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Alert, Button, ButtonBar, ButtonType, Dialog, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, FontWeight}
import scalafx.scene.Cursor
import scalafx.util.Duration
import scalafx.Includes.*

import scala.util.{Failure, Success}

class GameScreen(players: Int, cardsPerPlayer: Int) extends StackPane {
  private var unoCaller: Option[Int] = None
  private var gameOver: Boolean = false

  val allCards = CardFactory.createFullDeck()
  val hands = fullDeck.grouped(cardsPerPlayer).take(players).map(cards => PlayerHand(cards)).toList
  val usedCards = hands.flatMap(_.cards)
  val remainingDeck = fullDeck.diff(usedCards)
  val initialDiscard = remainingDeck.head
  val updatedRemainingDeck = remainingDeck.tail

  GameBoard.initGame(GameState(
    players = hands,
    currentPlayerIndex = 0,
    isReversed = false,
    drawPile = updatedRemainingDeck,
    discardPile = List(initialDiscard),
    allCards = allCards
  ))

  private val gameBoardImage = new ImageView(new Image("file:src/main/resources/gameboard/uno_gameboard_left.jpg")) {
    fitWidth = 1500
    fitHeight = 880
    preserveRatio = true
    smooth = true
    cache = true
    mouseTransparent = true
  }

  private val winnerLabel = new Label {
    text = ""
    font = Font.font("Arial", FontWeight.Bold, 48)
    textFill = Color.Gold
    style = "-fx-background-color: rgba(0,0,0,0.8); -fx-background-radius: 20; -fx-padding: 30;"
    visible = false
  }

  private val gameInfo = new Label {
    text = s"Spieler: $players | Karten: $cardsPerPlayer"
    font = Font(24)
    textFill = Color.White
    style = "-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 10; -fx-background-radius: 10;"
  }

  private val player1HandView = new HBox {
    spacing = -60
    alignment = Pos.BottomCenter
    padding = Insets(10)
    pickOnBounds = false
  }

  private val player2HandView = new HBox {
    spacing = -60
    alignment = Pos.TopCenter
    padding = Insets(10)
    pickOnBounds = false
  }

  private val discardPileView = new VBox {
    spacing = 5
    alignment = Pos.Center
    pickOnBounds = false
  }

  private val drawPileView = new ImageView {
    image = new Image("file:src/main/resources/cards/back.png")
    fitWidth = 130
    fitHeight = 190
    preserveRatio = true
    cursor = Cursor.Hand
    pickOnBounds = true
    onMouseClicked = (e: MouseEvent) => {
      println("Draw pile clicked!")
      GameBoard.executeCommand(DrawCardCommand())
      update()
      e.consume()
    }
  }

  private val drawButton = new Button("Draw") {
    style = buttonStyle
    onAction = _ => {
      GameBoard.executeCommand(DrawCardCommand())
      update()
    }
  }

  private val unoButton = new Button("UNO!") {
    style = buttonStyle
    onAction = _ => {
      GameBoard.executeCommand(UnoCalledCommand())
      unoCaller = Some(GameBoard.gameState.get.currentPlayerIndex)
      update()
    }
  }

  private val exitButton = new Button("Exit") {
    style = buttonStyle
    onAction = _ => System.exit(0)
  }
  
  private val youLabel = new Label {
    text = "You"
    style = "-fx-text-fill: white; -fx-font-size: 20pt;"
  }

  children = Seq(
    gameBoardImage,

    new VBox {
      alignment = Pos.TopLeft
      padding = Insets(20)
      children = Seq(exitButton)
      pickOnBounds = false
    },

    new VBox {
      alignment = Pos.TopCenter
      spacing = 10
      children = Seq(player2HandView)
      pickOnBounds = false
    },

    new VBox {
      alignment = Pos.BottomCenter
      spacing = 10
      children = Seq(
        youLabel,
        player1HandView
      )
      pickOnBounds = false
    },

    new VBox {
      alignment = Pos.Center
      spacing = 20
      children = Seq(
        new StackPane {
          children = drawPileView
          pickOnBounds = false
        },
        discardPileView
      )
      pickOnBounds = false
    },

    new VBox {
      alignment = Pos.BottomRight
      padding = Insets(0, 100, 100, 0)
      spacing = 10
      children = Seq(drawButton, unoButton)
      pickOnBounds = false
    },

    new VBox {
      alignment = Pos.TopRight
      padding = Insets(20)
      children = Seq(gameInfo)
      pickOnBounds = false
    },

    new StackPane {
      alignment = Pos.Center
      children = Seq(winnerLabel)
      pickOnBounds = false
    }
  )

  private def buttonStyle: String =
    "-fx-font-family: 'sans-serif'; " +
      "-fx-font-style: italic; " +
      "-fx-font-weight: bold; " +
      "-fx-font-size: 15pt; " +
      "-fx-background-color: linear-gradient(to bottom, #FCE205, #F9A602); " +
      "-fx-text-fill: white; " +
      "-fx-padding: 10 20; " +
      "-fx-background-radius: 10; " +
      "-fx-border-radius: 10;"

  private def createCardView(cards: List[Card], hidden: Boolean = false): Seq[ImageView] = {
    cards.zipWithIndex.map { case (card, index) =>
      val imagePath = /*if (hidden) "file:src/main/resources/cards/back.png" else*/ cardImagePath(card)
      new ImageView(new Image(imagePath)) {
        fitWidth = 130
        fitHeight = 190
        preserveRatio = true
        pickOnBounds = true
        if (!hidden) {
          cursor = Cursor.Hand
          onMouseClicked = (e: MouseEvent) => {
            println(s"Card $index clicked!")
            e.consume()
            playCard(card)
          }
        }

        if (!hidden && !gameOver) {
          cursor = Cursor.Hand
          onMouseClicked = (e: MouseEvent) => {
            println(s"Card $index clicked!")
            e.consume()
            playCard(card)
          }
        }

        style = if (!hidden) "-fx-effect: dropshadow(gaussian, white, 5, 0.5, 0, 0);" else ""
      }
    }
  }

  private def cardImagePath(card: Card): String = card match {
    case NumberCard(color, number) => s"file:src/main/resources/cards/${number}_$color.png"
    case ActionCard(color, action) => action match {
      case "reverse" => s"file:src/main/resources/cards/reverse_$color.png"
      case "skip" => s"file:src/main/resources/cards/next_$color.png"
      case "draw two" => s"file:src/main/resources/cards/draw2_$color.png"
      case _ => "file:src/main/resources/cards/unknown.png"
    }
    case WildCard(action) => action match {
      case "wild draw four" => "file:src/main/resources/cards/draw_four.png"
      case "wild" => "file:src/main/resources/cards/wild.png"
      case _ => "file:src/main/resources/cards/unknown.png"
    }
  }

  def playCard(card: Card): Unit = {
    GameBoard.gameState match {
      case Success(state) =>
        if (GameBoard.isValidPlay(card, state.discardPile.headOption.getOrElse(card), None)) {
          card match {
            case wild: WildCard => showColorPickerDialog(wild)
            case _ =>
              GameBoard.executeCommand(PlayCardCommand(card, None))
              GameBoard.gameState match {
                case Success(newState) =>
                  println(s"Current player: ${newState.currentPlayerIndex}")
                  update()
                case Failure(e) => println(s"Error: ${e.getMessage}")
              }
          }
        } else {
          showInvalidMoveMessage()
        }
      case Failure(e) => println(s"Error: ${e.getMessage}")
    }
  }

  private def showInvalidMoveMessage(): Unit = {
    new Alert(Alert.AlertType.Warning) {
      title = "Invalid move"
      headerText = "Diese Karte kann nicht gespielt werden"
      contentText = "Sie passt nicht zur obersten Karte auf dem Ablagestapel."
    }.showAndWait()
  }

  private def showColorPickerDialog(wildCard: WildCard): Unit = {
    val dialog = new Dialog[String]() {
      title = "Farbe wählen"
      headerText = "Wähle eine Farbe für die Wildcard"
    }

    val buttonTypes = Seq("Red", "Blue", "Green", "Yellow").map { color =>
      new ButtonType(color, ButtonBar.ButtonData.OKDone)
    } :+ new ButtonType("Abbrechen", ButtonBar.ButtonData.CancelClose)

    dialog.dialogPane().getButtonTypes.addAll(buttonTypes.map(_.delegate).toSeq: _*)

    val colorMap = Map("Red" -> "red", "Blue" -> "blue", "Green" -> "green", "Yellow" -> "yellow")

    dialog.resultConverter = (bt: ButtonType) => {
      if (bt.getButtonData == ButtonBar.ButtonData.OKDone)
        colorMap.get(bt.getText).orNull
      else
        null
    }

    dialog.showAndWait() match {
      case Some(color: String) if color.nonEmpty =>
        println(s"Color chosen: $color for wildcard $wildCard")
        GameBoard.executeCommand(PlayCardCommand(wildCard, Some(color)))
        GameBoard.gameState match {
          case Success(newState) =>
            println(s"Wildcard played, new player index: ${newState.currentPlayerIndex}")
            update()
          case Failure(e) =>
            println(s"Error after playing wildcard: ${e.getMessage}")
        }

      case Some(null) | None =>
        println("No color selected or dialog was cancelled.")

      case Some(other) =>
        println(s"Unexpected dialog result: $other (${Option(other).map(_.getClass.getName).getOrElse("null")})")
    }
  }

  def update(): Unit = {
    if (gameOver) return

    GameBoard.gameState match {
      case Success(state) =>
        state.players.zipWithIndex.find(_._1.cards.isEmpty) match {
          case Some((_, index)) =>
            gameOver = true
            winnerLabel.text = s"Spieler ${index + 1} hat gewonnen!"
            winnerLabel.visible = true
            drawButton.disable = true
            unoButton.disable = true
            player1HandView.children.foreach(_.setDisable(true))
            player2HandView.children.foreach(_.setDisable(true))
            return
          case None =>
        }

        if (unoCaller.isDefined) {
          val unoPlayer = unoCaller.get + 1
          gameInfo.text = s"Player $unoPlayer ruft UNO!"
          unoCaller = None
        } else {
          gameInfo.text = s"Player ${state.currentPlayerIndex + 1} ist am Zug"
        }
        
        youLabel.text = s"You (Player ${state.currentPlayerIndex + 1})"

        val pause = new PauseTransition(Duration(2000))
        val fade = new FadeTransition(Duration(1000), gameInfo) {
          fromValue = 1.0
          toValue = 0.0
        }
        pause.onFinished = _ => fade.play()
        pause.play()
        
        discardPileView.children.setAll(createCardView(state.discardPile.take(1)).map(_.delegate): _*)
        
        val currentPlayerHand = state.players(state.currentPlayerIndex).cards
        player1HandView.children.setAll(createCardView(currentPlayerHand).map(_.delegate): _*)
        
        val nextPlayerIndex = (state.currentPlayerIndex + 1) % state.players.length
        val nextPlayerCardCount = state.players(nextPlayerIndex).cards.length
        val backImage = new Image("file:src/main/resources/cards/back.png")
        val backCards = Seq.fill(nextPlayerCardCount) {
          new ImageView(backImage) {
            fitWidth = 130
            fitHeight = 190
            preserveRatio = true
          }
        }
        player2HandView.children.setAll(backCards.map(_.delegate): _*)

      case Failure(e) =>
        gameInfo.text = s"Fehler: ${e.getMessage}"
    }
  }

  update()
  GameBoard.addObserver(() => update())
}
