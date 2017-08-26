package io.gatling.thrift.testrunner

import io.gatling.core.Predef._
import io.gatling.thrift.Thrift
import io.gatling.thrift.action.ThriftActionBuilder
import io.gatling.thrift.client.ThriftClientBuilder
import io.gatling.thrift.Predef._
import org.micchon.ping.thriftscala.PingService

import scala.concurrent.duration._
import scala.util.Random

class ThriftSimulation extends Thrift[PingService.FutureIface] {

  val client = ThriftClientBuilder(Address(), Port()).build()

  val thriftAction = ThriftActionBuilder(
    "thrift session",
    client.echo(new Random().nextInt().toString)
  )

  val scn = scenario("Thrift protocol test")
    .repeat(2) { exec(thriftAction) }

  setUp(
    scn.inject(
      nothingFor(4 seconds),
      atOnceUsers(10),
      rampUsers(10) over (5 seconds),
      constantUsersPerSec(20) during (15 seconds),
      constantUsersPerSec(20) during (15 seconds) randomized,
      rampUsersPerSec(10) to 20 during (3 seconds),
      rampUsersPerSec(10) to 20 during (2 seconds) randomized,
      splitUsers(20) into (rampUsers(10) over (10 seconds)) separatedBy (10 seconds),
      splitUsers(20) into (rampUsers(10) over (10 seconds)) separatedBy atOnceUsers(
        30
      ),
      heavisideUsers(50) over (20 seconds)
    )
  ).assertions(
    global.responseTime.max.lessThan(1000),
    global.successfulRequests.percent.greaterThan(95)
  )
}
