package com.bin.data.flows.spark

import org.apache.spark.sql.SparkSession

class SparkUtils(appName: String) {

  val sparkSession = SparkSession
    .builder()
    .appName(appName)
    .enableHiveSupport()
    .getOrCreate()

//  sparkSession.conf.set("", "")
}

object SparkUtils {

  def get(appName: String): SparkSession = {
    new SparkUtils(appName).sparkSession
  }
}
