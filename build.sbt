import sbt.Keys._

parallelExecution in ThisBuild := false

lazy val versions = new {
  val finatra = "2.11.0"
  val scalatest = "3.0.0"
  val specs2 = "2.4.17"
  val gatling = "2.2.1"
  val akka = "2.4.16"
  val config = "1.3.1"
}

lazy val baseSettings = Seq(
  version := "0.1.0-SNAPSHOT",
  organization := "com.github.3tty0n",
  scalaVersion := "2.11.11",
  scalafmtVersion in ThisBuild := "1.0.0-RC2",
  ivyScala := ivyScala.value.map(_.copy(overrideScalaVersion = true)),
  scalacOptions := Seq(
    "-encoding",
    "UTF-8",
    "-target:jvm-1.8",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:implicitConversions",
    "-language:postfixOps"
  ),
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % versions.scalatest % "test",
    "com.typesafe" % "config" % versions.config
  ),
  resolvers += Resolver.sonatypeRepo("releases"),
  fork in run := true
)

lazy val assemblySettings = Seq(assemblyMergeStrategy in assembly := {
  case PathList("io", "netty", xs @ _ *) => MergeStrategy.first
  case meta(_)                           => MergeStrategy.discard
  case "BUILD"                           => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}, test in assembly := {})

lazy val meta = """META.INF(.)*""".r

lazy val noPublishSettings = Seq(publish := {}, publishLocal := {})

lazy val root = (project in file("."))
  .settings(noPublishSettings)
  .settings(name := "gatling-thrift")
  .aggregate(`gatling-thrift`, `gatling-thrift-example`)

lazy val `gatling-thrift` = (project in file("gatling-thrift"))
  .enablePlugins(JavaAppPackaging, UniversalDeployPlugin)
  .settings(baseSettings, assemblySettings)
  .settings(
    name := "gatling-thrift",
    libraryDependencies ++= Seq(
      "io.gatling" % "gatling-app" % versions.gatling,
      "io.gatling" % "gatling-test-framework" % versions.gatling,
      "io.gatling.highcharts" % "gatling-charts-highcharts" % versions.gatling,
      "com.typesafe.akka" %% "akka-stream" % versions.akka,
      "com.twitter" %% "finatra-thrift" % versions.finatra
    ),
    assemblyJarName in assembly := "gatling-thrift.jar",
    mainClass in assembly := Some(
      "io.gatling.thrift.testrunner.GatlingRunner"
    ),
    mappings in Universal := {
      val universalMappings = (mappings in Universal).value
      val fatJar = (assembly in Compile).value
      val filtered = universalMappings.filter {
        case (file, name) => !name.endsWith(".jar")
      }
      filtered :+ (fatJar -> ("lib/" + fatJar.getName))
    },
    scriptClasspath := Seq((assemblyJarName in assembly).value),
    publish := (publish in Universal).value,
    publishLocal := (publishLocal in Universal).value
  )

lazy val `gatling-thrift-example` = (project in file("gatling-thrift-example"))
  .enablePlugins(GatlingPlugin, JavaAppPackaging, UniversalDeployPlugin)
  .settings(baseSettings, assemblySettings)
  .settings(
    name := "gatling-thrift-example",
    assemblyJarName in assembly := "gatling-thrift-example.jar",
    mainClass in assembly := Some(
      "simulation.ThriftSimulationMain"
    ),
    mappings in Universal := {
      val universalMappings = (mappings in Universal).value
      val fatJar = (assembly in Compile).value
      val filtered = universalMappings.filter {
        case (file, name) => !name.endsWith(".jar")
      }
      filtered :+ (fatJar -> ("lib/" + fatJar.getName))
    },
    scriptClasspath := Seq((assemblyJarName in assembly).value),
    publish := (publish in Universal).value,
    publishLocal := (publishLocal in Universal).value
  )
  .dependsOn(`gatling-thrift`)
