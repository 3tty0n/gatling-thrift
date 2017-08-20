package io.gatling.thrift.protocol

import akka.actor.ActorSystem
import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolComponents, ProtocolKey}
import io.gatling.core.session.Session
import io.gatling.thrift.Predef._

class ThriftProtocol(address: Address, port: Port) extends Protocol {
  type Components = ThriftComponents
}

object ThriftProtocol {

  def apply(address: Address, port: Port): ThriftProtocol = new ThriftProtocol(address, port)

  val ThriftProtocolKey = new ProtocolKey {
    override type Protocol = ThriftProtocol
    override type Components = ThriftComponents

    override def protocolClass =
      //classOf[ThriftProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]
    classOf[ThriftProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    override def defaultValue(configuration: GatlingConfiguration) =
      new ThriftProtocol(Address(), Port())

    override def newComponents(
      system: ActorSystem,
      coreComponents: CoreComponents
    ): ThriftProtocol => ThriftComponents = {
      thriftProtocol => ThriftComponents(thriftProtocol)
    }
  }
}

case class ThriftComponents(thriftProtocol: ThriftProtocol) extends ProtocolComponents {
  override def onStart: Option[(Session) => Session] = None

  override def onExit: Option[(Session) => Unit] = None
}