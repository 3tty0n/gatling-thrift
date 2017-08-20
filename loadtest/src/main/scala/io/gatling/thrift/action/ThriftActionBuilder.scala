package io.gatling.thrift.action

import akka.actor.ActorRef
import com.twitter.util.{Future, Return, Throw}
import io.gatling.core.Predef.{Session, Status}
import io.gatling.core.action.Chainable
import io.gatling.core.result.message.{KO, OK}
import io.gatling.core.result.writer.{DataWriter, RequestMessage}

class ThriftActionBuilder[A](val next: ActorRef, callBack: => Future[A])
    extends Chainable {

  override def execute(session: Session): Unit = {
    val start = System.currentTimeMillis()
    callBack.respond {
      case Return(v) =>
        val end = System.currentTimeMillis()
        logger.info(v.toString)
        requestMessageDispatcher(session, "Thrift scenario", start, start, end, end, OK)
        next ! session
      case Throw(e) =>
        val end = System.currentTimeMillis()
        logger.error(e.getMessage)
        requestMessageDispatcher(session, "Thrift scenario", start, start, end, end, KO)
        next ! session
    }
  }

  private def requestMessageDispatcher(session: Session,
                                       name: String,
                                       requestStartDate: Long,
                                       requestEndDate: Long,
                                       responseStartDate: Long,
                                       responseEndDate: Long,
                                       status: Status,
                                       message: Option[String] = None,
                                       extraInfo: List[Any] = Nil): Unit = {
    DataWriter.dispatch(
      RequestMessage(
        scenario = session.scenarioName,
        userId = session.userId,
        groupHierarchy = session.groupHierarchy,
        name = "Thrift scenario",
        requestStartDate = requestStartDate,
        requestEndDate = requestEndDate,
        responseStartDate = responseStartDate,
        responseEndDate = responseEndDate,
        status = status,
        message = message,
        extraInfo = Nil
      )
    )
  }
}

object ThriftActionBuilder {
  def apply[A](next: ActorRef, callBack: => Future[A]): ThriftActionBuilder[A] =
    new ThriftActionBuilder[A](next, callBack)

  def call[A](next: ActorRef)(callBack: => Future[A]): ThriftActionBuilder[A] =
    new ThriftActionBuilder[A](next, callBack)
}
