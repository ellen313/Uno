package de.htwg.se.uno.controller.command

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.io.{ByteArrayOutputStream, PrintStream}

class CommandInvokerSpec extends AnyWordSpec with Matchers {

  "A CommandInvoker" should {

    "execute a command and add it to the undo stack" in {
      var executed = false

      val dummyCommand = new Command {
        override def execute(): Unit = executed = true
        override def undo(): Unit = ()
        override def redo(): Unit = ()
      }

      val invoker = new CommandInvoker
      invoker.executeCommand(dummyCommand)

      executed shouldBe true
    }

    "undo the last executed command and move it to the redo stack" in {
      var undone = false

      val dummyCommand = new Command {
        override def execute(): Unit = ()
        override def undo(): Unit = undone = true
        override def redo(): Unit = ()
      }

      val invoker = new CommandInvoker
      invoker.executeCommand(dummyCommand)
      invoker.undoCommand(dummyCommand)

      undone shouldBe true
    }

    "redo the last undone command and move it back to the undo stack" in {
      var redone = false

      val dummyCommand = new Command {
        override def execute(): Unit = ()
        override def undo(): Unit = ()
        override def redo(): Unit = redone = true
      }

      val invoker = new CommandInvoker
      invoker.executeCommand(dummyCommand)
      invoker.undoCommand(dummyCommand)
      invoker.redoCommand(dummyCommand)

      redone shouldBe true
    }

    "print 'Nothing to undo.' when undo stack is empty" in {
      val output = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(output)) {
        val invoker = new CommandInvoker
        invoker.undoCommand(new Command {
          override def execute(): Unit = ()
          override def undo(): Unit = ()
          override def redo(): Unit = ()
        })
      }

      output.toString should include("Nothing to undo.")
    }

    "print 'Nothing to redo.' when redo stack is empty" in {
      val output = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(output)) {
        val invoker = new CommandInvoker
        invoker.redoCommand(new Command {
          override def execute(): Unit = ()
          override def undo(): Unit = ()
          override def redo(): Unit = ()
        })
      }

      output.toString should include("Nothing to redo.")
    }
  }
}
