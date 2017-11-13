package server

import com.twitter.finatra.thrift.ThriftServer
import com.twitter.finatra.thrift.routing.ThriftRouter
import com.twitter.finatra.thrift.filters._
import org.micchon.ping.thriftscala.PingService
import org.micchon.ping.thriftscala.PingService.{ Echo, Ping }
import com.twitter.finatra.thrift.Controller
import com.twitter.util.Future
import javax.inject.Singleton

object ExampleServerMain extends ExampleServer

class ExampleServer extends ThriftServer {
  override val name                             = "example-server"
  override val defaultFinatraThriftPort: String = ":9911"
  override def defaultHttpPort: Int             = 9912

  override def configureThrift(router: ThriftRouter) {
    router
      .filter[LoggingMDCFilter]
      .filter[TraceIdMDCFilter]
      .filter[ThriftMDCFilter]
      .filter[AccessLoggingFilter]
      .filter[StatsFilter]
      .add[PingController]
  }
}

@Singleton
class PingController extends Controller with PingService.BaseServiceIface {

  override val ping = handle(Ping) { args: Ping.Args =>
    info(s"Responding to ping thrift call")
    Future.value("pong")
  }

  override val echo = handle(Echo) { args: Echo.Args =>
    Future.value(args.a)
  }

}
