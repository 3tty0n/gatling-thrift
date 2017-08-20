package io.gatling.thrift.testrunner

import io.gatling.app.{CliArgumentParser, Gatling}
import io.gatling.core.Predef.Simulation
import io.gatling.core.config.GatlingConfiguration

object GatlingRunner {
  def main(args: Array[String]): Unit = {
    val simulationClass = getClass
      .getClassLoader
      .loadClass(GatlingConfiguration.load(CliArgumentParser.parseCliArguments(args)).core.simulationClass.get)
      .asInstanceOf[Class[Simulation]]

    Gatling.fromArgs(args, Some(simulationClass))
  }
}

