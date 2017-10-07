package io.gatling.thrift

import com.twitter.util.Future
import io.gatling.core.session.Session
import io.gatling.thrift.action.ThriftActionBuilder
import io.gatling.thrift.protocol.{ThriftProtocol, ThriftProtocolBuilder}

trait ThriftDsl {

  implicit val thriftProtocol: ThriftProtocol

  def thrift: ThriftProtocolBuilder = ThriftProtocolBuilder()

  implicit def thriftProtocolBuilderToThriftProtocol(
      builder: ThriftProtocolBuilder
  ): ThriftProtocol = builder.build()

  implicit class CallBack2ActionBuilder[T](val f: Session => Future[T]) {
    def action: ThriftActionBuilder[T] = ThriftActionBuilder(f)
  }

}
