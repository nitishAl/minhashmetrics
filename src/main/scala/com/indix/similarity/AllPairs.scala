package com.indix.similarity

object AllPairs {
  def jaccardSimilarityIndex(text1:String, text2:String) = {
    val set1 = text1.toLowerCase.split(" +").toSet
    val set2 = text2.toLowerCase.split(" +").toSet

    (set1.intersect(set2).size).toDouble/(set1.union(set2).size)
  }
}
