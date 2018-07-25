package com.bin.data.flows.json

import com.bin.data.flows.exceptions.JsonParserException
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.io.Source
import scala.util.Try

class JsonParserSpec extends FlatSpec with Matchers with BeforeAndAfter {

  val configOk = "/config.json"
  val configError = "/configError.json"

  private class ReadResource(fileName: String) {

    val content = {
      val stream = getClass.getResourceAsStream(fileName)
      Try(Source.fromInputStream(stream).mkString)
    }

    val jsonContent = new JsonParser(content.get)
  }

  private object ReadResource {
    def apply(fileName: String): ReadResource = {
      new ReadResource(fileName)
    }
  }

  "config.json spark element" should "exist" in {
    val readRes = ReadResource(configOk)
    val streamingTime = readRes.jsonContent.getStreamingTime()
    streamingTime should ===(3)
  }

  "configError.json spark element incorrect" should "throw the exception JsonParserException" in {
    val readRes = ReadResource(configError)
    an [JsonParserException] should be thrownBy readRes.jsonContent.getStreamingTime()
  }

  "configError.json kafka element incorrect" should "throw the exception JsonParserException" in {
    val readRes = ReadResource(configError)
    an [JsonParserException] should be thrownBy readRes.jsonContent.getKafka()
  }

  "config.json kafka element" should "exist" in {
    val readRes = ReadResource(configOk)
    val kafkaConfig = readRes.jsonContent.getKafka()
    kafkaConfig.topicConsumer should ===("peopleData")
    // TODO: Check map content
  }
}
