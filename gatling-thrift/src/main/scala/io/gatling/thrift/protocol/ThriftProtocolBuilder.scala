package io.gatling.thrift.protocol

import io.gatling.thrift.data._

case class ThriftProtocolBuilder(connection: Connection = Connection()) {
  def host(h: String): ThriftProtocolBuilder =
    copy(connection = connection.copy(host = h))
  def port(p: Int): ThriftProtocolBuilder =
    copy(connection = connection.copy(port = p))
  def requestName(r: String): ThriftProtocolBuilder =
    copy(connection = connection.copy(requestName = r))

  def build(): ThriftProtocol = {
    connection.validate()
    ThriftProtocol(connection)
  }
}

object ThriftProtocolBuilder {
  implicit def toThriftProtocolBuilder(
    builder: ThriftProtocolBuilder
  ): ThriftProtocol = builder.build()
}
