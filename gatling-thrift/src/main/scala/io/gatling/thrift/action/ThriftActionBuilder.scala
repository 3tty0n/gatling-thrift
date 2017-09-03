package io.gatling.thrift.action

import com.twitter.util.Future
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext
import io.gatling.thrift.data.Connection

class ThriftActionBuilder[A](
  callback: => Future[A]
)(implicit connection: Connection)
    extends ActionBuilder {

  override def build(ctx: ScenarioContext, next: Action): Action = {
    import ctx._

    val statsEngine = coreComponents.statsEngine
    new ThriftAction[A](statsEngine, next, connection.requestName, callback)
  }
}

object ThriftActionBuilder {
  def apply[A](
    callback: => Future[A]
  )(implicit connection: Connection): ThriftActionBuilder[A] =
    new ThriftActionBuilder[A](callback)
}
