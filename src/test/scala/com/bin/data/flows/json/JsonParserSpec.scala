package com.bin.data.flows.json

import com.bin.data.flows.exceptions.JsonParserException
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source
import scala.util.Try

class JsonParserSpec extends FlatSpec with Matchers {

  val configOk = "/config.json"
  val configError = "/configError.json"

  private class ReadResource(fileName: String) {

    val content = {
      val stream = getClass.getResourceAsStream(fileName)
      Try(Source.fromInputStream(stream).mkString)
    }

    val jsonContent = new JsonParser(content.get)
  }

  "element spark.streamingTime" should "exist" in new ReadResource(configOk){
    val streamingTime = jsonContent.getStreamingTime()
    streamingTime should ===(3)
  }

  "spark element incorrect" should "throw JsonParserException" in new ReadResource(configError){
    an [JsonParserException] should be thrownBy jsonContent.getStreamingTime()
  }

  "kafka element incorrect" should "throw JsonParserException" in new ReadResource(configError){
    an [JsonParserException] should be thrownBy jsonContent.getKafkaProps()
  }

  "kafka.properties" should "not be empty" in new ReadResource(configOk){
    val kafkaConfig = jsonContent.getKafkaProps()
    kafkaConfig should not be empty
  }

  "kafka.properties" should "have defined bootstrap, key, value, group.id, reset, commit" in new ReadResource(configOk){
    val kafkaConfig = jsonContent.getKafkaProps()

    // Checking the content of the map
    kafkaConfig should contain ("bootstrap.servers" -> "localhost:9092")
    kafkaConfig should contain ("key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaConfig should contain ("value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaConfig should contain ("group.id" -> "consumer_kafka_6001")
    kafkaConfig should contain ("auto.offset.reset" -> "earliest")
    kafkaConfig should contain ("enable.auto.commit" -> "false")
  }

  "source properties" should "have defined name, typeSource and topicConsumer" in new ReadResource(configOk) {
    val sourceConf = jsonContent.getSource()

    // Checking the content of the source
    sourceConf.topicConsumer should not be empty
    sourceConf.name should not be empty
    sourceConf.typeSource should not be empty
  }

  "source properties incorrect" should "throw JsonParserException" in new ReadResource(configError) {
    an [JsonParserException] should be thrownBy jsonContent.getSource
  }
}
