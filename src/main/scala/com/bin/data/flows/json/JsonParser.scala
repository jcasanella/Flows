package com.bin.data.flows.json

import com.bin.data.flows.exceptions.JsonParserException
import play.api.libs.json._
import play.api.libs.functional.syntax._


case class KafkaConfig(topicConsumer: String, properties: Map[String, String])
class JsonParser(jsonToParse: String) {

  require(jsonToParse != null, "The json to parse is null")

  val json = Json.parse(jsonToParse)

  def getStreamingTime(): Long = {
    val resStreamTime = (json \ "spark" \ "streamingTime").validate[Long]
    resStreamTime match {
      case s: JsSuccess[Long] => s.get
      case e: JsError => throw new JsonParserException(JsError.toJson(e).toString())
    }
  }

  // TODO: Change method name
  def getKafka(): KafkaConfig = {

    implicit val jsonKafka: Reads[KafkaConfig] = {
      val props = (__ \ "properties").read[Map[String, String]]
      ((__ \ "topicConsumer").read[String] and props)(KafkaConfig(_: String, _: Map[String, String]))
    }
    val kafka = (json \ "kafka").get
    val resKafkaConfig = jsonKafka.reads(kafka)
    resKafkaConfig match {
      case s: JsSuccess[KafkaConfig] => s.get
      case e: JsError => throw new JsonParserException(JsError.toJson(e).toString())
    }
  }
}


