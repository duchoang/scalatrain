package com.typesafe.training.scalatrain

sealed abstract class TrainInfo {
  def number: Int
}

case class InterCityExpress(number: Int, hasWifi: Boolean = false) extends TrainInfo
case class RegionalExpress(number: Int) extends TrainInfo
case class BavarianExpress(number: Int) extends TrainInfo
