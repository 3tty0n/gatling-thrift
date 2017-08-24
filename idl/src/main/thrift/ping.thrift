namespace java org.micchon.ping.thriftjava
#@namespace scala org.micchon.ping.thriftscala
namespace rb Ping

include "finatra-thrift/finatra_thrift_exceptions.thrift"

service PingService {

  /**
   * Respond with 'pong'
   */
  string ping() throws (
    1: finatra_thrift_exceptions.ClientError clientError,
    2: finatra_thrift_exceptions.ServerError serverError
  )

  string echo(
    1: string a
  ) throws (
    1: finatra_thrift_exceptions.ClientError clientError,
    2: finatra_thrift_exceptions.ServerError serverError
  )
}