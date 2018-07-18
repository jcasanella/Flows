val Name = "FlowsApp"
val Version = "0.1"

lazy val root = (project in file("."))
  .settings(
    name := Name,
    version := Version,
    scalaVersion := "2.11.8",
    mainClass in assembly := Some("com.bin.data.flows.FlowsApp"),
    mainClass in Compile := Some("com.bin.data.flows.FlowsApp"),
    assemblyJarName := s"$Name-snapshot-$Version.jar"
  )

val sparkVersion = "2.1.0"

// This is to prevent error [java.lang.OutOfMemoryError: PermGen space]
javaOptions += "-XX:MaxPermSize=256m"

javaOptions += "-Xmx1024m"

libraryDependencies ++= Seq(
  // Spark Dependencies
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-hive" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion,

  // Kafka Client
  "org.apache.kafka" % "kafka-clients" % "0.10.0.0",

  // Scopt - parser options
  "com.github.scopt" %% "scopt" % "3.7.0",

  // Play json parser
  "com.typesafe.play" %% "play-json" % "2.6.7",

  // kafka unit test https://github.com/chbatey/kafka-unit
  "info.batey.kafka" % "kafka-unit" % "0.7",

  // hBase unit test - minicluster
  "org.apache.hbase" % "hbase-testing-util" % "2.0.0" % Test,

  "log4j" % "log4j" % "1.2.14"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}



