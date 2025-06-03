package de.htwg.se.uno.aview.gui

import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.control.Button
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Cursor
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color

object StartScreen {

  def apply(onStart: () => Unit): StackPane = {
    val startImage = new ImageView(new Image("file:src/main/resources/UnoLogo.jpeg")) {
      fitWidth = 1400
      fitHeight = 1000
      preserveRatio = true
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
//        Stop(0.0, Color.web("#FFA500")),    // Hellorange (Mitte)
//        Stop(0.5, Color.web("#FF4500")),    // Mittelorange
//        Stop(1.0, Color.web("#8B0000"))     // Dunkelrot (Rand)
//      )
//    )

    new StackPane {
      children = Seq(
//        new Rectangle {
//          width = 1200
//          height = 850
//          fill = backgroundGradient
//        },
        startImage,
        new VBox {
          spacing = 30
          padding = Insets(0, 0, 50, 0)
          alignment = Pos.BottomCenter
          children = Seq(
            new Button("Start Game") {
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

              cursor = Cursor.Hand

              onAction = _ => onStart()
            }
          )
        }
      )
    }
  }
}
