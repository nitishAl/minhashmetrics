package minhashmetrics.similarity

import java.io.File

import com.invincea.spark.hash.LSH
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.{SparseVector, Vectors}

object WordIdGenerator{
  var n = 0
  val cache = collection.mutable.Map[String, Int]()
  def getId(word:String) = cache.get(word) match {
    case Some(id) => id
    case None =>
      n = n + 1
      cache += (word -> n)
      n
  }
  val invertCache = cache.map{case (a, b) => (b, a)}.toMap
}

class MinHashApproach(@transient sc:SparkContext) {
  def cleanText(input:String):String = input.replaceAll("\r?\n|\r|\t"," ").toLowerCase

  def formSet(text:String): Set[String] = text.split(" +").toSet

  def runLSH(titles : List[String], output:String, threshold:Double) = {
    val vector = titles.map(title => (Vectors.sparse(65535, formSet(cleanText(title)).toSeq.map(word => (WordIdGenerator.getId(word), 1.0))).asInstanceOf[SparseVector], title)).toMap
    val lsh = new  LSH(data = sc.parallelize(vector.keys.toSeq), p = 65537, m = 1000, numRows = 120, numBands = 20, minClusterSize = 2)
    val model = lsh.run
    println(model.clusters.count())
    val clusters = model.clusters.map(a => a._2.flatMap(vector.get(_)).toList).collect()

    def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
      val p = new java.io.PrintWriter(f)
      try { op(p) } finally { p.close() }
    }
    printToFile(new File(output)) { p =>
      clusters.map(titles => AllPairsApproach.printSimilarTitles(titles.sorted, p, threshold))
    }
  }
}