package de.htwg.se.uno.controller.command

trait Command {
  def execute(): Unit
  def undo(): Unit
  def redo(): Unit
}
