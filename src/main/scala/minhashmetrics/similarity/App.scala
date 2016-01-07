package minhashmetrics.similarity

import org.apache.spark.{SparkConf, SparkContext}
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.io.Source

object App {
  def getTitle(productJson:String):String = {
    ((parse(productJson) \ "title").productElement(0)).toString
  }

  def main(args : Array[String]) {
    val input = Source.fromFile("addison.json")
    val titles = input.getLines().map(getTitle).toList

    AllPairsApproach.printSimilarTitles(titles.sorted, "allpairs-output.txt", 0.75)

    val sc = new SparkContext(new SparkConf().setAppName("").setMaster("local"))

    val minHashApproach = new MinHashApproach(sc)
    minHashApproach.runLSH(titles, "minhash-output.txt", 0.75)
  }

}
