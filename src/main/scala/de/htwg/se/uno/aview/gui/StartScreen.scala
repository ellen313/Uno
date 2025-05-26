package de.htwg.se.uno.aview.gui

import scalafx.scene.layout.VBox
import scalafx.scene.text.Text
import scalafx.scene.control.Button
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.effect.DropShadow
import scalafx.scene.paint.Color.*
import scalafx.scene.paint.{LinearGradient, Stops}

object StartScreen {

  def apply(onStart: () => Unit): VBox = {
    new VBox {
      spacing = 30
      padding = Insets(50, 80, 50, 80)
      alignment = Pos.Center
      children = Seq(
        new Text {
          text = "Uno"
          style = "-fx-font: italic bold 100pt sans-serif"
          fill = new LinearGradient(
            endX = 0,
            stops = Stops(Red, DarkRed)
          )
          effect = new DropShadow {
            color = IndianRed
            radius = 15
            spread = 0.25
          }
        },
        new Button("Start Game") {
          style = "fx-font: italic bold; -fx-font-size: 20pt; -fx-background-color: #b22222; -fx-text-fill: white;"
          onAction = _ => onStart()
        }
      )
    }
  }
}
