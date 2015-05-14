/*
 * Copyright © 2012 Typesafe, Inc. All rights reserved.
 */

package com.typesafe.training.scalatrain

import org.joda.time.{DateTimeConstants, DateTime}

object TestData {
  val christmasDate = new DateTime(2015, 12, 24, 0, 0)
  val newyearDate = new DateTime(2015, 1, 1, 0, 0)

  val regularDatesIce724: Set[DayOfWeek] = Set(Monday, Tuesday, Wednesday, Thursday, Friday)
  val nonWorkingDatesIce724: Set[DateTime] = Set(christmasDate, newyearDate)
  val timeTableIce724 = TimeTable(regularDatesIce724, nonWorkingDatesIce724)

  val regularDatesIce726: Set[DayOfWeek] = Set(Wednesday, Thursday, Friday, Saturday)
  val nonWorkingDatesIce726: Set[DateTime] = Set(christmasDate)
  val timeTableIce726 = TimeTable(regularDatesIce726, nonWorkingDatesIce726)

  val regularDatesIce728: Set[DayOfWeek] = Set(Saturday, Sunday)
  val nonWorkingDatesIce728: Set[DateTime] = Set()
  val timeTableIce728 = TimeTable(regularDatesIce728, nonWorkingDatesIce728)

  val munich    = Station("Munich")
  val nuremberg = Station("Nuremberg")
  val frankfurt = Station("Frankfurt")
  val cologne   = Station("Cologne")
  val essen     = Station("Essen")

  val ice724MunichTime    = Time(8, 50)
  val ice724NurembergTime = Time(10)
  val ice724FrankfurtTime = Time(12, 10)
  val ice724CologneTime   = Time(13, 39)
  val ice724EssenTime     = Time(15)

  val ice728EssenTime     = Time(15, 30)
  val ice728NurembergTime = Time(16)

  val ice726MunichTime    = Time(7, 50)
  val ice726NurembergTime = Time(9)
  val ice726FrankfurtTime = Time(11, 10)
  val ice726EssenTime   = Time(13, 2)

  val ice724 = Train(
    InterCityExpress(724),
    Vector(
      ice724MunichTime    -> munich,
      ice724NurembergTime -> nuremberg,
      ice724FrankfurtTime -> frankfurt,
      ice724CologneTime   -> cologne,
      ice724EssenTime     -> essen
    ),
    timeTableIce724
  )

  val ice726 = Train(
    InterCityExpress(726),
    Vector(
      ice726MunichTime    -> munich,
      ice726NurembergTime -> nuremberg,
      ice726FrankfurtTime -> frankfurt,
      ice726EssenTime   -> essen
    ),
    timeTableIce726
  )

  val ice728 = Train(
    InterCityExpress(728),
    Vector(
      ice728EssenTime     -> essen,
      ice728NurembergTime -> nuremberg
    ),
    timeTableIce728
  )

  val planner = new JourneyPlanner(Set(ice724, ice726, ice728))

  val hopMunich2NurembergIce726    = Hop(munich, nuremberg, ice726, 5)
  val hopNuremberg2FrankfurtIce726 = Hop(nuremberg, frankfurt, ice726, 1)

  val hopMunich2NurembergIce724    = Hop(munich, nuremberg, ice724, 2)
  val hopNuremberg2FrankfurtIce724 = Hop(nuremberg, frankfurt, ice724, 3)
  val hopFrank2CologneIce724       = Hop(frankfurt, cologne, ice724, 4)
  val hopCologne2EssenIce724       = Hop(cologne, essen, ice724, 7)
  val hopEssen2NuremIce728         = Hop(essen, nuremberg, ice728, 8)

  val invalidPath = Seq(hopMunich2NurembergIce724, hopNuremberg2FrankfurtIce726) // due to departure time of 726
  val validPath   = Seq(hopMunich2NurembergIce726, hopNuremberg2FrankfurtIce724)

  val nonCyclePath = Seq(hopMunich2NurembergIce726, hopNuremberg2FrankfurtIce724)
  val cyclePath = Seq(hopMunich2NurembergIce724, hopNuremberg2FrankfurtIce724, hopFrank2CologneIce724, hopCologne2EssenIce724, hopEssen2NuremIce728)
  val cyclePath2 = Seq(hopMunich2NurembergIce724, hopNuremberg2FrankfurtIce724, hopFrank2CologneIce724, hopCologne2EssenIce724, hopEssen2NuremIce728, hopNuremberg2FrankfurtIce724, hopFrank2CologneIce724)

  val path1 = Seq(hopMunich2NurembergIce726, hopNuremberg2FrankfurtIce726, hopFrank2CologneIce724)
  val path2 = Seq(hopMunich2NurembergIce724, hopNuremberg2FrankfurtIce724, hopFrank2CologneIce724)

  val costIce724MunichNuremberg = 1.0
  val costIce724NurembergFrankfurt = 2.0
  val costIce724FrankfurtCologne = 3.0
  val costIce724CologneEssen = 4.0

  val costIce726MunichNuremberg = 3.0
  val costIce726NurembergFrankfurt = 2.0
  val costIce726FrankfurtEssen = 1.0

  val costIce728EssenNuremberg = 4.0


  val allCostIce724 = Map(
    (munich, nuremberg) -> costIce724MunichNuremberg,
    (nuremberg, frankfurt) -> costIce724NurembergFrankfurt,
    (frankfurt, cologne) -> costIce724FrankfurtCologne,
    (cologne, essen) -> costIce724CologneEssen
  )

  val allCostIce726 = Map(
    (munich, nuremberg) -> costIce726MunichNuremberg,
    (nuremberg, frankfurt) -> costIce726NurembergFrankfurt,
    (frankfurt, essen) -> costIce726FrankfurtEssen
  )

  val allCostIce728 = Map(
    (essen, nuremberg) -> costIce728EssenNuremberg
  )

  val allCost = Map(
    ice724 -> allCostIce724,
    ice726 -> allCostIce726,
    ice728 -> allCostIce728
  )
}
