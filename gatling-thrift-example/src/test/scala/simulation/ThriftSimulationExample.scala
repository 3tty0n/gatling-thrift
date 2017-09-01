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

class ThriftSimulationExample
    extends ThriftSimulation[PingService.FutureIface] {
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

  setUp(scn.inject(nothingFor(4 seconds), atOnceUsers(100)))
}
