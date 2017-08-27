# gatling-thrift

This is a load testing tool for thrift server.

You can execute your load test as:
 - **sbt**
 - **jar** (in command line)

## Execute as sbt

1. Add `gatling-thrift` dependency

```scala
libraryDependencies += "com.github.3tty0n" %% "gatling-thrift" % "0.1.0-SNAPSHOT"
```

2. Enable gatling and scrooge plugin

```scala
addSbtPlugin("com.twitter" % "scrooge-sbt-plugin" % "4.18.0")

addSbtPlugin("io.gatling" % "gatling-sbt" % "2.2.1")
```

3. Define sbt settings. Please see [it](https://github.com/3tty0n/gatling-thrift/blob/master/gatling-thrift-example/resources/build.sbt.sample).

4. Add your thrift file in `src/main/thrift` directory

5. Create your simulation in `src/test` directory

``` scala
package simulation

import com.twitter.finagle.Thrift
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.thrift.Predef._
import io.gatling.thrift.action.ThriftActionBuilder
import io.gatling.thrift.testrunner.GatlingRunner
import org.micchon.ping.thriftscala.PingService

import scala.concurrent.duration._
import scala.util.Random

class ThriftSimulationExample
    extends ThriftSimulation[PingService.FutureIface] {
  override val client: PingService.FutureIface =
    Thrift.client.newIface[PingService.FutureIface]("localhost:9911")

  override val thriftAction: ActionBuilder =
    ThriftActionBuilder(
      "localhost",
      9911,
      "Thrift Action",
      client.echo(new Random().nextInt().toString)
    )

  override val scn: ScenarioBuilder =
    scenario("Thrift Scenario").repeat(2)(exec(thriftAction))

  setUp(
    scn.inject(
      nothingFor(4 seconds),
      atOnceUsers(10),
      rampUsers(10) over (5 seconds),
      constantUsersPerSec(20) during (15 seconds),
      constantUsersPerSec(20) during (15 seconds) randomized,
      rampUsersPerSec(10) to 20 during (3 seconds),
      rampUsersPerSec(10) to 20 during (2 seconds) randomized,
      splitUsers(20) into (rampUsers(10) over (10 seconds)) separatedBy (10 seconds),
      splitUsers(20) into (rampUsers(10) over (10 seconds)) separatedBy atOnceUsers(
        30
      ),
      heavisideUsers(50) over (20 seconds)
    )
  ).assertions(
    global.responseTime.max.lessThan(1000),
    global.successfulRequests.percent.greaterThan(95)
  )

}
```

6. Execte as below.

``` bash
$ sbt gatling-thrift-example/gatling:test
```

## Execute as jar

1. Create your simulation

2. Implement Main `object`


``` scala
package simulation

import io.gatling.thrift.testrunner.GatlingRunner
import io.gatling.thrift.Predef._

object ThriftSimulationMain extends GatlingRunner

class ThriftSimulationExample extend ThriftSimulation[YourServce] {
  ...
}
```

3. Enable sbt assembly

``` scala
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")
```

4. Define `sbt-assembly` settings as below

``` scala
assemblyJarName in assembly := "gatling-thrift-example.jar"

mainClass in assembly := Some("simulation.ThriftSimulationMain"),
```

5. Create fat jar

``` bash
$ sbt gatling-thrift-example/assembly
```

6. Execute as below

``` bash
$ gatling-thrift-example/target/scala-2.11/gatling-loadtest-example.jar \
    --simulation simulation.ThriftSimulationExample
```

## Publish

You can publish your simulation as zip by using `sbt-native-packager` and `sbt-assembly`.

1. Add `sbt-native-packager` and `sbt-assembly` plugin

``` scala
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")
```

2. Add settings as below

``` scala
assemblyJarName in assembly := "gatling-thrift-example.jar"
 
mainClass in assembly := Some("simulation.ThriftSimulationMain")
 
mappings in Universal := {
  val universalMappings = (mappings in Universal).value
  val fatJar = (assembly in Compile).value
  val filtered = universalMappings.filter {
    case (file, name) => !name.endsWith(".jar")
  }
  filtered :+ (fatJar -> ("lib/" + fatJar.getName))
}

scriptClasspath := Seq((assemblyJarName in assembly).value),

publish := (publish in Universal).value  // if you want to publish to local repository, add `publishLocal := (publish in Universal).value`
```

3. Execute publish task

``` bash
$ sbt gatling-thrift-exampoe/publish # if you want to publish to local repository, execute `sbt gatling-thrift-example/publishLocal`
```

If you want to execute the load test packaged by sbt-native-packager, execute commands as below.

``` bash
$ cd /path/to/gatling-thrift-example/0.1.0-SNAPSHOT/zips
$ unzip gatling-laodtest.zip
$ cd gatling-loadtest-1.0.0-SNAPSHOT
$ bin/gatling-thrift-example -s simulation.ThriftSimulationExample
```

## How to construct the scenario of the load testing

- [Inject](http://gatling.io/docs/current/general/simulation_setup/)
  - [Assersions](http://gatling.io/docs/current/general/assertions/#assertions)
- [Scenario](http://gatling.io/docs/current/general/scenario/)
