package io.gatling.thrift.action

import com.twitter.util.{Future, Return, Throw}
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.action.{Action, ExitableAction}
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.util.NameGen

class ThriftAction[A](val statsEngine: StatsEngine,
                      val next: Action,
                      requestName: String,
                      callback: => Future[A])
    extends ExitableAction
    with NameGen {
  override def name: String = genName("thriftConnect")

  override def execute(session: Session): Unit = {
    val start = System.currentTimeMillis()
    callback.respond {
      case Return(v) =>
        val end     = System.currentTimeMillis()
        val timings = ResponseTimings(start, end)
        logger.info(s"result: $v")
        statsEngine.logResponse(session, requestName, timings, OK, None, None)
        next ! session
      case Throw(e) =>
        val end     = System.currentTimeMillis()
        val timings = ResponseTimings(start, end)
        logger.error(s"An error is occurred: ${e.getMessage}", e)
        statsEngine.logResponse(session, requestName, timings, KO, None, Some(e.getMessage))
        next ! session
    }

  }
}
