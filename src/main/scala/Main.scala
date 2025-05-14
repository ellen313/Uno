import de.htwg.se.uno.aview.*

object Main {
  def main(args: Array[String]): Unit = {
    val game = UnoGame.runUno()
    UnoGame.inputLoop(new UnoTui(game))
  }
}
