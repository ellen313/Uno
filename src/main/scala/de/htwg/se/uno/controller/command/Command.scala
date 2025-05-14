package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model.GameState

trait Command {
  def execute(): Unit
}
