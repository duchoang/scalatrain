package com.typesafe.training.scalatrain

import TestData._
import org.scalatest.{Matchers, WordSpec}

class HopSpec extends WordSpec with Matchers {

  "new Hope" should { // star wars
    "throw an exception for empty train schedule" in {
      an[IllegalArgumentException] should be thrownBy Hop(ice724.stations.head, ice724.stations.head, new Train(InterCityExpress(724), Vector()), 1)
    }
    "throw an exception for a train with stations that aren't backtoback" in {
      an[IllegalArgumentException] should be thrownBy Hop(ice726.stations.head, ice726.stations.last, ice726, 1)
    }
  }

  "departureAndArrivalTime" should {
    "return the correct arrival and departure times" in {
      val fromStation = ice724.schedule.head
      val toStation = ice724.schedule.tail.head // get the second element
      val hop = Hop(fromStation._2, toStation._2, ice724, costIce724MunichNuremberg)

      hop.departureAndArrivalTime2.shouldEqual (fromStation._1, toStation._1)
    }
  }

  "toString" should {
    val fromStation = ice724.schedule.head
    val toStation = ice724.schedule.tail.head
    val hop = Hop(fromStation._2, toStation._2, ice724, costIce724MunichNuremberg)
    "have the correct format" in {
      hop.toString shouldEqual "Hop[Munich to Nuremberg]"
    }
  }

  "checkPathValid" should {
    "return true if the valid path" in {
      Hop.checkPathValid(validPath) shouldBe true
    }

    "return false if the invalid path" in {
      Hop.checkPathValid(invalidPath) shouldBe false
      Hop.checkPathValid(Seq()) shouldBe false
    }
  }

  "containCycle" should {
    "return true if contains a cyclic path" in {
      Hop.containCycle(cyclePath) shouldBe true
      Hop.containCycle(cyclePath2) shouldBe true
    }

    "return true if contains a cyclic path with a visited station" in {
      Hop.containCycle(cyclePath, Set(munich)) shouldBe true
    }

    "return true if contains a non-cyclic path with a visted station" in {
      Hop.containCycle(nonCyclePath, Set(munich)) shouldBe true
    }

    "return false if contains a non-cyclic path" in {
      Hop.containCycle(nonCyclePath) shouldBe false
    }

    "return false if the is no cycle" in {
      Hop.containCycle(Seq()) shouldBe false
    }
  }
}
