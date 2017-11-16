import sbt.Keys._
import ReleaseTransformations._
parallelExecution in ThisBuild := false

lazy val versions = new {
  val finatra   = "2.13.0"
  val logback   = "1.1.7"
  val scalatest = "3.0.3"
  val jackson   = "2.9.0"
  val guice     = "4.0"
  val gatling   = "2.3.0"
}

lazy val baseSettings = Seq(
  organization := "com.github.3tty0n",
  scalaVersion := "2.12.4",
  scalafmtOnCompile := true,
  ivyScala := ivyScala.value.map(_.copy(overrideScalaVersion = true)),
  scalacOptions := Seq(
    "-encoding",
    "UTF-8",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:implicitConversions",
    "-language:postfixOps"
  ),
  libraryDependencies ++= Seq(
    "com.twitter" %% "finatra-thrift" % versions.finatra excludeAll (
      ExclusionRule(organization = "com.fasterxml.jackson.module")
    ),
    "com.twitter" %% "finatra-thrift" % versions.finatra % "test" classifier "tests" excludeAll (
      ExclusionRule(organization = "com.fasterxml.jackson.module")
    ),
    "ch.qos.logback"               % "logback-classic"       % versions.logback,
    "com.twitter"                  %% "finatra-thrift"       % versions.finatra % "test",
    "com.twitter"                  %% "inject-app"           % versions.finatra % "test",
    "com.twitter"                  %% "inject-core"          % versions.finatra % "test",
    "com.twitter"                  %% "inject-modules"       % versions.finatra % "test",
    "com.twitter"                  %% "inject-server"        % versions.finatra % "test",
    "com.google.inject.extensions" % "guice-testlib"         % versions.guice % "test",
    "com.twitter"                  %% "finatra-thrift"       % versions.finatra % "test" classifier "tests",
    "com.twitter"                  %% "inject-app"           % versions.finatra % "test" classifier "tests",
    "com.twitter"                  %% "inject-core"          % versions.finatra % "test" classifier "tests",
    "com.twitter"                  %% "inject-modules"       % versions.finatra % "test" classifier "tests",
    "com.twitter"                  %% "inject-server"        % versions.finatra % "test" classifier "tests",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % versions.jackson,
    "org.scalatest"                %% "scalatest"            % versions.scalatest % "test"
  ),
  resolvers += Resolver.sonatypeRepo("releases"),
  releaseProcess := aggregateReleaseProcess
)

lazy val assemblySettings = {
  Seq(
    assemblyMergeStrategy in assembly := {
      case PathList("io", "netty", xs @ _*) =>
        MergeStrategy.first
      case PathList("META-INF", "MANIFEST.MF") =>
        MergeStrategy.discard
      case PathList("META-INF", "io.netty.versions.properties") =>
        MergeStrategy.first
      case PathList("META-INF", "services", _) =>
        MergeStrategy.concat
      case "BUILD" =>
        MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },
    test in assembly := {}
  )
}

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo in ThisBuild := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  sonatypeProfileName := "com.github.3tty0n",
  licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  homepage := Some(url("https://github.com/3tty0n")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/3tty0n/gatling-thrift"),
      "scm:git@github.com:3tty0n/gatling-thrift.git"
    )
  ),
  developers := List(
    Developer(id = "3tty0n", name = "Yusuke Izawa", email = "yuizalp@gmail.com", url = url("https://github.com/3tty0n"))
  )
)

lazy val aggregateReleaseProcess = Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("gatling-thrift/publishSigned"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

lazy val root = (project in file("."))
  .enablePlugins(
    DockerPlugin,
    ParadoxPlugin,
    ParadoxSitePlugin,
    GhpagesPlugin
  )
  .settings(baseSettings, publishSettings)
  .settings(
    name := "gatling-thrift",
    publishLocal in Docker := Def.sequential(
      publishLocal in Docker in `gatling-thrift-example`
    ),
    test in Test := Def.sequential(
      test in Test in `gatling-thrift-example`,
      test in Test in `gatling-thrift`
    ),
    paradoxTheme := Some(builtinParadoxTheme("generic")),
    sourceDirectory in Paradox := sourceDirectory.value / "main" / "paradox",
    git.remoteRepo := "git@github.com:3tty0n/gatling-thrift.git"
  )
  .aggregate(
    `gatling-thrift`,
    `gatling-thrift-example`
  )

lazy val `gatling-thrift` = (project in file("gatling-thrift"))
  .settings(baseSettings, publishSettings)
  .settings(
    name := "gatling-thrift",
    libraryDependencies ++= Seq(
      "io.gatling"            % "gatling-app"               % versions.gatling,
      "io.gatling"            % "gatling-test-framework"    % versions.gatling,
      "io.gatling.highcharts" % "gatling-charts-highcharts" % versions.gatling
    )
  )

lazy val `gatling-thrift-example` = (project in file("gatling-thrift-example"))
  .enablePlugins(
    GatlingPlugin,
    JavaAppPackaging,
    UniversalDeployPlugin,
    DockerPlugin
  )
  .settings(baseSettings, assemblySettings)
  .settings(
    name := "gatling-thrift-example",
    assemblyJarName in assembly := "gatling-thrift-example.jar",
    mainClass in assembly := Some("simulation.ThriftSimulationMain"),
    mainClass in (Compile, run) := Some("server.ExampleServerMain"),
    dockerBaseImage in Docker := "dockerfile/java",
    packageName in Docker := "micchon/gatling-thrift-example",
    mappings in Universal := {
      val universalMappings = (mappings in Universal).value
      val fatJar            = (assembly in Compile).value
      val filtered = universalMappings.filter {
        case (file, name) => !name.endsWith(".jar")
      }
      filtered :+ (fatJar -> ("lib/" + fatJar.getName))
    },
    scriptClasspath := Seq((assemblyJarName in assembly).value),
    publish := {}
  )
  .dependsOn(`gatling-thrift`)
