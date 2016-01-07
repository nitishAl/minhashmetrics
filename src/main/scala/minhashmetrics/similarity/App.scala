package minhashmetrics.similarity

import java.io._

import org.apache.spark.{SparkConf, SparkContext}
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.io.Source

object App {
  def cleanText(input:String):String = input.replaceAll("\r?\n|\r|\t"," ").toLowerCase

  def formSet(text:String): Set[String] = text.split(" +").toSet

  def getTitle(productJson:String):String = {
    ((parse(productJson) \ "title").productElement(0)).toString
  }

  def main(args : Array[String]) {
    val input = Source.fromFile("addison.json")
    val titles = input.getLines().map(getTitle).toList

    def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
      val p = new java.io.PrintWriter(f)
      try { op(p) } finally { p.close() }
    }
    printToFile(new File("output.txt")) { p =>
      AllPairsApproach.printSimilarTitles(titles, p, 0.75)
    }

//    val sc = new SparkContext(new SparkConf().setAppName("").setMaster("local"))
//
//    val minHashApproach = new MinHashApproach(sc)
//    val model = minHashApproach.runLSH(sc.parallelize(titles.map(cleanText).map(formSet)))

  }

}
