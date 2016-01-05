package com.indix.similarity

object AllPairsApproach {
  def jaccardSimilarityIndex(text1:String, text2:String) = {
    val set1 = text1.toLowerCase.split(" +").toSet
    val set2 = text2.toLowerCase.split(" +").toSet

    (set1.intersect(set2).size).toDouble/(set1.union(set2).size)
  }


  def allPairs(testLines:List[String]):List[(String, String)] = {
    def allPairs(testLines:List[String], acc:List[(String, String)]):List[(String, String)] = testLines match {
      case head :: Nil => acc
      case head :: tail =>  allPairs(tail, acc ++ tail.map(x => (head, x)))
    }
    allPairs(testLines, Nil)
  }

  def similarPairs(textLines:List[String]) = {
    val pairs = allPairs(textLines)
    pairs.filter{case (x, y) => jaccardSimilarityIndex(x, y) > 0.5}
  }
}
