package com.indix

import com.indix.similarity.AllPairsApproach

import scala.io.Source
import java.io._
import org.json4s._
import org.json4s.jackson.JsonMethods._

object App {
  
  def foo(x : Array[String]) = x.foldLeft("")((a,b) => a + b)

  def getTitle(productJson:String):String = {
    ((parse(productJson) \ "title").productElement(0)).toString
  }

  def main(args : Array[String]) {
    val input = Source.fromFile("akris.json")
    val titles: List[String] = input.getLines().toList.map(getTitle)

    def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
      val p = new java.io.PrintWriter(f)
      try { op(p) } finally { p.close() }
    }
    printToFile(new File("example.txt")) { p =>
      AllPairsApproach.similarPairs(titles).foreach(p.println)
    }
  }

}
