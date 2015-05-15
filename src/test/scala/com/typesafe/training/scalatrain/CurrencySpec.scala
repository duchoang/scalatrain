package com.typesafe.training.scalatrain

import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future

class CurrencySpec extends WordSpec with Matchers {

  "CurrencyService" should {
    "get the rate from Webservices Fixer.io" in {

      val future: Future[Double] = CurrencyService.convert(1, "GBP", "USD")
      val newValue = Await.result(future, 60.seconds)
      (newValue > 1.5) shouldBe true
    }
  }
}
