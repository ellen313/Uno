import de.htwg.se.uno.aview.UnoTUI
import de.htwg.se.uno.aview.gui.UnoGUI
import de.htwg.se.uno.aview.UnoGame
import de.htwg.se.uno.controller.controllerComponent.base.GameBoard

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main {
  def main(args: Array[String]): Unit = {
    val gameBoard = new GameBoard()

    val gui =  new UnoGUI(gameBoard)
    gameBoard.addObserver(gui)

    Future {
      gui.main(args)
    }

    while (gameBoard.gameState.isFailure) {
      Thread.sleep(100)
    }

    val tui = new UnoTUI(gameBoard)
    gameBoard.addObserver(tui)

    UnoGame.inputLoop(tui)
  }
}
