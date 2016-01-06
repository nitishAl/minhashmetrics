package minhashmetrics.similarity

import com.invincea.spark.hash.LSH
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.{SparseVector, Vectors}
import org.apache.spark.rdd.RDD

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
}

class MinHashApproach(@transient sc:SparkContext) {
  def runLSH(titles : RDD[Set[String]]) = {
    titles.repartition(8)
    val vctr = titles.map(words => (words.map(word => (WordIdGenerator.getId(word), 1.0)))).map(a => Vectors.sparse(65535, a.toSeq).asInstanceOf[SparseVector])
    val lsh = new  LSH(data = vctr, p = 65537, m = 1000, numRows = 1000, numBands = 200, minClusterSize = 2)
    val model = lsh.run
    model
  }
}