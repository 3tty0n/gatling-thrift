import sbt.Keys._

parallelExecution in ThisBuild := false

lazy val versions = new {
  val finatra = "2.10.0"
  val guice = "4.0"
  val logback = "1.1.7"
  val scalatest = "3.0.0"
  val specs2 = "2.4.17"
  val gatling = "2.2.1"
  val akka = "2.4.16"
  val config = "1.3.1"
}

lazy val baseSettings = Seq(
  version := "1.0.0-SNAPSHOT",
  organization := "com.github.3tty0n",
  scalaVersion := "2.11.11",
  scalafmtVersion in ThisBuild := "1.0.0-RC2",
  scalafmtOnCompile in ThisBuild := true,
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
  case "BUILD"                           => MergeStrategy.discard
  case PathList("io", "netty", xs @ _ *) => MergeStrategy.first
  case meta(_)                           => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}, test in assembly := {})

lazy val meta = """META.INF(.)*""".r

lazy val root = (project in file("."))
  .settings(name := "finatra-thrift-server-example", run := {
    (run in `server` in Compile).evaluated
  }, publish := {}, publishLocal := {})
  .aggregate(server, idl, loadtest)

lazy val server = (project in file("server"))
  .settings(baseSettings)
  .settings(
    name := "thrift-server",
    moduleName := "thrift-server",
    mainClass in (Compile, run) := Some("org.example.ExampleServerMain"),
    javaOptions ++= Seq(
      "-Dlog.service.output=/dev/stderr",
      "-Dlog.access.output=/dev/stderr"
    ),
    libraryDependencies ++= Seq(
      "com.twitter" %% "finatra-thrift" % versions.finatra,
      "ch.qos.logback" % "logback-classic" % versions.logback,
      "com.twitter" %% "finatra-thrift" % versions.finatra % "test",
      "com.twitter" %% "inject-app" % versions.finatra % "test",
      "com.twitter" %% "inject-core" % versions.finatra % "test",
      "com.twitter" %% "inject-modules" % versions.finatra % "test",
      "com.twitter" %% "inject-server" % versions.finatra % "test",
      "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",
      "com.twitter" %% "finatra-thrift" % versions.finatra % "test" classifier "tests",
      "com.twitter" %% "inject-app" % versions.finatra % "test" classifier "tests",
      "com.twitter" %% "inject-core" % versions.finatra % "test" classifier "tests",
      "com.twitter" %% "inject-modules" % versions.finatra % "test" classifier "tests",
      "com.twitter" %% "inject-server" % versions.finatra % "test" classifier "tests"
    ),
    publish := {},
    publishLocal := {}
  )
  .dependsOn(idl)

lazy val idl = (project in file("idl"))
  .settings(baseSettings)
  .settings(
    name := "thrift-idl",
    moduleName := "thrift-idl",
    scroogeThriftDependencies in Compile := Seq("finatra-thrift_2.11"),
    libraryDependencies ++= Seq(
      "com.twitter" %% "finatra-thrift" % versions.finatra
    ),
    publish := {},
    publishLocal := {}
  )

lazy val loadtest = (project in file("loadtest"))
  .enablePlugins(GatlingPlugin, JavaAppPackaging, UniversalDeployPlugin)
  .settings(baseSettings, assemblySettings)
  .settings(
    name := "gatling-loadtest",
    libraryDependencies ++= Seq(
      "io.gatling" % "gatling-app" % versions.gatling,
      "io.gatling" % "gatling-test-framework" % versions.gatling,
      "io.gatling.highcharts" % "gatling-charts-highcharts" % versions.gatling
        exclude ("io.gatling", "gatling-recorder"),
      "com.typesafe.akka" %% "akka-stream" % versions.akka
    ),
    assemblyJarName in assembly := "gatling-loadtest.jar",
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
  .dependsOn(idl)
