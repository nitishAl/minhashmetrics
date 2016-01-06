package minhashmetrics.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.reflect.ClassTag

object JsonUtil {
  private val mapper = new ObjectMapper()

  mapper.registerModule(DefaultScalaModule)

  def toJson(value: Any) = mapper.writeValueAsString(value)

  def fromJson[T: ClassTag](json: String) = {
    val classType = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
    mapper.readValue[T](json, classType)
  }

}
