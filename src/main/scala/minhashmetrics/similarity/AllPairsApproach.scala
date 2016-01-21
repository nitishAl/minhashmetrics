package minhashmetrics.similarity

import java.io._
import _root_.util.FeatureGenerator
import minhashmetrics.utils.{PreprocessorHelper, JsonUtil}

case class JaccardPair(first: String, second: String, similarity:Double){

}


object AllPairsApproach {

  val ph = new PreprocessorHelper(stemming = true)

  def cleanText(input:String):String = ph.preprocess(input.replaceAll("\r?\n|\r|\t"," ").toLowerCase)

  def formSet(text:String): Set[String] = text.split(" +").toSet

  def cleanAndFormSet(input:String):Set[String] = {
    formSet(cleanText(input))
  }

  def jaccardSimilarityIndex(set1:Set[String], set2:Set[String]): Double = {
    (set1.intersect(set2).size).toDouble/(set1.union(set2).size)
  }
  
  def printSimilarTitles(textLines:List[String], output:PrintWriter, threshold: Double):Unit = textLines match {
    case head :: Nil =>
    case head :: tail =>  {
      tail.map(tailLine => {
        val similarity: Double = jaccardSimilarityIndex(FeatureGenerator.cleanAndTokenize(head), FeatureGenerator.cleanAndTokenize(tailLine))
        if(similarity > threshold)
          output.println(JsonUtil.toJson(JaccardPair(FeatureGenerator.clean(head), FeatureGenerator.clean(tailLine), similarity)))
      })
      printSimilarTitles(tail, output, threshold)
    }
  }

  def printSimilarTitles(textLines:List[String], output:String, threshold: Double):Unit = {
    def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
      val p = new java.io.PrintWriter(f)
      try { op(p) } finally { p.close() }
    }
    printToFile(new File(output)) { p =>
      AllPairsApproach.printSimilarTitles(textLines, p, threshold)
    }
  }

  def getSimilarTitles(textLines:List[String], output:PrintWriter, threshold: Double) = {
    var titles = textLines
    var result = List[JaccardPair]()
    while(titles != Nil){
      result = titles.tail.flatMap(title => {
        val similarity: Double = jaccardSimilarityIndex(cleanAndFormSet(titles.head), cleanAndFormSet(title))
        if(similarity > threshold)
          Some(JaccardPair(titles.head, title, similarity))
        else
          None
      }) ++ result
      titles = titles.tail
    }
    result
  }
}
