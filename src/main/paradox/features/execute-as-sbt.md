# Execute as sbt

## Set up

Define your sbt settings as below:

```scala
lazy val root = (project in file(".")).
  enableplugins(Gatlingplugin).
  settings(
    name := "load-test",
    scalaVersion := "2.12.3",
    librarydependencies ++= Seq(
      "com.github.3tty0n" %% "gatling-thrift" % "0.4.2",
      "io.gatling" % "gatling-test-framework" % "2.3.0" % "test,it",
      "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.3.0" % "test,it"
    )
  )
```

## Define your simulation

Add your `thrift` file in `src/main/thrift`, and create your simulation in `src/test/scala` direcotry.

```scala
package simulation

import io.gatling.thrift.Predef._

class ThriftSimulationExample extends ThriftSimulation {
  ...
}
```

Then call `sbt gatling:test` which will execute your scenario of the load test.
