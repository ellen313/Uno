package de.htwg.se.uno.model.fileIOComponent

import de.htwg.se.uno.model.gameComponent.GameStateInterface
import de.htwg.se.uno.model.gameComponent.base.GameState
import play.api.libs.json.Json
import de.htwg.se.uno.model.JsonFormats.*
import java.io.{File, PrintWriter}

trait FileIOInterface {

  def save(gameState: GameStateInterface): Unit = {
    val game = gameState.asInstanceOf[GameState]
    val pw = new PrintWriter(new File("gamestate.json"))
    pw.write(Json.prettyPrint(Json.toJson(game)))
    pw.close()
  }

  def load(): GameStateInterface = {
    val source = scala.io.Source.fromFile("gamestate.json")
    val content = source.mkString
    source.close()
    Json.parse(content).as[GameState]
  }
}
