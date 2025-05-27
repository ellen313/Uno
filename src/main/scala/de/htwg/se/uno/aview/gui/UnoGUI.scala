package de.htwg.se.uno.aview.gui

import de.htwg.se.uno.aview.UnoGame
import de.htwg.se.uno.util.Observer
import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.model.PlayerHand
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.Includes.jfxScene2sfx
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

object UnoGUI extends JFXApp3 with Observer{

  override def update(): Unit = {
    println("Observer update called: Gamestate changed.")
  }

  override def start(): Unit = {
    GameBoard.addObserver(this)

    stage = new PrimaryStage {
      title = "Uno"
      scene = new Scene {
        fill = Color.rgb(40, 40, 40)
        content = StartScreen(() => {
          stage.scene().root = SetupScreen(stage)
        })
      }
    }
  }

  def startGame(players: Int, cards: Int): Unit = {
    new Thread(() => {
      val tui = UnoGame.runUno(Some(players), cards)
    }).start()
  }

  /*Switches to SetupScreen, when the Button in StartScreen is clicked*/
//  def showSetupScreen(): PrimaryStage = {
//    stage.scene().root = SetupScreen { (players: Int, cards: Int) =>
//      println(s"Start the Game with $players players and $cards cards")
//      startGame(players, cards)
//    }
//  }
}
