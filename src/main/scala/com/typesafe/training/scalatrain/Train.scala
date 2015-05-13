package com.typesafe.training.scalatrain

import scala.collection.immutable.Seq

case class Train(info: TrainInfo, schedule: Seq[(Time, Station)]) {
  require(schedule.size >= 2, "schedule must contain at least two elements")
  // TODO Verify that `schedule` is strictly increasing in time

  val stations: Seq[Station] = schedule.map(_._2)

  def timeAt(station: Station): Option[Time] =
    schedule.find(s => s._2 == station).map(_._1)

  val backToBackStations: Seq[(Station, Station)] =
    stations zip stations.tail

  val departureTimes: Seq[(Time, Station)] = for {
    timeAndStation <- schedule.init
  } yield timeAndStation._1 -> timeAndStation._2

  val allHops: Seq[Hop] = this.backToBackStations map {
    case (from, to) => Hop(from, to, this)
  }

  override val toString = info.toString
}
