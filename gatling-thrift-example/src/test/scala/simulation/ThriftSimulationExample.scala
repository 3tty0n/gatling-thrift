package simulation

import com.twitter.finagle.Thrift
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.thrift.Predef.ThriftSimulation
import io.gatling.thrift.action.ThriftActionBuilder
import org.micchon.ping.thriftscala.PingService

import scala.util.Random
import scala.concurrent.duration._

class ThriftSimulationExample extends ThriftSimulation[PingService.FutureIface] {
  val address = "localhost"

  val port = 9911

  override val client: PingService.FutureIface =
    Thrift.client.newIface[PingService.FutureIface]("localhost:9911")

  override val thriftAction: ActionBuilder =
    ThriftActionBuilder(
      "localhost",
      9911,
      "Thrift Action",
      client.echo(new Random().nextInt().toString)
    )

  override val scn: ScenarioBuilder =
    scenario("Thrift Scenario").repeat(2)(exec(thriftAction))

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