import de.htwg.se.uno.aview.UnoGame
import org.scalatest.wordspec.AnyWordSpec

class MainSpec extends AnyWordSpec {

  "The Main object" should {
    "initialize the game and deal cards correctly" in {
      
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        val gameBoard = UnoGame.runUno(Some(2), 7)
        assert(gameBoard.gameState.players.length == 2)
        assert(gameBoard.gameState.players.forall(_.cards.length == 7))
      }
      val output = out.toString()
      assert(output.contains("Player 1's turn"))
    }
  }
}
