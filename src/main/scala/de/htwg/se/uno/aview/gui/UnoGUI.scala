package de.htwg.se.uno.aview.gui

import de.htwg.se.uno.aview.UnoGame
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, ButtonType}
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint.Color.*
import scalafx.scene.paint.LinearGradient
import scalafx.scene.paint.*
import scalafx.scene.text.Text

// properties: holds value whose changes can be observed:
//  -> can be bound together, when one changes, the other does too
//  -> binding expressions: when one of the component properties changes,
//                          the value of the whole expression changes too
//  -> BooleanProperty, StringProperty, IntegerProperty etc.
//  -> accessed with .value or ()

object UnoGUI extends JFXApp3 {

  def startGame(): Unit = {
    val tui = UnoGame.runUno()
    UnoGame.inputLoop(tui)
  }

  override def start(): Unit = {
    stage = new PrimaryStage {
      title = "Uno"
      scene = new Scene {
        fill = Color.rgb(40, 40, 40)
        content = new VBox {
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
              onAction = _ => {
                new Thread(() => startGame()).start()
              }
            }
          )
        }
      }
    }
  }
}
