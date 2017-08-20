package org.example

import org.micchon.ping.thriftscala.PingService
import org.micchon.ping.thriftscala.PingService.{Echo, Ping}
import com.twitter.finatra.thrift.Controller
import com.twitter.util.Future
import javax.inject.Singleton

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
