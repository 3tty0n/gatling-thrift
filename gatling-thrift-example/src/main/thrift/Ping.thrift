namespace java org.micchon.ping.thriftjava
#@namespace scala org.micchon.ping.thriftscala
namespace rb Ping

service PingService {

  /**
   * Respond with 'pong'
   */
  string ping()

  string echo(
    1: string a
  )
}