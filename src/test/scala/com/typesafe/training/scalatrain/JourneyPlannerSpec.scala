/*
 * Copyright © 2012 Typesafe, Inc. All rights reserved.
 */

package com.typesafe.training.scalatrain

import TestData._
import java.lang.{ IllegalArgumentException => IAE }
import org.scalatest.{ Matchers, WordSpec }

class JourneyPlannerSpec extends WordSpec with Matchers {

  "stations" should {
    "be initialized correctly" in {
      planner.stations shouldEqual Set(munich, nuremberg, frankfurt, cologne, essen)
    }
  }

  "Calling trainsAt" should {
    "return the correct trains" in {
      planner.trainsAt(munich) shouldEqual Set(ice724, ice726)
      planner.trainsAt(cologne) shouldEqual Set(ice724)
    }
  }

  "Calling stopsAt" should {
    "return the correct stops" in {
      planner.stopsAt(munich) shouldEqual Set(ice724MunichTime -> ice724, ice726MunichTime -> ice726)
    }
  }

  "Calling isShortTrip" should {
    "return false for more than one station in between" in {
      planner.isShortTrip(munich, cologne) shouldBe false
      planner.isShortTrip(munich, essen) shouldBe false
    }
    "return true for zero or one stations in between" in {
      planner.isShortTrip(munich, nuremberg) shouldBe true
      planner.isShortTrip(munich, frankfurt) shouldBe true
      planner.isShortTrip(nuremberg, frankfurt) shouldBe true
      planner.isShortTrip(nuremberg, essen) shouldBe true
    }
  }

  "Calling allMappingHops" should {
    "return all hops of all trains, grouped by the departing station" in {
      planner.allMappingHops shouldEqual Map(
        munich -> Set(Hop(munich, nuremberg, ice724), Hop(munich, nuremberg, ice726)),
        nuremberg -> Set(Hop(nuremberg, frankfurt, ice724), Hop(nuremberg, frankfurt, ice726)),
        frankfurt -> Set(Hop(frankfurt, cologne, ice724), Hop(frankfurt, essen, ice726)),
        cologne -> Set(Hop(cologne, essen, ice724)),
        essen -> Set(Hop(essen, nuremberg, ice728))
      )
    }
  }

  "Calling allConnections" should {
    "return all possible paths between 2 stations" in {
      val pathMunichEssen = planner.allConnections(munich, essen, Time(8))
      val path1 = Seq(
        Hop(munich, nuremberg, ice726),
        Hop(nuremberg, frankfurt, ice726),
        Hop(frankfurt, essen, ice726)
      )
      val path2 = Seq(
        Hop(munich, nuremberg, ice724),
        Hop(nuremberg, frankfurt, ice724),
        Hop(frankfurt, cologne, ice724),
        Hop(cologne, essen, ice724)
      )

      pathMunichEssen foreach (path => {
        Hop.checkPathValid(path) shouldBe true
        Hop.containCycle(path) shouldBe false
      })

      pathMunichEssen.contains(path1) shouldBe false
      pathMunichEssen shouldEqual Set(path2)

      val path3 = Seq(
        Hop(munich, nuremberg, ice726),
        Hop(nuremberg, frankfurt, ice726)
      )
      val path4 = Seq(
        Hop(munich, nuremberg, ice724),
        Hop(nuremberg, frankfurt, ice724)
      )
      val pathMunichFrankfurt = planner.allConnections(munich, frankfurt, Time(8))
      pathMunichFrankfurt.contains(path3) shouldBe false
      pathMunichFrankfurt.contains(path4) shouldBe true

      planner.allConnections(frankfurt, munich, Time()) shouldEqual Set()

    }
  }
}
