package de.htwg.se.uno.model.gameComponent

import de.htwg.se.uno.model.gameComponent.InputResult
import de.htwg.se.uno.model.gameComponent.base.GameState

sealed trait InputResult

case class Success(game: GameStateInterface) extends InputResult

case class Failure(reason: String) extends InputResult

