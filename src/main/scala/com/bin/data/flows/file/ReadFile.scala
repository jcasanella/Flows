package com.bin.data.flows.file

import org.apache.spark.SparkFiles
import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory

import scala.io.Source

class ReadFile {

  val logger = LoggerFactory.getLogger(getClass.getName)

  private def use[A <: { def close(): Unit }, B](resource: A)(code: A ⇒ B): B = {
    try
      code(resource)
    finally
      resource.close()
  }

  def getContent(fileName: String)(implicit spark: SparkSession): String = {

    require(fileName != null, "File name is null!!!")

    logger.info(s"This is the fileName: $fileName")
    println(s"This is the fileName: $fileName")

    spark.sparkContext.addFile(fileName)
    val fileLoc = SparkFiles.get(fileName)
    use(Source.fromFile(fileLoc)) {
      _.mkString
    }
  }
}

object ReadFile {

  def apply(fileName: String)(implicit spark: SparkSession): String = {

    val rf = new ReadFile
    rf.getContent(fileName)
  }
}
