package de.htwg.se.uno.controller.command

class CommandInvoker {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def executeCommand(command: Command): Unit = {
    command.execute()
    undoStack = command :: undoStack
    redoStack = Nil
  }

  def redoCommand(command: Command): Unit = {
    redoStack match {
      case last :: rest =>
        last.redo()
        redoStack = rest
        undoStack = last :: undoStack
      case Nil =>
        println("Nothing to redo.")
    }
  }

  def undoCommand(command: Command): Unit = {
    undoStack match {
      case last :: rest =>
        last.undo()
        undoStack = rest
        redoStack = last :: redoStack
      case Nil =>
        println("Nothing to undo.")
    }
  }
}
