package com.bin.data.flows.source

import com.bin.data.flows.exceptions.SourceTypeException
import com.bin.data.flows.json.Parameters
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.slf4j.LoggerFactory

sealed abstract class Source(name: String) {
  def read()(implicit ssc: StreamingContext): DStream[String]
}
class KafkaSource(name: String, topicName: String, properties: Map[String, String]) extends Source(name: String) {

  val logger = LoggerFactory.getLogger(getClass.getName)


  override def read()(implicit ssc: StreamingContext): DStream[String] = {

    logger.info(">>> read!!!!!!")

    KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](Seq(topicName), properties)
    ).map(_.value)
  }
}

object Source {
  val logger = LoggerFactory.getLogger(getClass.getName)

  def apply(config: Parameters): Source = {

    config.sourceProps.typeSource match {
      case "kafka" =>

        logger.info(s">>> Kafka consumer from topic ${config.sourceProps.topicConsumer}")
        new KafkaSource(
          name = config.sourceProps.name,
          topicName = config.sourceProps.topicConsumer,
          properties = config.kafkaProps
        )

      case _ => throw new SourceTypeException("Source Type invalid")
    }
  }
}
