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
    (toTime, toStation) <- train.schedule if toStation == to
  } yield (fromTime, toTime)).head

  override def toString = {
    val (depart, arr) = this.departureAndArrivalTime
//    s"Hop[$from to $to] with train $train from $depart->$arr"
    s"Hop[$from to $to]"
  }
}

object Hop {
  def checkPathValid(path: Seq[Hop]): Boolean = path match {
    case hop1 +: hop2 +: rest =>
      val (_, arr1) = hop1.departureAndArrivalTime
      val (dep2, _) = hop2.departureAndArrivalTime
      arr1 <= dep2 && checkPathValid(hop2 +: rest)
    case _ => true
  }

  def containCycle(path: Seq[Hop], visitedStation: Seq[Station] = Seq()): Boolean = {
    path match {
      case hop +: Nil =>
        visitedStation.contains(hop.to)
      case hop +: rest =>
        if (visitedStation.contains(hop.from))
          true
        else
          containCycle(rest, hop.from +: visitedStation)
      case Nil => false
    }
  }

}
