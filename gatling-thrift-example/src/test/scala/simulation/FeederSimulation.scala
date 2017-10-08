package simulation

import com.twitter.finagle.Thrift
import com.twitter.util.Future
import io.gatling.core.Predef._
import io.gatling.core.session.Session
import io.gatling.thrift.Predef._
import io.gatling.thrift.protocol.ThriftProtocol
import org.micchon.ping.thriftscala.PingService

import scala.concurrent.duration._

class FeederSimulation extends ThriftSimulation {
  val client: PingService.FutureIface =
    Thrift.client.newIface[PingService.FutureIface]("localhost:9911")

  implicit val thriftProtocol: ThriftProtocol =
    thrift.port(9911).host("localhost").requestName("feeder request")

  def callbackFeeder: Session => Future[String] = { session =>
    client.echo(session("sym").as[String])
  }

  val orderFeed = csv("src/test/resources/orders.csv").random

  val scn = scenario("Feeder Scenario").repeat(2) {
    feed(orderFeed)
      .exec(callbackFeeder.action)
  }

  setUp(scn.inject(nothingFor(4 seconds), atOnceUsers(100)))
}
