/*
 * Copyright © 2012 Typesafe, Inc. All rights reserved.
 */

package com.typesafe.training.scalatrain

import TestData._
import java.lang.{ IllegalArgumentException => IAE }
import org.scalatest.{ Matchers, WordSpec }

class TrainSpec extends WordSpec with Matchers {

  "Train ice724" should {
    "stop in Nurember" in {
      ice724.timeAt(nuremberg) shouldEqual Some(ice724NurembergTime)
    }
    "stop in Essen" in {
      ice724.timeAt(essen) shouldEqual Some(ice724EssenTime)
    }
  }

  "Train ice726" should {
    "stop in Munich" in {
      ice726.timeAt(munich) shouldEqual Some(ice726MunichTime)
    }
    "not stop in Cologne" in {
      ice726.timeAt(cologne) shouldEqual None
    }
  }

  "Creating a Train" should {
    "throw an IllegalArgumentException for a schedule with 0 or 1 elements" in {
      an[IAE] should be thrownBy Train(InterCityExpress(724), Vector())
      an[IAE] should be thrownBy Train(InterCityExpress(724), Vector(ice724MunichTime -> munich))
    }
  }

  "stations" should {
    "be initialized correctly" in {
      ice724.stations shouldEqual Vector(munich, nuremberg, frankfurt, cologne, essen)
    }
  }
  "backToBack" should {
    "the correct pair of stations" in {
      ice724.backToBackStations shouldEqual Seq((munich, nuremberg),(nuremberg, frankfurt),(frankfurt, cologne),(cologne, essen))
    }
  }

  "departureTimes" should {
    "the correct pair of departure time and station" in {
      ice724.departureTimes shouldEqual Vector((ice724MunichTime, munich), (ice724NurembergTime,nuremberg), (ice724FrankfurtTime, frankfurt), (ice724CologneTime, cologne))
    }
  }

  "allHops" should {
    "the correct Hop" in {
      ice724.allHops shouldEqual Vector(Hop(munich, nuremberg, ice724), Hop(nuremberg, frankfurt, ice724), Hop(frankfurt, cologne, ice724), Hop(cologne, essen, ice724))
    }
  }


}
