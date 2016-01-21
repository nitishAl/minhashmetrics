package util
import scala.io

object FeatureGenerator extends FeatureGeneratorConfig {
  val APPAREL = topLevelCategories.getOrElse("APPAREL", "Apparel").toLowerCase
  val SHOES_AND_ACCESSORIES = topLevelCategories.getOrElse("SHOES_AND_ACCESSORIES", "Shoes & Accessories").toLowerCase
  val HPC = topLevelCategories.getOrElse("HPC", "Grocery, Health & Personal Care").toLowerCase
  val HOME_IMPROVEMENT = topLevelCategories.getOrElse("HOME_IMPROVEMENT", "Home Improvement").toLowerCase
  val OFFICE_SCHOOL_SUPPLIES = topLevelCategories.getOrElse("OFFICE_SCHOOL_SUPPLIES", "Office & School Supplies").toLowerCase
  val ARTS_CRAFTS = topLevelCategories.getOrElse("ARTS_CRAFTS", "Arts, Crafts & Sewing").toLowerCase
  val FURNITURE = topLevelCategories.getOrElse("FURNITURE", "Furniture").toLowerCase
  val ELECTRONICS = topLevelCategories.getOrElse("ELECTRONICS", "Electronics").toLowerCase
  val BABY_ESSENTIALS = topLevelCategories.getOrElse("BABY_ESSENTIALS", "Baby Essentials").toLowerCase
  val HOME = topLevelCategories.getOrElse("HOME", "Home").toLowerCase
  val TOOLS_HARDWARE = topLevelCategories.getOrElse("TOOLS_HARDWARE", "Tools & Hardware").toLowerCase
  val SPORTS_OUTDOOR = topLevelCategories.getOrElse("SPORTS_OUTDOOR", "Sports & Outdoors").toLowerCase
  val AUTOMOTIVE = topLevelCategories.getOrElse("AUTOMOTIVE", "Automotive").toLowerCase
  val PATIO_LAWN_GARDEN = topLevelCategories.getOrElse("PATIO_LAWN_GARDEN", "Patio, Lawn & Garden").toLowerCase
  val COMPUTERS = topLevelCategories.getOrElse("COMPUTERS", "Computers").toLowerCase
  val TOYS_GAMES = topLevelCategories.getOrElse("TOYS_GAMES", "Toys & Games").toLowerCase
  val ADDITIONAL = topLevelCategories.getOrElse("ADDITIONAL", "additional").toLowerCase

  val RUGS = leafCategoryMapping.getOrElse("RUGS", "additional").toLowerCase
  val OTHER_APPAREL = leafCategoryMapping.getOrElse("OTHER_APPAREL", "Apparel").toLowerCase.split(",").map(_.trim)

  val categoriesWithStopWords = stopWordFileMapping.keys.toSet

  val colors = scala.io.Source.fromInputStream(FeatureGenerator.getClass.getResourceAsStream("/colors.txt"))(io.Codec("UTF-8")).getLines().toSet

  val rules = {
    categoriesWithStopWords.map { category =>
      (topLevelCategories(category).toLowerCase,
        Set("the") ++ io.Source.fromInputStream(FeatureGenerator.getClass.
          getResourceAsStream(stopWordFileMapping(category)))(io.Codec("UTF-8")).getLines().toSet)
    }.toMap
  }

  def removeSpecialChars(title: String) = {
    val specialChars = "™®\\()[]_-/\"&@,#!:;<='`\t\n\r"
    title.toLowerCase.map(c => if(specialChars.contains(c)) " " else c).mkString("").replaceAll("[\\p{javaSpaceChar}]+", " ").trim
  }

  def splitDimension(word: String) = {
    val r = "[0-9]+([x|X])[0-9]+".r
    word match {
      case r(c) => word.replace(c, " ")
      case _ => word
    }
  }

  def clean(title: String, parentCategory: String = "", aggressive: Boolean = false) = {
    val truncatedTitle = title.toLowerCase.take(400)  // prevents spurious titles from causing issues
    val stopwords = rules.getOrElse(parentCategory.toLowerCase, Set("the")) // ++ (if(retainColor.contains(parentCategory)) Set() else colors)

    val withoutStopWords = removeSpecialChars(truncatedTitle).split(" ")
      .map(w => if(w.size > 0 && w.last == 's') w.substring(0, w.length-1) else w)
      .filter(w => !stopwords.contains(w))
      .map(w => splitDimension(w))
    //.filterNot(w => aggressive && categories.contains(parentCategory) && w.matches(".*\\d.*"))

    withoutStopWords.map(w => w.replaceAll("\\.0$", "").replaceAll("^0\\.", ".")).filter(w => w.trim.length > 0).mkString(" ")
  }

  def tokenize(phrase: String, stopwords: Set[String]= Set()) = {
    val withoutStopWords = removeSpecialChars(phrase.toLowerCase).split(" ")
      .map(w => if(w.size > 0 && w.last == 's') w.substring(0, w.length-1) else w) //stem last s
      .filter(w => !stopwords.contains(w))

    withoutStopWords.filter(w => w.trim.length > 0).toSet[String]
  }

  def cleanAndTokenize(text: String) = {
    val cleanedText = clean(text)
    tokenize(cleanedText)
  }
}
