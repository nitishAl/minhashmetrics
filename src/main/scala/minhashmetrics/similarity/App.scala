package minhashmetrics.similarity

import java.io.File

import minhashmetrics.utils.{JsonUtil, PreprocessorHelper}
import org.apache.spark.{SparkConf, SparkContext}
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.io.Source

object App {

  val ph = new PreprocessorHelper()

  def cleanText(input:String):String = ph.preprocess(input.replaceAll("\r?\n|\r|\t"," ").toLowerCase)

  def formSet(text:String): Set[String] = text.split(" +").toSet

  def cleanAndFormSet(input:String):Set[String] = {
    formSet(cleanText(input))
  }

  def jaccardSimilarityIndex(set1:Set[String], set2:Set[String]): Double = {
    (set1.intersect(set2).size).toDouble/(set1.union(set2).size)
  }

  def getTitle(productJson:String):String = {
    ((parse(productJson) \ "title").productElement(0)).toString
  }

  def main(args : Array[String]) {
    val input = Source.fromFile("akris.json")
    val titles = input.getLines().map(getTitle).toList

    val sc = new SparkContext(new SparkConf().setAppName("").setMaster("local"))

    val minHashApproach = new MinHashApproach(sc)
    minHashApproach.runLSH(titles, "minhash-output.txt", 0.75)
//    readLSHOutput()

    //AllPairsApproach.printSimilarTitles(titles.sorted, "allpairs-output.txt", 0.75)
  }

  def readLSHOutput() = {
    val titles = Source.fromFile("cleansed-titles")
    val lineToTitle = titles.getLines().toList.zipWithIndex.map{case (x, y) => (y, x + 1)}.toMap

    val lshLinePairs = Source.fromFile("lsh-output")

    def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
      val p = new java.io.PrintWriter(f)
      try { op(p) } finally { p.close() }
    }
    printToFile(new File("lshpairs-output.txt")) { p =>

    lshLinePairs.getLines().toList.map(x => x.split(",").toList)
      .map{case List(first, second) => (lineToTitle.get(first.toInt), lineToTitle.get(second.toInt))}
      .flatMap{
        case (Some(first), Some(second)) => {
        val similarityIndex: Double = jaccardSimilarityIndex(cleanAndFormSet(first), cleanAndFormSet(second))
        if (similarityIndex > 0.75)
          Option(JaccardPair(first, second, similarityIndex))
        else
          None
      }.map(x => p.println(JsonUtil.toJson(x)))
      }
    }
  }
}
