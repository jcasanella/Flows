package com.bin.data.flows

import com.bin.data.flows.exceptions.ConfigParamsException
import com.bin.data.flows.file.ReadFile
import com.bin.data.flows.json.{JsonParser, KafkaConfig}
import com.bin.data.flows.params.ConfigParams
import com.bin.data.flows.spark.SparkUtils
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.LoggerFactory
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe



object FlowsApp extends App {

  def readJson(fileName: String): (Long, KafkaConfig) = {
    val fileContent = ReadFile(fileName)
    val jp = new JsonParser(fileContent)
    val batchTime = jp.getStreamingTime()
    val kafkaProps = jp.getKafka()

    (batchTime, kafkaProps)
  }

  val logger = LoggerFactory.getLogger(getClass.getName)
  logger.info("Starting the App")

  ConfigParams.getParams(args) match {
    case Some(config) =>

      implicit val session = SparkUtils.get(getClass.getName)

      val jsonElems = readJson(config.configFile)

      val ssc = new StreamingContext(
        session.sparkContext,
        Seconds(jsonElems._1)
      )

      val topics = Array(jsonElems._2.topicConsumer)
      val stream = KafkaUtils.createDirectStream[String, String](
        ssc,
        PreferConsistent,
        Subscribe[String, String](topics, jsonElems._2.properties)
      )

      val feeds = stream.map(_.value())
      feeds.foreachRDD { rdd =>
        if (!rdd.isEmpty) {
          import session.implicits._
          val df = rdd.toDS()
          df.show()
        } else {
          println("Empty!!!!")
        }
      }

      ssc.start()
      ssc.awaitTermination()
      ssc.stop(stopSparkContext = true, stopGracefully = true)

    case _ => throw new ConfigParamsException("FlowsApp expects more parameters")
  }
}
