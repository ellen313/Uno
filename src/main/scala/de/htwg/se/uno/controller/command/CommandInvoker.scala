package de.htwg.se.uno.controller.command

class CommandInvoker {
  private var history: List[Command] = Nil

  def executeCommand(command: Command): Unit = {
    command.execute()
    history = command :: history
  }
}
