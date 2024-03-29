package com.typesafe.training.scalatrain

import org.joda.time.DateTime

import scala.collection.immutable.Set

case class JourneyPlanner(trains: Set[Train]) {

  val stations: Set[Station] = trains.flatMap(_.stations)

  val mapBack2BackStations: Map[Station, Set[(Station, Station)]] =
    trains.flatMap(train => train.backToBackStations).groupBy(back2Back => back2Back._1)

  val sinkStations: Set[Station] = stations.filterNot(station => mapBack2BackStations.contains(station))

  def trainsOnWeekday(dayOfWeek: DayOfWeek): Set[Train] = trains.filter(_.canRunOnWeekday(dayOfWeek))

  def trainsOnDate(givenDate: DateTime): Set[Train] = trains.filter(_.canRunOnDate(givenDate))

  def trainsAt(station: Station): Set[Train] =
    trains.filter(_.stations.contains(station))

  def stopsAt(station: Station): Set[(Time, Train)] = {
    for {
      train <- trains
      time <- train.timeAt(station)
    } yield time -> train
  }

  def isShortTrip(from: Station, to: Station): Boolean =
    trains.exists(train =>
      train.stations.dropWhile(station => station != from) match {
        case `from` +: `to` +: _      => true
        case `from` +: _ +: `to` +: _ => true
        case _                        => false
      }
    )

  // All hops of all trains, grouped by the departing station
  def allMappingHops(allCost: Map[Train, Map[(Station, Station), Double]]): Map[Station, Set[Hop]] =
    trains.flatMap(train => train.allHops(allCost(train))).groupBy(hop => hop.from)

  // Connections between two stations given a departure time.
  def allConnections(start: Station, end: Station, departureTime: Time, allCost: Map[Train, Map[(Station, Station), Double]]): Set[Seq[Hop]] = {

    val allHops = this.allMappingHops(allCost)

    def hasRepeatedHop(visitedPath: Seq[Hop], hop: Hop): Boolean = {
      !visitedPath.exists(_.from == hop.to)
    }

    def findPath(from: Station, visitedPath: Seq[Hop]): Set[Seq[Hop]] = {
      if (from == end) Set(visitedPath)
      else {
        for {
          hop <- allHops.getOrElse(from, Set()) if hasRepeatedHop(visitedPath, hop)
          path <- findPath(hop.to, visitedPath :+ hop)
        } yield path
      }
    }

    val allPaths: Set[Seq[Hop]] = findPath(start, Seq())
    allPaths filter {
      case head +: tail =>
        val (depart, _) = head.departureAndArrivalTime
        depart > departureTime && Hop.checkPathValid(head +: tail) && !Hop.containCycle(head +: tail)
      case Nil => false
    }
  }

  def findRoute(start: Station, end: Station, departureDateTime: DateTime, allCost: Map[Train, Map[(Station, Station), Double]]): Set[Seq[Hop]] = {
    val departureTime: Time = Time(departureDateTime.getHourOfDay, departureDateTime.getMinuteOfHour)
    val allPaths: Set[Seq[Hop]] = allConnections(start, end, departureTime, allCost)

    val filterPaths = allPaths.filter(path =>
      path.nonEmpty && path.forall(hop => hop.train.canRunOnDate(departureDateTime)))

    // adjust all cost on all path
    filterPaths.map(path => {
      path.map(hop => {
        val now = DateTime.now()
        val before2Week = departureDateTime.minusDays(14)
        val before1Day = departureDateTime.minusDays(1)
        val newcost =
          if (now.compareTo(before1Day) >= 0) {
            // now >= before1Day
            hop.cost * 0.75
          } else if (now.compareTo(before2Week) >= 0) {
            // before2Week <= now < before1Day
            hop.cost * 1.5
          } else
            hop.cost
        hop.copy(cost = newcost)
      })
    })
  }

}

object JourneyPlanner {

  def calculateTotalTime(path: Seq[Hop]): Int = {
    val (_, lastArr) = path.last.departureAndArrivalTime
    val (firstDepart, _) = path.head.departureAndArrivalTime
    if (lastArr < firstDepart)
      lastArr.asMinutes + 24*60 - firstDepart.asMinutes
    else
      lastArr - firstDepart
  }

  def calculateTotalCost(path: Seq[Hop]): Double = {
    path.foldLeft(0.0)((costs, hop) => costs + hop.cost)
  }

  def sortPaths[A](paths: Set[Seq[Hop]], calculate: Seq[Hop] => A)(implicit ordering: Ordering[A]): List[(Seq[Hop], A)] = {
    val pathsWithTotal: List[(Seq[Hop], A)] = paths.toList.map(path => {
      assert(path.nonEmpty)
      (path, calculate(path))
    })
    pathsWithTotal.sortBy(tuple => tuple._2)
  }

  def sortPathsByTotalTime(paths: Set[Seq[Hop]]): List[(Seq[Hop], Int)] = {
    sortPaths[Int](paths, calculateTotalTime)
  }

  def sortPathsByTotalCost(paths: Set[Seq[Hop]]): List[(Seq[Hop], Double)] = {
    sortPaths[Double](paths, calculateTotalCost)
  }
}
