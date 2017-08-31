package sbtgatlingthrift

import sbt.{Def, _}
import sbt.Keys._
import sbtassembly.AssemblyKeys._
import sbtassembly.{MergeStrategy, PathList}
import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.packager.Keys.scriptClasspath

object GatlingThriftPlugin extends AutoPlugin {

  object autoImport {
    val gatlingThriftScalaVersion = "2.11.11"
    val gatlingThriftUniversalPackagerSettings =
      settingKey[Seq[Def.Setting[_]]](
        "sbt-native-packager settings for gatling-thrift"
      )
  }

  import autoImport._

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    gatlingThriftUniversalPackagerSettings := Seq(
      assemblyMergeStrategy in assembly := {
        case PathList("io", "netty", xs @ _ *) => MergeStrategy.first
        case meta(_)                           => MergeStrategy.discard
        case "BUILD"                           => MergeStrategy.discard
        case x =>
          val oldStrategy = (assemblyMergeStrategy in assembly).value
          oldStrategy(x)
      },
      test in assembly := {},
      mappings in Universal := {
        val universalMappings = (mappings in Universal).value
        val fatJar = (assembly in Compile).value
        val filtered = universalMappings.filter {
          case (file, name) => !name.endsWith(".jar")
        }
        filtered :+ (fatJar -> ("lib/" + fatJar.getName))
      },
      scriptClasspath := Seq((assemblyJarName in assembly).value)
    )
  )

  private lazy val meta = """META.INF(.)*""".r

}
