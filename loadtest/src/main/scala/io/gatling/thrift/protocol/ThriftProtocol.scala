package io.gatling.thrift.protocol

import io.gatling.core.config.Protocol
import io.gatling.thrift.Predef._

case class ThriftProtocol(address: Address, port: Port) extends Protocol
