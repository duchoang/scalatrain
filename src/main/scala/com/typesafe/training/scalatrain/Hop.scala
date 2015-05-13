package com.typesafe.training.scalatrain

case class Hop(from: Station, to: Station, train: Train) {
  require(train.backToBackStations.nonEmpty)
  require(train.backToBackStations.contains((from, to)))

  def calculateDepArrTime: (Time, Time) = {
    val fromSchedule = train.schedule.find(timeStation => timeStation._2 == from)
    val toSchedule = train.schedule.find(timeStation => timeStation._2 == to)
    assert(fromSchedule.nonEmpty)
    assert(toSchedule.nonEmpty)
    (fromSchedule.get._1, toSchedule.get._1)
  }

}
