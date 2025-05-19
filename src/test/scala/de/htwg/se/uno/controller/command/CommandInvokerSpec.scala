package de.htwg.se.uno.controller.command

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CommandInvokerSpec extends AnyWordSpec with Matchers {

  "A CommandInvoker" should {

    "execute a command and add it to history" in {
      var executed = false

      val dummyCommand = new Command {
        override def execute(): Unit = {
          executed = true
        }
      }

      val invoker = new CommandInvoker
      invoker.executeCommand(dummyCommand)

      executed shouldBe true
    }
  }
}
