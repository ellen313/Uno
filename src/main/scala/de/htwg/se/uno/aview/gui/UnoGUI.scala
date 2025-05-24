package de.htwg.se.uno.aview.gui

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.layout.HBox
import scalafx.scene.paint.LinearGradient

// properties: holds value whose changes can be observed:
//  -> can be bound together, when one changes, the other does too
//  -> binding expressions: when one of the component properties changes,
//                          the value of the whole expression changes too
//  -> BooleanProperty, StringProperty, IntegerProperty etc.
//  -> accessed with .value or ()

object UnoGUI extends JFXApp3 {

  override def start(): Unit = {
    stage = new PrimaryStage {
      title = "Uno"
      scene = new Scene {
        fill = Color.rgb(38, 38, 38)
        content = new HBox {
          padding = Insets(50, 80, 50, 80)
          children = Seq(
            new Text {
              text = "Uno"
              style = "-fx-font: normal bold 100pt sans-serif"
              fill = new LinearGradient(
                endX = 0,
                stops = Stops(Red, DarkRed)
              )
            }
          )
        }
      }
    }
  }
}
