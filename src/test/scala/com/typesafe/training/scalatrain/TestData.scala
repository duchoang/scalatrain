/*
 * Copyright © 2012 Typesafe, Inc. All rights reserved.
 */

package com.typesafe.training.scalatrain

object TestData {

  val munich = Station("Munich")

  val nuremberg = Station("Nuremberg")

  val frankfurt = Station("Frankfurt")

  val cologne = Station("Cologne")

  val essen = Station("Essen")

  val ice724MunichTime = Time(8, 50)

  val ice724NurembergTime = Time(10)

  val ice724FrankfurtTime = Time(12, 10)

  val ice724CologneTime = Time(13, 39)

  val ice724EssenTime = Time(15)

  val ice728EssenTime = Time(15, 30)

  val ice728NurembergTime = Time(16)

  val ice726MunichTime = Time(7, 50)

  val ice726NurembergTime = Time(9)

  val ice726FrankfurtTime = Time(11, 10)

  val ice726CologneTime = Time(13, 2)

  val ice724 = Train(
    InterCityExpress(724),
    Vector(
      ice724MunichTime -> munich,
      ice724NurembergTime -> nuremberg,
      ice724FrankfurtTime -> frankfurt,
      ice724CologneTime -> cologne,
      ice724EssenTime -> essen
    )
  )

  val ice728 = Train(
    InterCityExpress(728),
    Vector(
      ice728EssenTime -> essen,
      ice728NurembergTime -> nuremberg
    )
  )

  val ice726 = Train(
    InterCityExpress(726),
    Vector(
      ice726MunichTime -> munich,
      ice726NurembergTime -> nuremberg,
      ice726FrankfurtTime -> frankfurt,
      ice726CologneTime -> essen
    )
  )

  val planner = new JourneyPlanner(Set(ice724, ice726, ice728))

  val hopMunich2NurembergIce726 = Hop(munich, nuremberg, ice726)
  val hopNuremberg2FrankfurtIce726 = Hop(nuremberg, frankfurt, ice726)

  val hopMunich2NurembergIce724 = Hop(munich, nuremberg, ice724)
  val hopNuremberg2FrankfurtIce724 = Hop(nuremberg, frankfurt, ice724)
  val hopFrank2ColoIce724 = Hop(frankfurt, cologne, ice724)
  val hopColo2EssenIce724 = Hop(cologne, essen, ice724)
  val hopEssen2NuremIce728 = Hop(essen, nuremberg, ice728)

  val invalidPath = Seq(hopMunich2NurembergIce724, hopNuremberg2FrankfurtIce726)
  val validPath = Seq(hopMunich2NurembergIce726, hopNuremberg2FrankfurtIce724)

  val nonCyclePath = Seq(hopMunich2NurembergIce726, hopNuremberg2FrankfurtIce724)
  val cyclePath = Seq(hopMunich2NurembergIce724, hopNuremberg2FrankfurtIce724, hopFrank2ColoIce724, hopColo2EssenIce724, hopEssen2NuremIce728)
  val cyclePath2 = Seq(hopMunich2NurembergIce724, hopNuremberg2FrankfurtIce724, hopFrank2ColoIce724, hopColo2EssenIce724, hopEssen2NuremIce728, hopNuremberg2FrankfurtIce724, hopFrank2ColoIce724)
}
