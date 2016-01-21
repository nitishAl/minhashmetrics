package util

import org.yaml.snakeyaml.Yaml
import scala.collection.JavaConversions._
import scala.io

trait FeatureGeneratorConfig {
  val BRAND_FILTER = 'B
  val BUCKETS = Map[String, Symbol](
    "Grocery, Health & Personal Care" -> BRAND_FILTER,
    "Shoes & Accessories" -> BRAND_FILTER,
    "Home Improvement" -> BRAND_FILTER,
    "Home" -> BRAND_FILTER,
    "Patio, Lawn & Garden" -> BRAND_FILTER,
    "Tools & Hardware" -> BRAND_FILTER,
    "Furniture" -> BRAND_FILTER
  )

  //config from resource yml
  val configFromYaml =  io.Source.fromInputStream(getClass.getResourceAsStream("/matchconfig.yml"))(io.Codec("UTF-8")).mkString
  private val yaml = new Yaml().load(configFromYaml)
  private val matcherConstantsFromYaml = if(yaml == null) Map[String, Map[String,String]]() else yaml match {
    case obj: java.util.LinkedHashMap[_, _] =>
      obj.toMap.asInstanceOf[Map[String, java.util.HashMap[String,String]]].map{e => {

        (e._1, e._2.toMap)}}.toMap

  }

  val (topLevelCategories, stopWordFileMapping, leafCategoryMapping) =
    (matcherConstantsFromYaml.getOrElse("TOP_LEVEL_CATEGORIES",Map[String,String]()),
      matcherConstantsFromYaml.getOrElse("STOP_WORD_FILE_MAPPING",Map[String,String]()),
      matcherConstantsFromYaml.getOrElse("LEAF_LEVEL_MAPPING",Map[String,String]()))
}
