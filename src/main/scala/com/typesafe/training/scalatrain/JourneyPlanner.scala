package com.typesafe.training.scalatrain

import scala.collection.immutable.Set

case class JourneyPlanner(trains: Set[Train]) {

  val stations: Set[Station] = trains.flatMap(_.stations)

  def trainsAt(station: Station): Set[Train] = {
    trains.filter(_.stations.contains(station))
  }

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

  //all hops of all trains, grouped by the departing station
  def allMappingHops: Map[Station, Set[Hop]] = trains.flatMap(_.allHops).groupBy(hop => hop.from)

  //connections between two stations given a departure time.
  def allConnections(start: Station, end: Station, departureTime: Time): Set[Seq[Hop]] = {

    val allHops = this.allMappingHops

    def findPath(from: Station, end: Station, visitedPath: Seq[Hop]): Set[Seq[Hop]] = {
//      println(s"find path $from -> $end, visited path:\n\t${visitedPath.map(hop => s"${hop.from} -> ${hop.to}, train ${hop.train.info}").mkString("\n\t")}")
      if (from == end) Set(visitedPath)
      else {
        val allPaths: Set[Seq[Hop]] =
          (for (hop <- allHops.getOrElse(from, Set()) if !visitedPath.exists(visitedHop => visitedHop.from == hop.to)) yield {
          findPath(hop.to, end, visitedPath :+ hop)
        }).flatten
        allPaths
      }
    }

    val allPaths: Set[Seq[Hop]] = findPath(start, end, Seq())
    allPaths filter {
      case head +: tail =>
        val (depart, _) = head.departureAndArrivalTime
        depart > departureTime && Hop.checkPathValid(head +: tail) && !Hop.containCycle(head +: tail)
      case Nil => false
    }
  }
}
