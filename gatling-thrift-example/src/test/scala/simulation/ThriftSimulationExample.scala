package simulation

import com.twitter.finagle.Thrift
import com.twitter.util.Future
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.thrift.Predef._
import io.gatling.thrift.protocol.ThriftProtocol
import org.micchon.ping.thriftscala.PingService

import scala.util.Random
import scala.concurrent.duration._

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

  setUp(scn.inject(nothingFor(4 seconds), atOnceUsers(100)))
}
