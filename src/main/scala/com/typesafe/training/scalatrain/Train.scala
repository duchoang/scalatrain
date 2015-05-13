package com.typesafe.training.scalatrain

import scala.collection.immutable.Seq

case class Train(info: TrainInfo, schedule: Seq[(Time, Station)]) {
  require(schedule.size >= 2, "schedule must contain at least two elements")
  // TODO Verify that `schedule` is strictly increasing in time

  val stations: Seq[Station] = schedule.map(_._2)

  def timeAt(station: Station): Option[Time] =
    schedule.find(s => s._2 == station).map(_._1)

  def backToBackStations: Seq[(Station, Station)] =
    stations zip stations.tail

  def departureTimes = for {
    timeAndStation <- schedule
  } yield timeAndStation._1 -> timeAndStation._2

}
