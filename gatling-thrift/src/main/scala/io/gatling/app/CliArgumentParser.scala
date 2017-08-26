package io.gatling.app

import io.gatling.app.cli.ArgsParser
import scala.collection.mutable

object CliArgumentParser {

  def parseCliArguments(args: Array[String]): ConfigOverrides = {
    val argsParser = new ArgsParser(args)

    argsParser.parseArguments match {
      case Left(commandLineOverrides) => commandLineOverrides
      case Right(statusCode)          => mutable.Map[String, Any]()
    }
  }
}
