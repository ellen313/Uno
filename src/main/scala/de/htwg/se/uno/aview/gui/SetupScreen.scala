package de.htwg.se.uno.aview.gui

import scalafx.scene.layout.VBox
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.geometry.Pos
import scalafx.geometry.Insets

object SetupScreen {
  def apply(onStartGame: (Int, Int) => Unit): VBox = {
    val playersInput = new TextField {
      promptText = "Number of Players (2-10)"
      maxWidth = 200
    }
    val cardsInput = new TextField {
      promptText = "Number of Cards"
      maxWidth = 200
    }
    val startButton = new Button("Start the Game") {
      onAction = _ =>
        try {
          val players = playersInput.text.value.toInt
          val cards = cardsInput.text.value.toInt
          if (players < 2 || players > 10) throw new NumberFormatException("Invalid number of Players")
          onStartGame(players, cards)
        } catch {
          case _: NumberFormatException =>
            println("Please enter valid number!")
        }
    }

    new VBox {
      spacing = 10
      padding = Insets(20)
      alignment = Pos.Center
      children = Seq(
        new Label("Setup"),
        playersInput,
        cardsInput,
        startButton
      )
    }
  }
}

