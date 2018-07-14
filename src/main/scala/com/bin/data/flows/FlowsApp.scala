package com.bin.data.flows

import com.bin.data.flows.exceptions.ConfigParamsException
import com.bin.data.flows.file.ReadFile
import com.bin.data.flows.params.ConfigParams
import com.bin.data.flows.spark.SparkUtils
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.LoggerFactory
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe



object FlowsApp extends App {

  val logger = LoggerFactory.getLogger(getClass.getName)
  logger.info("This is a test")

  implicit val session = SparkUtils.get(getClass.getName)
  val ssc = new StreamingContext(
    session.sparkContext,
    Seconds(5)
  )

  println("Starting the App")
  ConfigParams.getParams(args) match {
    case Some(config) =>

      val fileContent = ReadFile(config.configFile)
      println(fileContent)

      val kafkaParams = Map[String, Object](
        "bootstrap.servers" -> "localhost:9092",
        "key.deserializer" -> classOf[StringDeserializer],
        "value.deserializer" -> classOf[StringDeserializer],
        "group.id" -> "consumer_kafka_6001",
        "auto.offset.reset" -> "earliest",
        "enable.auto.commit" -> (false: java.lang.Boolean)
      )

      val topics = Array(config.topicName)
      val stream = KafkaUtils.createDirectStream[String, String](
        ssc,
        PreferConsistent,
        Subscribe[String, String](topics, kafkaParams)
      )

      val feeds = stream.map(_.value())
      feeds.foreachRDD { rdd =>
        if (!rdd.isEmpty) {
          import session.implicits._
          val df = rdd.toDS()
          df.show()
        } else {
          println("Not empty")
        }
      }

      ssc.start()
      ssc.awaitTermination()
      ssc.stop(stopSparkContext = true, stopGracefully = true)

    case _ => throw new ConfigParamsException("FlowsApp expects more parameters")
  }
}
