package com.bin.data.flows.json

import com.bin.data.flows.exceptions.JsonParserException
import com.bin.data.flows.file.ReadFile
import org.apache.spark.sql.SparkSession
import play.api.libs.json._
import play.api.libs.functional.syntax._

//case class KafkaConfig(topicConsumer: String, properties: Map[String, String])
case class SourceProps(name: String, typeSource: String, topicConsumer: String)

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

  def getKafkaProps(): Map[String, String] = {

//    implicit val jsonKafka: Reads[KafkaConfig] = {
//      val props = (__ \ "properties").read[Map[String, String]]
//      ((__ \ "topicConsumer").read[String] and props)(KafkaConfig(_: String, _: Map[String, String]))
//    }
//    val kafka = (json \ "kafka").get
    val kafkaProps = (json \ "kafka").validate[Map[String, String]]
//    val resKafkaConfig = jsonKafka.reads(kafka)
    kafkaProps match {
      case s: JsSuccess[Map[String, String]] => s.get
      case e: JsError => throw new JsonParserException(JsError.toJson(e).toString())
    }
  }

  def getSource(): SourceProps = {

    val sourceProps = (json \ "source").validate[SourceProps]
    sourceProps match {
      case s: JsSuccess[SourceProps] => s.get
      case e: JsError =>  throw new JsonParserException(JsError.toJson(e).toString())
    }
  }
}

case class Parameters(batchTime: Long, kafkaProps: Map[String, String], sourceProps: SourceProps)
object Parameters {

  def apply(fileName: String)(implicit session: SparkSession): Parameters = {

    val fileContent = ReadFile(fileName)
    val jsonParser = new JsonParser(fileContent)
    Parameters (
      batchTime = jsonParser.getStreamingTime,
      kafkaProps = jsonParser.getKafkaProps,
      sourceProps = jsonParser.getSource
    )
  }
}




