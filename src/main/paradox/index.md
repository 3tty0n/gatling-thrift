# gatling-thrift

[![Build Status](https://travis-ci.org/3tty0n/gatling-thrift.svg?branch=master)](https://travis-ci.org/3tty0n/gatling-thrift)
 [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.3tty0n/gatling-thrift_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.3tty0n/gatling-thrift_2.12)

gatling-thrift is a [Gatling](http://gatling.io/) third party plugin for thrift protocol.

If you want more information for Gatling, see this [documentation](https://gatling.io/documentation/).

## Set up

Builds are available for for Scala 2.12.x. The main line of development of gatling-thrift is 2.12.4.

First, you add `gatling-thrift` and `gatling` to `librarydependencies` in `build.sbt`:

```scala
librarydependencies ++= Seq(
  "com.github.3tty0n" %% "gatling-thrift" % "0.6.0",
  "io.gatling" % "gatling-test-framework" % "2.3.0" % "test,it",
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.3.0" % "test,it"
)
```

And add these plugins to `project/plugins.sbt`:

```scala
addSbtPlugin("com.twitter" % "scrooge-sbt-plugin" % "17.11.0")
addSbtPlugin("io.gatling" % "gatling-sbt" % "2.2.2")
```

Then, enable `Gatlingplugin` in `build.sbt`:

```scala
enablePlugin(GatlingPlugin)
```

@@@ warning

This version of gatling-thrift is built on `17.11.0` of Finagle family.

If you want to use version `17.10.0` of Finagle family, use version `0.5.0` of gatling-thrift.

@@@

@@@ index

- [Usage](features/usage.md)
- [Execute as sbt](features/execute-as-sbt.md)
- [Execute as jar](features/execute-as-jar.md)
- [Publish](features/publish.md)
- [For developers](features/for-developers.md)

@@@

[repo]: https://github.com/3tty0n/gatling-thrift
