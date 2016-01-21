package util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object JsonUtil {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  def toMap(json: String) = {
    mapper.readValue(json, classOf[Map[String, String]])
  }
  
  def getFieldOrElse(json:String, field:String, orElse:String) = {
    toMap(json).getOrElse(field, orElse)
  }
}
