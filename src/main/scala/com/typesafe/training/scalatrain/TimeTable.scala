package com.typesafe.training.scalatrain

import org.joda.time.DateTime

case class TimeTable(workingDates: Set[DayOfWeek], nonWorkingDates: Set[DateTime]) {

  def isAvailableForDate(givenDate: DateTime): Boolean = {
    val dayOfWeek = DayOfWeek(givenDate.getDayOfWeek)
    !inNonWorkingDates(givenDate) && dayOfWeek.nonEmpty && workingDates.contains(dayOfWeek.get)
  }

  def isAvailableForDay(day: DayOfWeek): Boolean =
    workingDates.contains(day)

  // return true if `givenDate` in the set of non-working-dates
  private def inNonWorkingDates(givenDate: DateTime): Boolean = {
    def areDatesTheSame(date: DateTime): Boolean =
      date.getDayOfMonth == givenDate.getDayOfMonth && date.getMonthOfYear == givenDate.getMonthOfYear && date.getYear == givenDate.getYear
    nonWorkingDates.exists(date => areDatesTheSame(date))
  }
}

sealed trait DayOfWeek

case object Monday    extends DayOfWeek
case object Tuesday   extends DayOfWeek
case object Wednesday extends DayOfWeek
case object Thursday  extends DayOfWeek
case object Friday    extends DayOfWeek
case object Saturday  extends DayOfWeek
case object Sunday    extends DayOfWeek

object DayOfWeek {
  def apply(index: Int): Option[DayOfWeek] = index match {
    case 1 => Some(Monday)
    case 2 => Some(Tuesday)
    case 3 => Some(Wednesday)
    case 4 => Some(Thursday)
    case 5 => Some(Friday)
    case 6 => Some(Saturday)
    case 7 => Some(Sunday)
    case _ => None
  }
}
