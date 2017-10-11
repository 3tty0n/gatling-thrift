# Publish

You can publish your simulation as zip by using sbt-native-packager and sbt-assembly.

## Set up

Enable `sbt-native-packager` and `sbt-assembly` plugin

In `project/plugins.sbt`, add:

```scala
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")
```

In `build.sbt`, add:

```scala
enablePlugins(GatlingPlugin, JavaAppPackaging, UniversalDeployPlugin)
```

## Define settings

Add settings as below

```scala
lazy val root = (project in file("."))
  .settings(publishSettings, assemblySettings)
  .settings(...)

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

## Execute

call as below:

```bash
$ sbt publish
# if you want to publish to local repository, execute `sbt publishLocal`
```
