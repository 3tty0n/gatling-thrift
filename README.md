# gatling-thrift

[![Build Status](https://travis-ci.org/3tty0n/gatling-thrift.svg?branch=master)](https://travis-ci.org/3tty0n/gatling-thrift)
 [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.3tty0n/gatling-thrift_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.3tty0n/gatling-thrift_2.12)


This is a [Gatling](http://gatling.io/) third party Thrift plugin for [Finatra Thrift Server](https://twitter.github.io/finatra/user-guide/thrift/server.html).

You can execute your load test as:
 - **sbt**
 - **jar** (in command line)

## Set up

Builds are available for Scala 2.11.x, and for Scala 2.12.x. The main line of development of gatling-thrift is 2.12.3.

1. In `build.sbt`, add:
    1. If you use Scala 2.12.x and Gatling 2.3.x:
       ```scala
       libraryDependencies += "com.github.3tty0n" %% "gatling-thrift" % "0.4.2"
       ```

    1. If you use Scala 2.11.x and Gatling 2.2.x:
       see this [documentation](https://github.com/3tty0n/gatling-thrift/tree/0.1.0#gatling-thrift).
       ```scala
       libraryDependencies += "com.github.3tty0n" %% "gatling-thrift" % "0.1.0"
       ```

1. In `project/plugins.sbt`, add:

    ```scala
    addSbtPlugin("com.twitter" % "scrooge-sbt-plugin" % "4.20.0")
    addSbtPlugin("io.gatling" % "gatling-sbt" % "2.2.2")
    ```

1. And enable GatlingPlugin.

    ```scala
    enablePlugins(GatlingPlugin)
    ```

## Usage

First, you should define `client` and `thriftProtocol`

``` scala
import io.gatling.thrift.Predef._

val client = Thrift.client.newIface[PingService.FutureIface]("localhost:9911")

implicit val thriftProtocol: ThriftProtocol = thrift
  .port(9911)
  .host("localhost")
  .requestName("example request")
```

### Simple

```scala
def callback: Future[String] = {
  client.echo(new Random().nextInt().toString)
}

val scn = senario("Thrift Scenario").repeqt(100) {
  exec(callback.action)
}

setUp(scn.inject(nothingFor(4 seconds), atOnceUsers(100)))
```

### Session

```scala
def callback: Session => Future[String] = { session =>
  clinet.echo(session("randNum").as[Int].toString)
}

val scn = senario("Session Scenario").repeqt(100) {
  exec { session =>
    session.set("randNum", new Random().nextInt
  }.exec(callback.action)
}

setUp(scn.inject(nothingFor(4 seconds), atOnceUsers(100)))
```

### Feeder

```scala
/**
 * Note: Please set `orders.csv` at resources directory.
 * accountId, sym, qty, price
 * 1L, 7203.T, 10, 6700
 * 2L, 7100.T, 5, 322
 * 3L, 8100.T, 40, 788
 */
val orderFeed = csv("orders.csv").random

def callback: Session => Future[String] = { session =>
  clinet.echo(session("sym").as[String])
}

val scn = senario("Feeder Scenario").repeqt(100) {
  feed(orderFeed)
    .exec(callback.action)
}

setUp(scn.inject(nothingFor(4 seconds), atOnceUsers(100)))
```


## Execute as sbt

1. Define sbt settings. Please see [it](https://github.com/3tty0n/gatling-thrift/blob/master/gatling-thrift-example/resources/build.sbt.sample).

1. Add your thrift file in `src/main/thrift` directory

1. Create your simulation in `src/test/scala` directory

    ```scala
    package simulation

    import io.gatling.thrift.Predef._

    class ThriftSimulationExample extends ThriftSimulation {
      ...
    }
    ```

1. Execte as below.

    ``` bash
    $ sbt gatling-thrift-example/gatling:test
    ```

## Execute as jar

1. Implement Main `object` in `src/main/scala`

    ```scala
    package simulation

    import io.gatling.app.runner.GatlingRunner
    import io.gatling.thrift.Predef._

    object ThriftSimulationMain extends GatlingRunner

    class ThriftSimulationExample extends ThriftSimulation {
      ...
    }
    ```

2. Enable sbt assembly

    ```scala
    addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")
    ```

3. Define `sbt-assembly` settings as below

    ```scala
    lazy val assemblySettings =
      Seq(
        assemblyMergeStrategy in assembly := {
          case PathList("io", "netty", xs @ _ *) => MergeStrategy.first
          case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
          case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.first
          case PathList("META-INF", "services", _) => MergeStrategy.concat
          case "BUILD" => MergeStrategy.discard
          case x =>
            val oldStrategy = (assemblyMergeStrategy in assembly).value
            oldStrategy(x)
        },
        test in assembly := {},
        assemblyJarName in assembly := "gatling-thrift-example.jar",
        mainClass in assembly := Some("simulation.ThriftSimulationMain"),
      )
    ```

4. Create fat jar

    ```bash
    $ sbt gatling-thrift-example/assembly
    ```

5. Execute as below

    ```bash
    $ java -jar gatling-thrift-example/target/scala-2.12/gatling-thrift-example.jar \
        --simulation simulation.ThriftSimulationExample
    ```

## Publish

You can publish your simulation as zip by using `sbt-native-packager` and `sbt-assembly`.

1. Enable `sbt-native-packager` and `sbt-assembly` plugin
    1. In `project/plugins.sbt`, add:

        ```scala
        addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0")
        addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")
        ```

    1. In `build.sbt`, add:

          ```scala
          enablePlugins(GatlingPlugin, JavaAppPackaging, UniversalDeployPlugin)
          ```

1. Add settings as below

    ```scala
    lazy val yourProject = (project in file("your-project"))
      .settings(
        publishSettings,
        assemblySettings
      )
      .settings(
        ...
      )

    lazy val assemblySettings = ...

    lazy val publishSettings = Seq(
      mappings in Universal := {
        val universalMappings = (mappings in Universal).value
        val fatJar = (assembly in Compile).value
        val filtered = universalMappings.filter {
          case (file, name) => !name.endsWith(".jar")
        }
        filtered :+ (fatJar -> ("lib/" + fatJar.getName))
      },
      scriptClasspath := Seq((assemblyJarName in assembly).value)
      publish := (publish in Universal).value
      // if you want to publish to local repository, add `publishLocal := (publish in Universal).value`
    )
    ```

1. Execute publish task

    ```bash
    $ sbt gatling-thrift-example/publish # if you want to publish to local repository, execute `sbt gatling-thrift-example/publishLocal`
    ```

1. If you want to execute the load test packaged by sbt-native-packager, execute commands as below.

    ```bash
    $ export VERSION=$(cat version.sbt | sed -e "s/[^0-9.]//g")
    $ cd /path/to/gatling-thrift-example/$VERSION/zips
    $ unzip gatling-laodtest.zip
    $ cd gatling-loadtest-$VERSION
    $ bin/gatling-thrift-example -s simulation.ThriftSimulationExample
    ```

## Development

1. Start the server

    ```bash
    $ sbt gatling-thrift-example/docker:publishLocal
    $ export VERSION=$(cat version.sbt | sed -e "s/[^0-9.]//g")
    $ docker run -it -p 127.0.0.1:9911:9911 --rm -d micchon/gatling-thrift-example:$VERSION bin/gatling-thrift-example
    ```

2. Execute the test

    ```bash
    $ sbt gatling-thrift-example/gatling:test
    ```

## How to construct the scenario of the load testing

- [Inject](http://gatling.io/docs/current/general/simulation_setup/)
  - [Assersions](http://gatling.io/docs/current/general/assertions/#assertions)
- [Scenario](http://gatling.io/docs/current/general/scenario/)
