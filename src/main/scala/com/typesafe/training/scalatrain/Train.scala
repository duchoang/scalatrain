package com.typesafe.training.scalatrain

import scala.collection.immutable.Seq

case class Train(info: TrainInfo, schedule: Seq[(Time, Station)]) {
  require(schedule.size >= 2, "Schedule should contain at least two elements")

  //TODO Verify that schedule is strictly increasing in time

  val stations: Seq[Station] = schedule.map(_._2)

  def timeAt(station: Station): Option[Time] = {
    schedule.find(s => s._2 == station).map(_._1)
  }
}


sealed abstract class TrainInfo {
  def number: Int
}

case class InterCityExpress(number: Int, hasWifi: Boolean = false) extends TrainInfo

case class RegionalExpress(number: Int) extends TrainInfo

case class BavarianRegional(number: Int) extends TrainInfo
