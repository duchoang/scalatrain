package com.typesafe.training.scalatrain

import org.joda.time.DateTime
import play.api.libs.json._

import play.api.libs.ws.WSRequestHolder

import scala.concurrent.ExecutionContext.Implicits.global
import com.ning.http.client.AsyncHttpClientConfig
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.Future

import scala.collection.mutable.{Map => MutableMap}

/*
{
    "base": "USD",
    "date": "2015-05-15",
    "rates": {
        "AUD": 1.2499,
        "BGN": 1.7265
    }
}
 */
case class Conversion(base: String, date: DateTime = DateTime.now, rates: List[(CountryCode, Rate)]) {
  def getRate(code: String): Option[Double] = {
    rates collectFirst {
      case (CountryCode(`code`), Rate(value)) => value
    }
  }
}

case class CountryCode(code: String)
case class Rate(value: Double)


object Conversion {

  implicit val ratesReader = new Reads[List[(CountryCode, Rate)]] {
    def reads(jsVal: JsValue): JsResult[List[(CountryCode, Rate)]] = jsVal match {
      case JsObject(fields) =>
        val result: List[JsResult[(CountryCode, Rate)]] = fields.toList map {
          case (countryCode, JsNumber(rate)) => JsSuccess((CountryCode(countryCode), Rate(rate.toDouble)))
          case _ => JsError()
        }
        JsSuccess(result.flatMap(_.asOpt))
      case _ => println(jsVal); JsError()
    }
  }

  implicit val conversionRead: Reads[Conversion] = Json.reads[Conversion]
}

object CurrencyService {
  val apiURL: String = "http://api.fixer.io/"
  val builder = new AsyncHttpClientConfig.Builder()
  val client  = new NingWSClient(builder.build())
  val request: WSRequestHolder = client.url(s"${apiURL}latest")

  val cached: MutableMap[String, Conversion] = MutableMap()

  def convert(value: Double, fromCurrency: String, toCurrency: String): Future[Double] = {
    // first check if `fromCurrency` exists in the cache
    val futureConversion: Future[Conversion] =
      if (cached.contains(fromCurrency))
        Future.successful(cached(fromCurrency))
      else
        request.withQueryString("base" -> fromCurrency)
           .get
           .map(response => {
              val json: JsValue = Json.parse(response.body)
              val conversion: Conversion = json.validate[Conversion].get

              // save to cache
              cached += fromCurrency -> conversion

              conversion
           })

    futureConversion.map(conversion => {
      val rate: Double = conversion.getRate(toCurrency).get
      value * rate
    })
  }

}

