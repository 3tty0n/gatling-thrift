package simulation

import com.twitter.finagle.Thrift
import com.twitter.util.Future
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.core.session.Session
import io.gatling.thrift.Predef._
import io.gatling.thrift.protocol.ThriftProtocol
import org.micchon.ping.thriftscala.PingService

import scala.util.Random
import scala.concurrent.duration._

class SessionSimulation extends ThriftSimulation {

  val client: PingService.FutureIface =
    Thrift.client.newIface[PingService.FutureIface]("localhost:9911")

  implicit val thriftProtocol: ThriftProtocol =
    thrift.port(9911).host("localhost").requestName("feeder request")

  def callbackSession: Session => Future[String] = { session =>
    client.echo(session("randNum").as[Int].toString)
  }

  val scn: ScenarioBuilder = scenario("Thrift Scenario").repeat(2) {
    exec { session =>
      session.set("randNum", new Random().nextInt())
    }.exec {
      callbackSession.action
    }
  }

  setUp(
    scn.inject(nothingFor(4 seconds), atOnceUsers(100))
  ).protocols(thriftProtocol)
}
