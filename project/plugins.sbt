resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  "Twitter Maven" at "https://maven.twttr.com"
)

addSbtPlugin("com.twitter" % "scrooge-sbt-plugin" % "17.11.0")
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.14")
addSbtPlugin("io.gatling" % "gatling-sbt" % "2.2.2")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.1")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.6")

// for publish
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

// for documentation
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.3.1")
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.3.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.2")
