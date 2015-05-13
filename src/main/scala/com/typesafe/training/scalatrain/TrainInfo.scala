package com.typesafe.training.scalatrain

sealed abstract class TrainInfo {
  def number: Int
}

case class InterCityExpress(number: Int, hasWifi: Boolean = false) extends TrainInfo {
  override def toString = "ice_" + number
}
case class RegionalExpress(number: Int) extends TrainInfo {
  override def toString = "re_" + number
}
case class BavarianExpress(number: Int) extends TrainInfo {
  override def toString = "be_" + number
}
