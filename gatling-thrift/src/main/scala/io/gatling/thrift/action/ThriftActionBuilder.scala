package io.gatling.thrift.action

import com.twitter.util.Future
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Session
import io.gatling.core.structure.ScenarioContext
import io.gatling.thrift.protocol.ThriftProtocol

class ThriftActionBuilder[A](callback: Session => Future[A])(implicit thriftProtocol: ThriftProtocol)
    extends ActionBuilder {

  override def build(ctx: ScenarioContext, next: Action): Action = {
    import ctx._

    val statsEngine = coreComponents.statsEngine
    new ThriftAction[A](statsEngine, next, thriftProtocol.connection.requestName, callback)
  }
}

object ThriftActionBuilder {
  def apply[A](
      callback: Session => Future[A]
  )(implicit thriftProtocol: ThriftProtocol): ThriftActionBuilder[A] =
    new ThriftActionBuilder[A](callback)
}
