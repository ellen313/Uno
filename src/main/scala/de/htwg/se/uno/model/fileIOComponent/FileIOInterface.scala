package de.htwg.se.uno.model.fileIOComponent

trait FileIOInterface {

  def load(path: String = "game.json"): Any
  def save(gameState: Any, path: String = "game.json"): Unit
}
