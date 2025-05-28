package de.htwg.se.uno.aview.gui

import de.htwg.se.uno.aview
import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.controller.GameBoard.fullDeck
import de.htwg.se.uno.controller.command.{DrawCardCommand, PlayCardCommand, UnoCalledCommand}
import de.htwg.se.uno.model.{ActionCard, Card, CardFactory, GameState, NumberCard, PlayerHand, WildCard}
import scalafx.animation.{FadeTransition, PauseTransition}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Cursor
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control.{Alert, Button, ButtonType, Dialog, Label}
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, HBox, Pane, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.util.Duration

import scala.util.{Failure, Success}


class GameScreen(players: Int, cardsPerPlayer: Int) extends StackPane {
  private var unoCaller: Option[Int] = None
  val allCards = CardFactory.createFullDeck()
  val hands = fullDeck.grouped(cardsPerPlayer).take(players).map(cards => PlayerHand(cards)).toList
  val usedCards = hands.flatMap(_.cards)
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
    fitHeight = 880
    preserveRatio = true
    smooth = true
    cache = true
  }

  private val gameInfo = new Label {
    text = s"Spieler: $players | Karten: $cardsPerPlayer"
    font = Font(24)
    textFill = Color.White
    style = "-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 10; -fx-background-radius: 10;"
  }

  private val player1Label = new Label("You") {
    font = Font(20)
    textFill = Color.White
    style =
      "-fx-font-family: 'sans-serif'; " +
      "-fx-font-style: italic; " +
      "-fx-font-weight: bold; " +
      "-fx-font-size: 15pt; " +
      "-fx-background-color: linear-gradient(to bottom, #FCE205, #F9A602); " +
      "-fx-text-fill: white; " +
      "-fx-padding: 10 20; " +
      "-fx-background-radius: 10; " +
      "-fx-border-radius: 10;"
  }

  private val player1HandView = new HBox {
    spacing = -70
    alignment = Pos.BottomCenter
    padding = Insets(10)
  }

  private val player2Label = new Label("Player 1") {
    font = Font(20)
    textFill = Color.White
    style =
      "-fx-font-family: 'sans-serif'; " +
      "-fx-font-style: italic; " +
      "-fx-font-weight: bold; " +
      "-fx-font-size: 15pt; " +
      "-fx-background-color: linear-gradient(to bottom, #FCE205, #F9A602); " +
      "-fx-text-fill: white; " +
      "-fx-padding: 10 20; " +
      "-fx-background-radius: 10; " +
      "-fx-border-radius: 10;"
  }

  private val player2HandView = new HBox {
    spacing = -70
    alignment = Pos.TopCenter
    padding = Insets(10)
  }

  private val unoButton = new Button("UNO!") {
    style =
      "-fx-font-family: 'sans-serif'; " +
      "-fx-font-style: italic; " +
      "-fx-font-weight: bold; " +
      "-fx-font-size: 15pt; " +
      "-fx-background-color: linear-gradient(to bottom, #FCE205, #F9A602); " +
      "-fx-text-fill: white; " +
      "-fx-padding: 10 20; " +
      "-fx-background-radius: 10; " +
      "-fx-border-radius: 10;"
    effect = new DropShadow {
      color = Color.White
      radius = 10
    }
    cursor = Cursor.Default
    onMouseEntered = _ => cursor = Cursor.Hand
    onMouseExited = _ => cursor = Cursor.Default
    onAction = _ => {
      GameBoard.executeCommand(UnoCalledCommand())
      unoCaller = Some(GameBoard.gameState.get.currentPlayerIndex)
      update()
    }
  }

  private val discardPileView = new VBox {
    spacing = 5
    alignment = Pos.Center
    children = createCardView(GameBoard.gameState.get.discardPile.take(1))
  }

  val drawPileView = new ImageView {
    image = new Image("file:src/main/resources/cards/back.png")
    fitWidth = 100
    fitHeight = 150
    preserveRatio = true
  }

  private val playerHandView = new HBox {
    spacing = -50
    alignment = Pos.BottomCenter
    padding = Insets(10)
  }

  // Hauptlayout
  children = Seq(
    gameBoardImage,

    new VBox {
      spacing = 10
      children = Seq(player1Label)
      alignment = Pos.BottomCenter
      margin = Insets(0,0,20,0)
    },
    new VBox {
      spacing = 10
      children = Seq(player2Label)
      alignment = Pos.TopCenter
      margin = Insets(20,0,0,0)
    },

    new VBox {
      spacing = 20
      alignment = Pos.Center
      children = Seq(
        gameInfo,
        new Button("Ziehen") {
          style =
            "-fx-font-family: 'sans-serif'; " +
              "-fx-font-style: italic; " +
              "-fx-font-weight: bold; " +
              "-fx-font-size: 15pt; " +
              "-fx-background-color: linear-gradient(to bottom, #FCE205, #F9A602); " +
              "-fx-text-fill: white; " +
              "-fx-padding: 10 20; " +
              "-fx-background-radius: 10; " +
              "-fx-border-radius: 10;"
          effect = new DropShadow {
            color = Color.White
            radius = 10
          }
          cursor = Cursor.Default
          onMouseEntered = _ => cursor = Cursor.Hand
          onMouseExited = _ => cursor = Cursor.Default
          onAction = _ => {
            GameBoard.executeCommand(DrawCardCommand())
            update()
          }
        },
        unoButton
      )
    },
    new VBox {
      spacing = 10
      alignment = Pos.Center
      children = Seq(discardPileView)
    },
    player1HandView,
    player2HandView
  )


  private def createCardView(cards: List[Card], hidden: Boolean = false): Seq[ImageView] = {
    val overlapOffset = 30.0
    cards.zipWithIndex.map { case (card, index) =>
      val imagePath = if (hidden) {
        "file:src/main/resources/cards/back.png"
      } else {
        cardImagePath(card)
      }
      new ImageView {
        image = new Image(cardImagePath(card))
        fitWidth = 130
        fitHeight = 390
        preserveRatio = true
        if (!hidden) {
          onMouseClicked = _ => playCard(card)
        }
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
        if (unoCaller.isDefined) {
          val unoPlayer = unoCaller.get + 1
          gameInfo.text = s"Spieler $unoPlayer ruft UNO!"
          unoCaller = None
        } else {
          gameInfo.text = s"Spieler ${state.currentPlayerIndex + 1} ist am Zug"
        }
        gameInfo.opacity = 1.0

        val pause = new PauseTransition(Duration(2000))
        val fade = new FadeTransition(Duration(1000), gameInfo) {
          fromValue = 1.0
          toValue = 0.0
        }
        pause.onFinished = _ => fade.play()
        pause.play()

        discardPileView.children = createCardView(state.discardPile.take(1))

        // you spieler
        val player1Cards = state.players.head.cards.take(7)
        player1HandView.children.setAll(createCardView(state.players.head.cards).map(_.delegate): _*)
        if (state.players.length > 1) {
          val player2Cards = state.players(1).cards.take(7)
          player2HandView.children.setAll(createCardView(player2Cards, hidden = true).map(_.delegate): _*)
        }
      case Failure(e) =>
        gameInfo.text = s"Fehler: ${e.getMessage}"
        gameInfo.opacity = 1.0
    }
  }

  update()
  GameBoard.addObserver(() => update())
}
