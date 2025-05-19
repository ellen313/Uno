package de.htwg.se.uno.util

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class ObserverSpec extends AnyWordSpec {
  "An Observable" when {
    "an observer is added and notified" should {
      "trigger the observer's update method" in {
        var wasUpdated = false

        val observer = new Observer {
          override def update(): Unit = {
            wasUpdated = true
          }
        }

        val observable = new Observable {}

        observable.addObserver(observer)
        observable.notifyObservers()

        wasUpdated shouldBe true
      }
    }

    "an observer is removed" should {
      "not be notified" in {
        var wasUpdated = false

        val observer = new Observer {
          override def update(): Unit = {
            wasUpdated = true
          }
        }

        val observable = new Observable {}

        observable.addObserver(observer)
        observable.removeObserver(observer)
        observable.notifyObservers()

        wasUpdated shouldBe false
      }
    }
  }
}