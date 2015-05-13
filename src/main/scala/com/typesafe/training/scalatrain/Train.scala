package com.typesafe.training.scalatrain

case class Train(info: TrainInfo, schedule: Seq[(Time, Station)]) {
  require(schedule.size >= 2, "schedule must contain at least two elements")
  // TODO Verify that `schedule` is strictly increasing in time

  val stations: Seq[Station] =
    schedule.map(trainAndStation => trainAndStation._2)

  def timeAt(station: Station): Option[Time] =
    schedule.find(timeAndStation => timeAndStation._2 == station).map(found => found._1)

  def back2Back = stations.init zip stations.tail

  def departureTimes = for {
    timeAndStation <- schedule
  } yield timeAndStation._1 -> timeAndStation._2
}
