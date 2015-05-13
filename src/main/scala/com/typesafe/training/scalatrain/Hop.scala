package com.typesafe.training.scalatrain

case class Hop(from: Station, to: Station, train: Train) {
  require(train.backToBackStations.nonEmpty)
  require(train.backToBackStations.contains((from, to)))
}
