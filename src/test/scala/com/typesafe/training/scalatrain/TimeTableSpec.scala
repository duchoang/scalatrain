package com.typesafe.training.scalatrain

import org.joda.time.DateTime
import org.scalatest.{Matchers, WordSpec}
import TestData._

class TimeTableSpec extends WordSpec with Matchers {
  "calling isAvailable() on TimeTable" should {
    "return true if given date is in working days and not in holiday" in {
      timeTableIce724.isAvailable(new DateTime(2015, 5, 14, 0, 0)) shouldBe true
    }

    "return false if given date is not in working days" in {
      timeTableIce724.isAvailable(new DateTime(2015, 5, 16, 0, 0)) shouldBe false
    }
    "return false if given date is in holiday" in {
      timeTableIce724.isAvailable(new DateTime(2015, 12, 24, 5, 30)) shouldBe false
    }
  }
}
