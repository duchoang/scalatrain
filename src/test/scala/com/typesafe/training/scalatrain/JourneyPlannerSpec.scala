/*
 * Copyright © 2012 Typesafe, Inc. All rights reserved.
 */

package com.typesafe.training.scalatrain

import java.lang.{IllegalArgumentException => IAE}

import com.typesafe.training.scalatrain.TestData._
import org.joda.time.DateTime
import org.scalatest.{Matchers, WordSpec}

class JourneyPlannerSpec extends WordSpec with Matchers {

  "stations" should {
    "be initialized correctly" in {
      planner.stations shouldEqual Set(munich, nuremberg, frankfurt, cologne, essen, stockport)
    }
  }

  "Calling trainsAt" should {
    "return the correct trains" in {
      planner.trainsAt(munich)  shouldEqual Set(ice724, ice726)
      planner.trainsAt(cologne) shouldEqual Set(ice724)
    }
  }

  "Calling stopsAt" should {
    "return the correct start" in {
      planner.stopsAt(munich) shouldEqual Set(ice724MunichTime -> ice724, ice726MunichTime -> ice726)
    }

    "return the correct stops" in {
      planner.stopsAt(nuremberg) shouldEqual Set(ice724NurembergTime -> ice724, ice726NurembergTime -> ice726, ice728NurembergTime -> ice728)
    }
  }

  "Calling isShortTrip" should {
    "return false for more than one station in between" in {
      planner.isShortTrip(munich, cologne) shouldBe false
      planner.isShortTrip(munich, essen)   shouldBe false
    }

    "return true for one stations in between" in {
      planner.isShortTrip(munich, nuremberg)    shouldBe true
      planner.isShortTrip(munich, frankfurt)    shouldBe true
      planner.isShortTrip(nuremberg, frankfurt) shouldBe true
      planner.isShortTrip(nuremberg, essen)     shouldBe true
    }

    "return false for zero stations (i.e. same station) in between" in {
      planner.isShortTrip(munich, munich)       shouldBe false
      planner.isShortTrip(nuremberg, nuremberg) shouldBe false
    }
  }

  "Calling allMappingHops" should {
    "return all hops of all trains, grouped by the departing station" in {
      planner.allMappingHops(allCost) shouldEqual Map(
        munich    -> Set(Hop(munich, nuremberg, ice724, costIce724MunichNuremberg), Hop(munich, nuremberg, ice726, costIce726MunichNuremberg)),
        nuremberg -> Set(Hop(nuremberg, frankfurt, ice724, costIce724NurembergFrankfurt), Hop(nuremberg, frankfurt, ice726, costIce726NurembergFrankfurt), Hop(nuremberg, stockport, ice728, costIce728NurembergStockport)),
        frankfurt -> Set(Hop(frankfurt, cologne, ice724, costIce724FrankfurtCologne), Hop(frankfurt, essen, ice726, costIce726FrankfurtEssen)),
        cologne   -> Set(Hop(cologne, essen, ice724, costIce724CologneEssen)),
        essen     -> Set(Hop(essen, nuremberg, ice728, costIce728EssenNuremberg))
      )
    }
  }

