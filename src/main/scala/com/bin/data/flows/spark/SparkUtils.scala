package com.bin.data.flows.spark

import org.apache.spark.sql.SparkSession

trait SparkUtils {

  implicit val sparkSession = SparkSession
    .builder()
    .appName("FlowsApp")
    .enableHiveSupport()
    .getOrCreate()
}

