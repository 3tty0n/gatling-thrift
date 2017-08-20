package io.gatling.thrift

import akka.actor.{ActorRef, Props}
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.Protocols
import io.gatling.thrift.Predef._
import io.gatling.thrift.action.ThriftActionBuilder
import io.gatling.thrift.client.ThriftClientBuilder
import org.micchon.ping.thriftscala.PingService

import scala.concurrent.duration._

class ThriftSimulation extends Thrift[PingService.FutureIface] {

  val client = ThriftClientBuilder(Address("localhost"), Port("9911")).build

  val thriftAction = new ActionBuilder {
    override def build(next: ActorRef, protocols: Protocols): ActorRef = {
      system.actorOf(
        Props(
          ThriftActionBuilder.call(next) {
            client.echo(s"hoge${scala.util.Random.nextInt()}")
          }
        )
      )
    }
  }

  val scn = scenario("Thrift protocol test")
    .repeat(2) { exec(thriftAction) }

  setUp(
    scn.inject(
      atOnceUsers(250)
    )
  ).assertions(
    global.responseTime.max.lessThan(1000),
    global.successfulRequests.percent.greaterThan(95)
  )
}
