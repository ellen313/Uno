import org.scalatest.wordspec.AnyWordSpec

class MainSpec extends AnyWordSpec {

  "The Main object" should {
    "initialize the game and deal cards correctly" in {
      
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        val gameState = Main.runUno(Some(2), 7)
        assert(gameState.players.length == 2)
        assert(gameState.players.forall(_.cards.length == 7))
      }
        //Main.main(Array())
      val output = out.toString()
      assert(output.contains("Player 1's turn"))
    }
  }
}
