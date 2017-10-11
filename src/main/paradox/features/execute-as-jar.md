# Execute as jar

Your can execute your load test in CLI, not using sbt.

## Set up

First you define `build.sbt` as @ref:[this](execute-as-sbt.md#set-up).

## Define your simulation

Create main class in `src/main/scala`, not `src/main/test`:

```scala
package simulation

import io.gatling.app.runner.GatlingRunner
import io.gatling.thrift.Predef._

object ThriftSimulationMain extends GatlingRunner

class ThriftSimulationExample extends ThriftSimulation {
  ...
}
```

## sbt assembly
Add sbt assembly to `project/plugins.sbt`:

```scala
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")
```

And define sbt assembly settings as below:

```scala
lazy val root = (project in file(".")).
  settings(assemblysettings).
  settings(...)

lazy val assemblySettings = Seq(
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
  assemblyJarName in assembly := "load-test.jar",
  mainClass in assembly := Some("simulation.ThriftSimulationMain"),
)
```

## Execute

Create fat jar as `sbt assembly` and execute it:

```bash
$ sbt assembly
$ java -jar target/scala-2.12/load-test.jar --simulation simulation.ThriftSimulationExample
```
