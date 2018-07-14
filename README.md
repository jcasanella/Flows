set SBT_OPTS="-Xmx2G"

sbt assembly

topicName = peopleData

kafka-console-consumer --topic peopleData --new-consumer --from-beginning --bootstrap-server localhost:9092

type d:\development\projects\Flows\src\main\resources\people.json | kafka-console-producer.bat --topic peopleData --broker-list localhost:9092

Hadoop
spark-submit2 --files d:\development\projects\Flows\src\main\resources\config.json#config.json --class com.bin.data.flows.FlowsApp --master local[1] d:\development\projects\Flows\target\scala-2.11\FlowsApp-snapshot-0.1.jar -t peopleData -c config.json

Local
copy d:\development\projects\Flows\src\main\resources\config.json config.json
spark-submit2 --files d:\development\projects\Flows\src\main\resources\config.json --class com.bin.data.flows.FlowsApp --master local[1] d:\development\projects\Flows\target\scala-2.11\FlowsApp-snapshot-0.1.jar -t peopleData -c config.json














