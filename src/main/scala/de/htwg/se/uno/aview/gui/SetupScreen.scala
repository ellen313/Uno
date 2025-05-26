package de.htwg.se.uno.aview.gui

import scalafx.scene.layout.StackPane
import scalafx.scene.layout.VBox
import scalafx.scene.control.{Button, ComboBox, Label, TextField}
import scalafx.geometry.Pos
import scalafx.geometry.Insets
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

object SetupScreen {
  def apply(onStartGame: (Int, Int) => Unit): StackPane = {
    val defaultCardsPerPlayer = 7

    val playersInput = new ComboBox[Int](2 to 10) {
      promptText = "Number of Players (2-10)"
      maxWidth = 200
    }

    val startButton = new Button("Start the Game") {
      style = "-fx-font-size: 14pt; -fx-background-color: white; -fx-text-fill: red;"
      onAction = _ =>
        try {
          val players = playersInput.value.value
          if (players < 2 || players > 10) throw new NumberFormatException("Invalid number of Players")
          onStartGame(players, defaultCardsPerPlayer)
        } catch {
          case _: Exception =>
            println("Please select a valid number of players!")
        }
    }

    val layout = new VBox {
      spacing = 10
      padding = Insets(20)
      alignment = Pos.Center
      children = Seq(
        new Label("UNO Setup") {
          style = "-fx-font-size: 20pt; -fx-text-fill: white;"
        },
        playersInput,
        startButton
      )
    }

    val backgroundRec = new Rectangle {
      width <== layout.width
      height <== layout.height
      fill = Color.rgb(178, 34, 34) // Uno-Rot
    }

    new StackPane {
      children = Seq(backgroundRec, layout)
    }
  }
}
