package com.bin.data.flows

import com.bin.data.flows.exceptions.ConfigParamsException
import com.bin.data.flows.json.Parameters
import com.bin.data.flows.params.ConfigParams
import com.bin.data.flows.source.Source
import com.bin.data.flows.spark.SparkUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.LoggerFactory

object FlowsApp extends App with SparkUtils {

  val logger = LoggerFactory.getLogger(getClass.getName)
  logger.info("Starting the App")

  ConfigParams.getParams(args) match {
    case Some(config) =>

      val params = Parameters(config.configFile)

      val ssc = new StreamingContext(
        sparkSession.sparkContext,
        Seconds(params.batchTime)
      )

//      val topics = Array(jsonElems._2.topicConsumer)
//      val stream = KafkaUtils.createDirectStream[String, String](
//        ssc,
//        PreferConsistent,
//        Subscribe[String, String](topics, jsonElems._2.properties)
//      )
//
//      val feeds = stream.map(_.value())

      val source = Source(params)
      val feeds = source.read

      feeds.foreachRDD { rdd =>
        if (!rdd.isEmpty) {
          import sparkSession.implicits._
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
