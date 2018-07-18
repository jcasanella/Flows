package com.bin.data.flows.params

import scopt.OptionParser

case class Config(configFile: String = "")
class ConfigParams {

  val parser = new scopt.OptionParser[Config]("Flows") {

    head("Flows", "1.0")

    opt[String]('c', "configFile") required() valueName("<configFile>") action { (x, c) =>
      c.copy(configFile = x) } text("configFile is the configuration file")

    help("help").text("prints how to usage this app")
  }
}

object ConfigParams {

  def getParams(args: Array[String]): Option[Config] = {

    val cp = new ConfigParams()
    cp.parser.parse(args, Config())
  }
}
