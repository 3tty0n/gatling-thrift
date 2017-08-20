package org.micchon

import com.twitter.finatra.thrift.ThriftServer
import com.twitter.finatra.thrift.routing.ThriftRouter
import com.twitter.finatra.thrift.filters._

object ExampleServerMain extends ExampleServer

class ExampleServer extends ThriftServer {
  override val name = "example-server"
  override val defaultFinatraThriftPort: String = ":9911"
  override def defaultHttpPort: Int = 9912

  override def configureThrift(router: ThriftRouter) {
    router
      .filter[LoggingMDCFilter]
      .filter[TraceIdMDCFilter]
      .filter[ThriftMDCFilter]
      .filter[AccessLoggingFilter]
      .filter[StatsFilter]
      .filter[ExceptionTranslationFilter]
      .add[PingController]
  }
}
