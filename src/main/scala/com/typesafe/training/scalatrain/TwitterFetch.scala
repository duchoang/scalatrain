package com.typesafe.training.scalatrain

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.oauth.OAuthCalculator
import play.api.libs.oauth.ConsumerKey
import play.api.libs.oauth.RequestToken
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.{Reads, Json, JsValue}
import com.ning.http.client.AsyncHttpClientConfig.Builder
import play.api.libs.ws.ning.NingWSClient

import scala.util.Try


case class RawTweet(created_at: String, text: String)
case class Tweet(created_at: DateTime, text: String)

object RawTweet {
  implicit val rawTweetRead: Reads[RawTweet] = Json.reads[RawTweet]
}

object TwitterFetch extends App {

  val builder = new Builder()
  val client = new NingWSClient(builder.build())

  val key = ConsumerKey(
    "QTrTJcKKV4DTgBB1JJiw",
    "bal18VhhDpMwaME4C5vavV6FANpXSDv0DINobxTw")

  val token = RequestToken(
    "22909464-zZCFk4nAKlPQgVlCgP4tC9fbCXWvbMr7pKSY4JzgY",
    "sGnkSHdvdYgJWOnupbDWZh5lU1TiyVl6BBPFWi54ukIUb")

  client.url("https://api.twitter.com/1.1/statuses/user_timeline.json")
    .withQueryString("count" -> "100", "screen_name" -> "boldtrain")
    .sign(OAuthCalculator(key, token))
    .get
    .map(result => result.json)
    .map(processJson)
    .onComplete(t => {
      if (t.isSuccess) {
        val listTweet: Seq[Tweet] = t.get
        listTweet foreach (tweet => {
          val (station, reason, time) = extractFromTweet(tweet.text)
          val date: DateTime = new DateTime(time.toLong)
          println(s"Station: $station with reason: ${reason.mkString(" ")}, with time=$date" )
        })
      }
      client.close
    })

  def processJson(json: JsValue): Seq[Tweet] = {
//    println(Json.prettyPrint(json))
    val result: Option[Seq[RawTweet]] = json.validate[Seq[RawTweet]].asOpt

    //format of time: "Fri May 15 14:20:54 +0000 2015"
    val patternDate = "E MMM dd HH:mm:ss Z yyyy"
    result match {
      case None => Seq.empty
      case Some(rawTweets) =>
        for (rawTweet <- rawTweets) yield {
          val created = DateTime.parse(rawTweet.created_at, DateTimeFormat.forPattern(patternDate))
          Tweet(created, rawTweet.text)
        }
    }
  }

  //#boldtrainchange Cologne is closed for strike (closureId = 1431697795755)
  //#boldtrainchange Munich is now reopened  (closureId = 1431697735752)
  def extractFromTweet(text: String): (Station, Option[String], String) = {
    val ClosedPattern = "#boldtrainchange (.*) is closed for (.*) \\(closureId = (.*)\\)".r
    val OpenPattern = "#boldtrainchange (.*) is now reopened  \\(closureId = (.*)\\)".r
    text match {
      case ClosedPattern(station, reason, time) =>
        (Station(station), Some(reason), time)
      case OpenPattern(station, time) =>
        (Station(station), None, time)
    }
  }
}
