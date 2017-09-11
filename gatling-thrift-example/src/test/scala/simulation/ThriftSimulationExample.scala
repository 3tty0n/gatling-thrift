package simulation

import java.util.concurrent.atomic.AtomicBoolean

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

  private val isDone = new AtomicBoolean(false)

  // https://github.com/3tty0n/gatling-thrift/issues/9
  // This function arises OK once and makes rest of them KO
  def callback: Future[String] = {
    if (isDone.compareAndSet(false, true)) {
      Future.value("first")
    } else {
      Future.exception(new RuntimeException("already done"))
    }
  }

  def callbackSimple: Future[String] =
    client.echo(new Random().nextInt().toString)

  implicit val thriftProtocol: ThriftProtocol =
    thrift.port(9911).host("localhost").requestName("example request")

  val scn: ScenarioBuilder = scenario("Thrift Scenario")
    .exec(callback)
    .exec(callbackSimple)

  setUp(scn.inject(nothingFor(4 seconds), atOnceUsers(100)))
}
