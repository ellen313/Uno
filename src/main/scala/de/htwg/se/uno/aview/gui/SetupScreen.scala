package de.htwg.se.uno.aview.gui

import scalafx.scene.layout.StackPane
import scalafx.scene.layout.VBox
import scalafx.scene.control.{Button, ComboBox, Label, ListCell, TextField}
import scalafx.geometry.Pos
import scalafx.geometry.Insets
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color.{DarkRed, LightGrey, LightYellow, Red, White}
import scalafx.scene.paint.{Color, CycleMethod, LinearGradient, RadialGradient, Stop, Stops}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text

object SetupScreen {
  def apply(onStartGame: (Int, Int) => Unit): StackPane = {
    val setupImage = new ImageView(new Image("file:src/main/scala/UnoSetup.jpg")) {
      fitWidth = 1400
      fitHeight = 900
      preserveRatio = false
    }

    val defaultCardsPerPlayer = 7

    val playersInput = new ComboBox[Int](2 to 10) {
      promptText = "Number of Players (2-10)"
      margin = Insets(20)
      minWidth = 300
      minHeight = 40
      style = """
        -fx-background-color: #F2F2F2;
        -fx-text-fill: #8B0000;
        -fx-font-size: 14pt;
        -fx-font-weight: bold;
        -fx-prompt-text-fill: derive(#8B0000, 30%);
        -fx-border-color: #8B0000;
        -fx-border-width: 2;
        -fx-border-radius: 5;
        -fx-background-radius: 5;
      """
    }

    val startButton = new Button("Start Game") {
      style = "-fx-font-family: 'sans-serif'; " +
        "-fx-font-style: italic; " +
        "-fx-font-weight: bold; " +
        "-fx-font-size: 20pt; " +
        "-fx-background-color: linear-gradient(to bottom, #FF4500, #8B0000); " +
        "-fx-text-fill: white; " +
        "-fx-padding: 10 20; " +
        "-fx-background-radius: 10; " +
        "-fx-border-radius: 10;"
      effect = new DropShadow {
        color = Color.DarkRed
        radius = 10
      }
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
      spacing = 20
      alignment = Pos.TopCenter
      children = Seq(
        new Text("Setup") {
          style = "-fx-font: italic bold 100pt sans-serif"
          fill = new LinearGradient(
            endX = 0,
            stops = Stops(White, Color.LightGrey)
          )
          margin = Insets(30,0,0,0)
          effect = new DropShadow {
            color = Color.DarkRed
            radius = 15
            spread = 0.25
          }
        },
        playersInput,
        startButton
      )
    }

//    val backgroundGradient = new RadialGradient(
//      focusAngle = 0,
//      focusDistance = 0,
//      centerX = 0.5,
//      centerY = 0.5,
//      radius = 1,
//      proportional = true,
//      cycleMethod = CycleMethod.NoCycle,
//      stops = Seq(
//        Stop(0.0, Color.web("#FF4500")),
//        Stop(0.5, Color.web("#8B0000")),
//        Stop(1.0, Color.web("#8B0000"))
//      )
//    )

    new StackPane {
      children = Seq(
        setupImage,
        new StackPane {
          alignment = Pos.TopCenter
          padding = Insets(150,0,0,0)
          children = layout
        }
      )
    }
  }
}
