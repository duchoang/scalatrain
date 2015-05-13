package com.typesafe.training.scalatrain

trait OutputHelper {
  def print(s: String) = Console.println(s)
}

object Hi extends OutputHelper {
  def hello() = "print hello world"
}
