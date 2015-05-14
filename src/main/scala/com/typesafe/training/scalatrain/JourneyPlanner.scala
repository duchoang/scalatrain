package com.typesafe.training.scalatrain

import scala.collection.immutable.Set

case class JourneyPlanner(trains: Set[Train]) {

  val stations: Set[Station] = trains.flatMap(_.stations)

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
  val allMappingHops: Map[Station, Set[Hop]] = trains.flatMap(_.allHops).groupBy(hop => hop.from)

  // Connections between two stations given a departure time.
  def allConnections(start: Station, end: Station, departureTime: Time): Set[Seq[Hop]] = {

    val allHops = this.allMappingHops

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

  private def hasRepeatedHop(visitedPath: Seq[Hop], hop: Hop): Boolean = {
    !visitedPath.exists(_.from == hop.to)
  }
}
