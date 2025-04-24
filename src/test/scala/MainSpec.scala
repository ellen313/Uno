import org.scalatest.wordspec.AnyWordSpec

class MainSpec extends AnyWordSpec {

  "The Main object" should {
    "initialize the game and deal cards correctly" in {
      
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        
        Main.main(Array())
        
        val output = out.toString()
        
        assert(output.contains("player 1's hand:"))
        assert(output.contains("player 2's hand:"))
      }
    }
  }
}