  "Calling allConnections" should {
    val pathMunichEssen = planner.allConnections(munich, essen, Time(8), allCost)
    val path1 = Seq(
      Hop(munich, nuremberg, ice726, costIce726MunichNuremberg),
      Hop(nuremberg, frankfurt, ice726, costIce726NurembergFrankfurt),
      Hop(frankfurt, essen, ice726, costIce726FrankfurtEssen)
    )
    val path2 = Seq(
      Hop(munich, nuremberg, ice724, costIce724MunichNuremberg),
      Hop(nuremberg, frankfurt, ice724, costIce724NurembergFrankfurt),
      Hop(frankfurt, cologne, ice724, costIce724FrankfurtCologne),
      Hop(cologne, essen, ice724, costIce724CologneEssen)
    )
    val path3 = Seq(
      Hop(munich, nuremberg, ice726, costIce726MunichNuremberg),
      Hop(nuremberg, frankfurt, ice726, costIce726NurembergFrankfurt)
    )
    val path4 = Seq(
      Hop(munich, nuremberg, ice724, costIce724MunichNuremberg),
      Hop(nuremberg, frankfurt, ice724, costIce724NurembergFrankfurt)
    )
    val pathMunichFrankfurt = planner.allConnections(munich, frankfurt, Time(8), allCost)

    "return all possible paths between 2 stations" in {
      pathMunichEssen foreach (path => {
        Hop.checkPathValid(path) shouldBe true
        Hop.containCycle(path)   shouldBe false
      })

      pathMunichEssen.contains(path1) shouldBe false
      pathMunichEssen shouldEqual Set(path2)

      pathMunichFrankfurt.contains(path3) shouldBe false
      pathMunichFrankfurt.contains(path4) shouldBe true
    }

    "return an empty Set for reverse paths between 2 stations" in {
      planner.allConnections(frankfurt, munich, Time(), allCost) shouldEqual Set()
    }
  }

  "Calling sortPathsByTotalTime" should {
    "return the correct order of 2 paths" in {
      Hop.checkPathValid(path1) shouldBe true
      Hop.checkPathValid(path2) shouldBe true
      val totalTimeOfPath1 = JourneyPlanner.calculateTotalTime(path1)
      val totalTimeOfPath2 = JourneyPlanner.calculateTotalTime(path2)
      val (paths, times) = JourneyPlanner.sortPathsByTotalTime(Set(path1, path2)).unzip
      paths shouldEqual List(path2, path1)
      times shouldEqual List(totalTimeOfPath2, totalTimeOfPath1)
    }
  }

  "Calling sortPathsByTotalCost" should {
    "return the correct order of 2 paths" in {
      Hop.checkPathValid(path1) shouldBe true
      Hop.checkPathValid(path2) shouldBe true
      val totalCostOfPath1 = JourneyPlanner.calculateTotalCost(path1)
      val totalCostOfPath2 = JourneyPlanner.calculateTotalCost(path2)
      val (paths, costs) = JourneyPlanner.sortPathsByTotalCost(Set(path1, path2)).unzip
      paths shouldEqual List(path2, path1)
      costs shouldEqual List(totalCostOfPath2, totalCostOfPath1)
    }
  }

  "Calling trainsOn" should {
    "return the list of train working on given date" in {
      planner.trainsOnDate(new DateTime(2015, 5, 13, 0, 0)) shouldEqual Set(ice724, ice726)
      planner.trainsOnDate(new DateTime(2015, 5, 11, 0, 0)) shouldEqual Set(ice724)
      planner.trainsOnDate(new DateTime(2015, 5, 16, 0, 0)) shouldEqual Set(ice726, ice728)
      planner.trainsOnDate(new DateTime(2015, 5, 17, 0, 0)) shouldEqual Set(ice728)
    }
    "return the list of train working on holiday" in {
      planner.trainsOnDate(christmasDate) shouldEqual Set(ice728)
      planner.trainsOnDate(newyearDate) shouldEqual Set(ice726, ice728)
    }
  }

  "Calling findRoute" should {
    "return all possible paths between 2 stations given a DateTime" in {
      val expectedPath = Seq(
        Hop(munich, nuremberg, ice724, costIce724MunichNuremberg),
        Hop(nuremberg, frankfurt, ice724, costIce724NurembergFrankfurt),
        Hop(frankfurt, cologne, ice724, costIce724FrankfurtCologne),
        Hop(cologne, essen, ice724, costIce724CologneEssen)
      )
      planner.findRoute(munich, essen, new DateTime(2015, 6, 4, 8, 0), allCost) shouldEqual Set(expectedPath)
      planner.findRoute(munich, essen, new DateTime(2015, 6, 5, 8, 0), allCost) shouldEqual Set(expectedPath)
      planner.findRoute(munich, essen, new DateTime(2015, 6, 6, 8, 0), allCost) shouldEqual Set()
    }
  }

  "Calling sinkStations" should {
    "return all sink stations" in {
      planner.sinkStations shouldEqual Set(stockport)
    }
  }

}
