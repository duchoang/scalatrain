package com.typesafe.training.scalatrain

import TestData._
import org.scalatest.{Matchers, WordSpec}

class HopSpec extends WordSpec with Matchers {

  "new Hope" should {
    "throw an exception for empty train schedule" in {
      an[IllegalArgumentException] should be thrownBy Hop(ice724.stations.head, ice724.stations.head, new Train(InterCityExpress(724), Vector()))
    }
    "throw an exception for a train with stations that aren't backtoback" in {
      an[IllegalArgumentException] should be thrownBy Hop(ice726.stations.head, ice726.stations.last, ice726)
    }
  }

  "departureAndArrivalTime" should {
    "return the correct arrival and departure times" in {
      val fromStation = ice724.schedule.head
      val toStation = ice724.schedule.tail.head
      val hop = Hop(fromStation._2, toStation._2, ice724)
      hop.departureAndArrivalTime2.shouldEqual (fromStation._1, toStation._1)
    }

  }
}