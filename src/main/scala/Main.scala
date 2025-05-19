import de.htwg.se.uno.aview.*

object Main {
  def main(args: Array[String]): Unit = {
    val tui = UnoGame.runUno()
    UnoGame.inputLoop(tui)
  }
}
