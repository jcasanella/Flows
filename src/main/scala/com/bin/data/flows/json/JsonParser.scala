package com.bin.data.flows.json

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class KafkaConfig(topicConsumer: String, properties: Map[String, String])
class JsonParser(jsonToParse: String) {

  require(jsonToParse != null, "The json to parse is null")

  val json = Json.parse(jsonToParse)

  def getStreamingTime(): Long = (json \ "spark" \ "streamingTime").as[Long]

  def getKafka(): KafkaConfig = {

    implicit val jsonKafka: Reads[KafkaConfig] = {
      val props = (__ \ "properties").read[Map[String, String]]
      ((__ \ "topicConsumer").read[String] and props)(KafkaConfig(_: String, _: Map[String, String]))
    }
    val kafka = (json \ "kafka").get
    jsonKafka.reads(kafka).get
  }
}


