package io.gatling.thrift.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

case class ThriftComponents(thriftProtocol: ThriftProtocol) extends ProtocolComponents {
  override def onStart: Option[(Session) => Session] = None

  override def onExit: Option[(Session) => Unit] = None
}
