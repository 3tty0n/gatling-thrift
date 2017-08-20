package io.gatling.thrift.client

import io.gatling.thrift.Predef._
import org.micchon.ping.thriftscala.PingService
import com.twitter.finagle.Thrift

class ThriftClientBuilder(address: Address, port: Port) {

  def build(): PingService.FutureIface =
    Thrift.client
      .newIface[PingService.FutureIface](s"${address.value}:${port.value}")

}

object ThriftClientBuilder {
  def apply(address: Address, port: Port): ThriftClientBuilder =
    new ThriftClientBuilder(address, port)
}
