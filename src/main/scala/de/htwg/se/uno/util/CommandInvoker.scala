package de.htwg.se.uno.util

class CommandInvoker {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def executeCommand(command: Command): Unit = {
    command.execute()
    undoStack = command :: undoStack
    redoStack = Nil
  }

  def undoCommand(): Unit = {
    undoStack match {
      case Nil =>
        println("Nothing to undo.")
      case head :: stack => {
        head.undo()
        undoStack = stack
        redoStack = head :: redoStack
      }
    }
  }

  def redoCommand(): Unit = {
    redoStack match {
      case Nil =>
        println("Nothing to redo.")
      case head :: stack => {
        head.redo()
        redoStack = stack
        undoStack = head :: undoStack
      }  
    }
  }
}
