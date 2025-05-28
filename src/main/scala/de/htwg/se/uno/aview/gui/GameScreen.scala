package de.htwg.se.uno.aview.gui

import de.htwg.se.uno.aview
import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.controller.GameBoard.fullDeck
import de.htwg.se.uno.controller.command.{DrawCardCommand, PlayCardCommand}
import de.htwg.se.uno.model.{ActionCard, Card, CardFactory, GameState, NumberCard, PlayerHand, WildCard}
import scalafx.animation.{FadeTransition, PauseTransition}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Cursor
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control.{Alert, Button, ButtonType, Dialog, Label}
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{HBox, Pane, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.util.Duration

import scala.util.{Failure, Success}


class GameScreen(players: Int, cardsPerPlayer: Int) extends StackPane {
  val allCards = CardFactory.createFullDeck()
  val hands = fullDeck.grouped(cardsPerPlayer).take(players).map(cards => PlayerHand(cards)).toList
  val usedCards = hands.flatten(_.cards)
  val remainingDeck = fullDeck.diff(usedCards)

  GameBoard.initGame(GameState(
    players = hands,
    currentPlayerIndex = 0,
    isReversed = false,
    drawPile = remainingDeck,
    discardPile = Nil,
    allCards = allCards
  ))

  private val gameBoardImage = new ImageView(new Image("file:src/main/resources/gameboard/uno_gameboard_left.jpg")) {
    fitWidth = 1500
    fitHeight = 900
    alignment = Pos.TopCenter
    preserveRatio = false
  }

  private val gameInfo = new Label {
    text = s"Spieler: $players | Karten: $cardsPerPlayer"
    font = Font(24)
    textFill = Color.White
    style = "-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 10; -fx-background-radius: 10;"
  }


  private val discardPileView = new VBox {
    spacing = 5
    alignment = Pos.Center
    children = createCardView(GameBoard.gameState.get.discardPile.take(1))
  }


  private val drawPileView = new Button {
    text = "Ziehen"
    style = "-fx-font-family: 'sans-serif'; " +
      "-fx-font-style: italic; " +
      "-fx-font-weight: bold; " +
      "-fx-font-size: 20pt; " +
      "-fx-background-color: linear-gradient(to bottom, #FCE205, #F9A602); " +
      "-fx-text-fill: white; " +
      "-fx-padding: 10 20; " +
      "-fx-background-radius: 10; " +
      "-fx-border-radius: 10;"
    effect = new DropShadow {
      color = Color.White
      radius = 10
    }

    cursor = Cursor.Hand

    onAction = _ => {
      GameBoard.executeCommand(DrawCardCommand())
      update()
    }
  }


  private val playerHandView = new Pane {
    prefHeight = 150
  }

  // Hauptlayout
  children = Seq(
    gameBoardImage,
    new VBox {
      spacing = 20
      alignment = Pos.Center
      children = Seq(
        gameInfo,
        new HBox {
          spacing = 50
          alignment = Pos.Center
          children = Seq(drawPileView, discardPileView)
        }
      )
    },
    new StackPane {
      alignment = Pos.BottomCenter
      padding = Insets(0, 0, 50, 0)
      children = playerHandView
    }
  )


  private def createCardView(cards: List[Card]): Seq[ImageView] = {
    val overlapOffset = 30.0
    cards.zipWithIndex.map { case (card, index) =>
      new ImageView {
        image = new Image(cardImagePath(card))
        fitWidth = 80
        preserveRatio = true
        layoutX = index * overlapOffset
        layoutY = 0
        onMouseClicked = _ => playCard(card)
      }
    }
  }

  private def cardImagePath(card: Card): String = card match {
    case NumberCard(number, color) =>
      s"file:src/main/resources/cards/${number}_$color.png"

    case ActionCard(color, action) =>
      action match {
        case "reverse"   => s"file:src/main/resources/cards/reverse_$color.png"
        case "skip"      => s"file:src/main/resources/cards/next_$color.png"
        case "draw two"  => s"file:src/main/resources/cards/draw2_$color.png"
        case _           => "file:src/main/resources/cards/unknown.png" // fallback
      }

    case WildCard(action) =>
      action match {
        case "wild draw four" => "file:src/main/resources/cards/draw_four.png"
        case "wild"           => "file:src/main/resources/cards/wild.png"
        case _                => "file:src/main/resources/cards/unknown.png"
      }
  }


  def playCard(card: Card): Unit = {
    GameBoard.gameState match {
      case Success(state) =>
        if (GameBoard.isValidPlay(card, state.discardPile.head, None)) {
          card match {
            case wild: WildCard =>

              showColorPickerDialog(wild)
            case _ =>

              GameBoard.executeCommand(PlayCardCommand(card, None))
              update()
          }
        } else {
          showInvalidMoveMessage()
        }
      case Failure(_) =>
    }
  }

  private def showInvalidMoveMessage(): Unit = {
    val alert = new Alert(Alert.AlertType.Warning) {
      title = "Ungültiger Zug"
      headerText = "Diese Karte kann nicht gespielt werden"
      contentText = "Die Karte passt nicht zur obersten Karte auf dem Ablagestapel"
    }
    alert.showAndWait()
  }

  private def showColorPickerDialog(wildCard: WildCard): Unit = {
    val dialog = new Dialog[String]() {
      title = "Farbe wählen"
      headerText = "Wähle eine Farbe für die Wildcard"
      contentText = "Bitte wähle:"
    }
      val buttonTypes = Seq(
        new ButtonType("Rot", ButtonData.OKDone),
        new ButtonType("Blau", ButtonData.OKDone),
        new ButtonType("Grün", ButtonData.OKDone),
        new ButtonType("Gelb", ButtonData.OKDone),
        new ButtonType("Abbrechen", ButtonData.CancelClose)
      )

    dialog.dialogPane().getButtonTypes.addAll(buttonTypes.map(_.delegate): _*)

    val colorMap = Map(
      "Rot" -> "red",
      "Blau" -> "blue",
      "Grün" -> "green",
      "Gelb" -> "yellow"
    )

    dialog.resultConverter = (buttonType: ButtonType) => {
      if (buttonType.getButtonData == ButtonData.OKDone) {
        colorMap.getOrElse(buttonType.getText, "")
      } else {
        null
      }
    }

    dialog.showAndWait() match {
      case Some(color: String) =>
        GameBoard.executeCommand(PlayCardCommand(wildCard, Some(color)))
        update()
      case None =>
    }
  }

  def update(): Unit = {
    GameBoard.gameState match {
      case Success(state) =>
        gameInfo.opacity = 1.0
        gameInfo.text = s"Spieler ${state.currentPlayerIndex + 1} ist am Zug"

        val pause = new PauseTransition(Duration(2000))

        val fade = new FadeTransition(Duration(1000), gameInfo) {
          fromValue = 1.0
          toValue = 0.0
        }

        pause.onFinished = _ => fade.play()
        pause.play()

        discardPileView.children = createCardView(state.discardPile.take(1))
        playerHandView.children.setAll(createCardView(state.players.head.cards).map(_.delegate): _*) // Only for player 1
      case Failure(e) =>
        gameInfo.text = s"Fehler: ${e.getMessage}"
        gameInfo.opacity = 1.0
    }
  }

  update()
  GameBoard.addObserver(() => update())
}
