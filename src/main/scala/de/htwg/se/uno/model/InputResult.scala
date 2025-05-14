package de.htwg.se.uno.model

sealed trait InputResult

case class Success(game: GameState) extends InputResult

case class Failure(reason: String) extends InputResult

