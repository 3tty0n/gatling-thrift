package io.gatling.thrift

import io.gatling.thrift.protocol.ThriftProtocol

object Predef {
  case class Address(value: String = "localhost")

  case class Port(value: Int = 9911)

  def thrift(address: Address, port: Port) = new ThriftProtocol(address, port)
}
