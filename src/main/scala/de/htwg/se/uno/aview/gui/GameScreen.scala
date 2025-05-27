package de.htwg.se.uno.aview.gui

import de.htwg.se.uno.aview
import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.controller.command.{DrawCardCommand, PlayCardCommand}
import de.htwg.se.uno.model.{Card, GameState, NumberCard, PlayerHand, WildCard}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control.{Alert, Button, ButtonType, Dialog, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scala.util.{Failure, Success}


class GameScreen(players: Int, cardsPerPlayer: Int) extends StackPane {

  val playerHand: PlayerHand = PlayerHand(List(NumberCard("red", 5)))
  GameBoard.initGame(GameState(
    players = List(playerHand),
    currentPlayerIndex = 0,
    isReversed = false,
    drawPile = Nil,
    discardPile = Nil,
    allCards = Nil
  ))

  private val gameBoardImage = new ImageView(new Image("file:src/main/resources/gameboard/uno_gameboard_left.jpg")) {
    fitWidth = 1400
    fitHeight = 900
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
    style = "-fx-font-size: 16pt; -fx-padding: 20;"
    onAction = _ => {
      GameBoard.executeCommand(DrawCardCommand())
      update()
    }
  }
  

  private val playerHandView = new HBox {
    spacing = 10
    alignment = Pos.Center
    children = createCardView(GameBoard.gameState.get.players.head.cards)
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
    cards.map { card =>
      new ImageView {
        image = new Image(cardImagePath(card))
        fitWidth = 80
        preserveRatio = true
        onMouseClicked = _ => playCard(card)
      }
    }
  }
  
  private def cardImagePath(card: Card): String = {
    card match {
      case _: WildCard => s"file:src/main/resources/cards/wild.png"
      case _ => s"file:src/main/resources/cards/${card.color}.png"
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
        gameInfo.text = s"Spieler ${state.currentPlayerIndex + 1} ist am Zug"
        discardPileView.children = createCardView(state.discardPile.take(1))
        playerHandView.children = createCardView(state.players.head.cards) // Only for player 1
      case Failure(e) =>
        gameInfo.text = s"Fehler: ${e.getMessage}"
    }
  }
  
  update()
  GameBoard.addObserver(() => update())
}
