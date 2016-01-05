package com.indix

import com.indix.similarity.AllPairs

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._

object App {
  
  def foo(x : Array[String]) = x.foldLeft("")((a,b) => a + b)

  def getTitle(productJson:String) = {
    (parse(productJson) \ "title").productElement(0)
  }

  def main(args : Array[String]) {
    val input = Source.fromFile("akris.json")
    val titles = input.getLines().toList.map(getTitle)
    println(AllPairs.jaccardSimilarityIndex("Hello world", "Hello world India"))
  }

}
