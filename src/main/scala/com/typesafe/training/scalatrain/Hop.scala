package com.typesafe.training.scalatrain

case class Hop(from: Station, to: Station, train: Train) {
  require(train.backToBackStations.nonEmpty)
  require(train.backToBackStations.contains((from, to)))

  def departureAndArrivalTime: (Time, Time) = {
    val fromSchedule = train.schedule.find(timeStation => timeStation._2 == from)
    val toSchedule = train.schedule.find(timeStation => timeStation._2 == to)
    assert(fromSchedule.nonEmpty)
    assert(toSchedule.nonEmpty)
    (fromSchedule.get._1, toSchedule.get._1)
  }

  def departureAndArrivalTime2: (Time, Time) = (for {
    (fromTime, fromStation) <- train.schedule if fromStation == from
    (toTime, toStation)     <- train.schedule if toStation == to
  } yield (fromTime, toTime)).head

}
