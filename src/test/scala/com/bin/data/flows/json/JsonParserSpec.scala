package com.bin.data.flows.json

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.io.Source

class JsonParserSpec extends FlatSpec with Matchers with BeforeAndAfter {

  var content: String = _

  before {
    val stream = getClass.getResourceAsStream("/config.json")
    content = Source.fromInputStream(stream).mkString
  }

  "config.json" should "have content" in {

    content.size should be >= 0
  }

  "file" should "content property spark" in {

    val jp = new JsonParser(content)
    val streamingTime = jp.getStreamingTime()

    streamingTime should ===(3)
  }

  "file" should "content property kafka" in {

    val jp = new JsonParser(content)
    val kafkaConfig = jp.getKafka()

    kafkaConfig.properties.foreach(row => println(s"${row._1} -> ${row._2}"))

    kafkaConfig.topicConsumer should ===("peopleDat")
  }
}
