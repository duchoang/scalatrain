package com.typesafe.training.scalatrain

import scala.collection.immutable.Set

case class JourneyPlanner(trains: Set[Train]) {
  val stations: Set[Station] = trains.flatMap(train => train.stations)

  def trainsAt(station: Station): Set[Train] = {
    trains.filter(train => train.stations contains station)
  }

  def stopsAt(givenStation: Station): Set[(Time, Train)] = {
    for {
      train <- trains
      //(time, station) <- train.schedule if station == givenStation
      time <- train.timeAt(givenStation)
    } yield (time, train)
  }

  def isShortTrip(from: Station, to: Station): Boolean = {
    trains.exists(train => {
      val restStations = train.stations.dropWhile(station => station != from)
      restStations match {
        case `from` +: `to` +: _ => true
        case `from` +: _ +: `to` +: _ => true
        case _ => false
      }
    })
  }
}
