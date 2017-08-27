import sbt.Keys._

parallelExecution in ThisBuild := false

lazy val versions = new {
  val finatra = "2.11.0"
  val scalatest = "3.0.0"
  val gatling = "2.2.1"
  val akka = "2.4.16"
}

lazy val baseSettings = Seq(
  version := "0.1.0-SNAPSHOT",
  organization := "com.github.3tty0n",
  scalaVersion := "2.11.11",
  scalafmtVersion in ThisBuild := "1.0.0-RC2",
  scalafmtOnCompile := true,
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
    "org.scalatest" %% "scalatest" % versions.scalatest % "test"
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

lazy val publishSettings = Seq(
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    pomExtra :=
      <url>https://github.com/3tty0n/gatling-thrift</url>
      <developers>
        <developer>
          <id>3tty0n</id>
          <name>Yusuke Izawa</name>
          <url>https://github.com/3tty0n</url>
        </developer>
      </developers>
      <scm>
        <url>git@github.com:3tty0n/gatling-thrift.git</url>
        <connection>scm:git:git@github.com:3tty0n/gatling-thrift.git</connection>
      </scm>
  )

lazy val root = (project in file("."))
  .settings(publishSettings)
  .settings(name := "gatling-thrift")
  .aggregate(`gatling-thrift`, `gatling-thrift-example`)

lazy val `gatling-thrift` = (project in file("gatling-thrift"))
  .settings(baseSettings)
  .settings(
    name := "gatling-thrift",
    libraryDependencies ++= Seq(
      "io.gatling" % "gatling-app" % versions.gatling,
      "io.gatling" % "gatling-test-framework" % versions.gatling,
      "io.gatling.highcharts" % "gatling-charts-highcharts" % versions.gatling,
      "com.typesafe.akka" %% "akka-stream" % versions.akka,
      "com.twitter" %% "finatra-thrift" % versions.finatra
    )
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
    publish := {},
    publishLocal := (publishLocal in Universal).value
  )
  .dependsOn(`gatling-thrift`)
