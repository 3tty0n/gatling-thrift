assembly:
	sbt gatling-thrift-example/assembly

load-test-cli: assembly
	java -jar gatling-thrift-example/target/scala-2.12/gatling-thrift-example.jar --simulation simulation.ThriftSimulationExample

load-test-sbt:
	sbt "gatling-thrift-example/gatling:test"
