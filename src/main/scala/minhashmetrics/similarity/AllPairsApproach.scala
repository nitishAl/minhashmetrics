package minhashmetrics.similarity

case class JaccardPair(first: String, second: String, similarity:Double){
}

object AllPairsApproach {

  def cleanText(input:String):String = input.replaceAll("\r?\n|\r|\t"," ").toLowerCase

  def formSet(text:String): Set[String] = text.split(" +").toSet

  def jaccardSimilarityIndex(set1:Set[String], set2:Set[String]): Double = {
    (set1.intersect(set2).size).toDouble/(set1.union(set2).size)
  }

  def allPairs(testLines:List[String]):List[JaccardPair] = {
    def allPairs(testLines:List[String], acc:List[JaccardPair]):List[JaccardPair] = testLines match {
      case head :: Nil => acc
      case head :: tail =>  allPairs(tail, acc ++ tail.map(x => new JaccardPair(cleanText(head), cleanText(x), jaccardSimilarityIndex(formSet(cleanText(head)), formSet(cleanText(head))))))
    }
    allPairs(testLines, Nil)
  }

  def similarPairs(textLines:List[String]) = {
    val pairs: List[JaccardPair] = allPairs(textLines)
    pairs.filter(_.similarity > 0.5)
  }
}
