package de.htwg.se.uno.model.fileIOComponent.fileIOJSON

import de.htwg.se.uno.model.fileIOComponent.FileIOInterface
import de.htwg.se.uno.model.gameComponent.GameStateInterface

import java.io.{File, PrintWriter}

class FileIO extends FileIOInterface {
  override def save(gameState: Any, path: String = "game.json"): Unit = {
    gameState match {
      case state: GameStateInterface =>
        val json: JsValue = Json.toJson(state)
        val writer = new PrintWriter(new File(path))
        writer.write(Json.prettyPrint(json))
        writer.close()
      case _ => throw new IllegalArgumentException("Unsupported type")
    }
  }

  override def load(path: String = "game.json"): Any = {
    val source = Source.fromFile(path)
    val content = try source.getLines().mkString finally source.close()

    Json.parse(content).as[GameStateInterface]
  }
}
