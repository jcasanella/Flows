package com.bin.data.flows.json

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class KafkaConfig(topicConsumer: String, properties: Map[String, AnyVal])
class JsonParser(jsonToParse: String) {

  require(jsonToParse != null, "The json to parse is null")

  val json = Json.parse(jsonToParse)

  def getStreamingTime(): Long = (json \ "spark" \ "streamingTime").as[Long]

  def getKafka(): String = {
  //  val jsonKafka = (json \ "kafka").get
   // implicit val kafkaConfig = Json.fromJson[KafkaConfig](jsonKafka)
   // kafkaConfig.get
   // jsonKafka.as[KafkaConfig]
   // val kafkaRead = new Read

//    val jsonProp = (json \ "kafka" \ "properties").get
//    implicit lazy val mapReads: Reads[Map[String, AnyVal]] = new Reads[Map[String, AnyVal]] {
//      def reads(jv: JsValue): JsResult[Map[String, AnyVal]] =
//        JsSuccess(jv.as[Map[String, AnyVal]].map{case (k, v) =>
//          k -> v.asInstanceOf[AnyVal]
//        })
//    }
//    val props = mapReads.reads(jsonProp).get
    val topic = (json \ "kafka" \ "topicConsumer").as[String]

    //KafkaConfig(topic, props)
    topic
  }
}


