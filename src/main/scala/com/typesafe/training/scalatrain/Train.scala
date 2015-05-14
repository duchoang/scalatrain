package com.typesafe.training.scalatrain

import scala.collection.immutable.Seq
import org.joda.time.DateTime

case class Train(info: TrainInfo, schedule: Seq[(Time, Station)], timetable: TimeTable) {
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

  def allHops(allCost: Map[(Station, Station), Double]): Seq[Hop] = this.backToBackStations map {
    case (from, to) =>
      Hop(from, to, this, allCost((from, to)))
  }

  def canRunOnDate(givenDate: DateTime): Boolean =
    this.timetable.isAvailableForDate(givenDate)

  def canRunOnWeekday(day: DayOfWeek): Boolean =
    this.timetable.isAvailableForDay(day)

  override val toString = info.toString
}
