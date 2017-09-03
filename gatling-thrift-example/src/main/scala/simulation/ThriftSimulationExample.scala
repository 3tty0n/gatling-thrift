package simulation

import com.twitter.finagle.Thrift
import com.twitter.util.Future
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.thrift.Predef._
import io.gatling.thrift.protocol.ThriftProtocol
import io.gatling.thrift.testrunner.GatlingRunner
import org.micchon.ping.thriftscala.PingService

import scala.concurrent.duration._
import scala.util.Random

object ThriftSimulationMain extends GatlingRunner

class ThriftSimulationExample extends ThriftSimulation {
  val client: PingService.FutureIface =
    Thrift.client.newIface[PingService.FutureIface]("localhost:9911")

  implicit val callback: Future[String] =
    client.echo(new Random().nextInt().toString)

  implicit val thriftProtocol: ThriftProtocol =
    thrift.port(9911).host("localhost").requestName("example request")

  val scn: ScenarioBuilder = scenario("Thrift Scenario").repeat(2) {
    exec(callback)
  }

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
  )

}
