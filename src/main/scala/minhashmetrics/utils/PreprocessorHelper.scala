package minhashmetrics.utils

import breeze.text.analyze.PorterStemmer
import breeze.text.tokenize.WhitespaceTokenizer
import breeze.text.transform.StopWordFilter
import java.util.regex.Pattern

class PreProcessor(shouldStem: Boolean, ignoreNumbers: Boolean) {
  val stopWordsFilter = new StopWordFilter()
  val wordContainingDigit = Pattern.compile(".*\\p{Digit}+.*")
  val nonAscii = Pattern.compile("[^\\p{ASCII}]")
  val punctuation = Pattern.compile("[\\p{Punct}]")
  def stem(word: String) = if(shouldStem) PorterStemmer(word) else word
  def dropNumeral(word: String) = ignoreNumbers && wordContainingDigit.matcher(word).matches()
  def smallWords(word: String) = word.length < 2
  def preProcess(doc: String) = {
    val cleanupDoc = nonAscii.matcher(punctuation.matcher(doc.toLowerCase).replaceAll("")).replaceAll("").replaceAll("\\s+", " ").trim()
    stopWordsFilter(WhitespaceTokenizer(cleanupDoc)).map(stem).filter(w => !dropNumeral(w)).filter(w => !smallWords(w)).toArray
  }
}

class PreprocessorHelper(stemming: Boolean = false, ignoreNumbers: Boolean = true, bigrams : Boolean = false, onlyBigrams : Boolean = false){
  val preProcessor = new PreProcessor(stemming, ignoreNumbers)

  def preprocess(title: String) = {
    val tokens = tokenize(title)
    if (bigrams && !onlyBigrams)
      (tokens ++ bigrams(tokens)).mkString(" ")
    else if (onlyBigrams)
      bigrams(tokens).mkString(" ")
    else
      tokens.mkString(" ")
  }

  def tokenize(title: String) = {
    preProcessor.preProcess(title)
  }

  def bigrams(tokens: Array[String]) = {
    tokens.sliding(2).map{case Array(a,b) => "%s%s".format(a,b)}
  }
}