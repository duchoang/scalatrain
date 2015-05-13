package com.typesafe.training.scalatrain

import play.api.libs.json.{JsNumber, JsObject, JsString, JsValue}

import scala.util.{Failure, Success, Try}

case class Time(hours: Int = 0, minutes: Int = 0) extends Ordered[Time] {
  //Verify that hours is within 0 and 23
  require(hours < 24 && hours >= 0, "Hours should be within 0 and 23")

  //Verify that minutes is within 0 and 59
  require(minutes < 60 && minutes >= 0, "Minutes should be within 0 and 59")

  val asMinutes: Int = hours * 60 + minutes

  def minus(that: Time): Int = this.asMinutes - that.asMinutes

  def -(that: Time): Int = minus(that)

  override lazy val toString: String = f"$hours%02d:$minutes%02d"

  override def compare(that: Time): Int = this - that

  def toJson: JsValue = {
    JsObject(Seq("hours" -> JsNumber(hours), "minutes" -> JsNumber(minutes)))
  }

}

object Time {
  def fromMinutes(minutes: Int): Time = Time(minutes / 60, minutes % 60)

  def fromJson(json: JsValue): Option[Time] = {
    val conversion: Try[Time] = for {
      hours <- Try((json \ "hours").as[Int])
      mins <- Try((json \ "minutes").as[Int]).recover({case _: Exception => 0})
    } yield Time(hours, mins)
    conversion.toOption
    /*
    val conversion: Try[Time] = Try(json match {
      case JsObject(Seq(("hours", JsNumber(hours)), ("minutes", minutes))) =>
        val min = minutes match {
          case JsNumber(m) => m.toInt
          case _ => 0
        }
        Time(hours.toInt, min)
    })
    conversion match {
      case Success(t) => Some(t)
      case Failure(_) => None
    }
    */
  }
}
