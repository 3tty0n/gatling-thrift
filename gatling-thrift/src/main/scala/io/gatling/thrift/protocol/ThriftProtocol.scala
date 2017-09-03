package io.gatling.thrift.protocol

import akka.actor.ActorSystem
import io.gatling.core.{CoreComponents, protocol}
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.ProtocolKey
import io.gatling.thrift.data.Connection

class ThriftProtocol(connection: Connection) {

  val ThriftProtocolKey = new ProtocolKey {
    override type Protocol = ThriftProtocol
    override type Components = ThriftComponents

    override def protocolClass: Class[protocol.Protocol] =
      classOf[ThriftProtocol]
        .asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    override def defaultProtocolValue(
      configuration: GatlingConfiguration
    ): Protocol = ThriftProtocol(connection)

    override def newComponents(
      system: ActorSystem,
      coreComponents: CoreComponents
    ): ThriftProtocol => ThriftComponents = { thriftProtocol =>
      ThriftComponents(thriftProtocol)
    }
  }
}

object ThriftProtocol {

  def apply(connection: Connection): ThriftProtocol =
    new ThriftProtocol(connection)
}
