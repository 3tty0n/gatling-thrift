# Usage

Create your simulation class in `test` directory, and extends `ThriftSimulation`:

```scala
import io.gatling.thrift.Predef._

class YourSimulation extends ThriftSimulation {
  ...
}
```

Define `client` and `thriftProtocol`:

``` scala
val client = Thrift.client.newIface[PingService.MethodPerEndpoint]("localhost:9911")

implicit val thriftProtocol: ThriftProtocol = thrift
  .port(9911)
  .host("localhost")
  .requestName("example request")
```

## Simple

```scala
def callback: Future[String] = {
  client.echo(new Random().nextInt().toString)
}

val scn = senario("Thrift Scenario").repeqt(100) {
  exec(callback.action)
}

setUp(scn.inject(nothingFor(4 seconds), atOnceUsers(100)))
```

@@@ warning
The expression like `exec(session => client.echo(session("i").as[Int]))` is not supported. Please define your callback out side `exec`.
@@@

## Session

```scala
def callback: Session => Future[String] = { session =>
  clinet.echo(session("randNum").as[Int].toString)
}

val scn = senario("Session Scenario").repeqt(100) {
  exec { session =>
    session.set("randNum", new Random().nextInt)
  }.exec(callback.action)
}

setUp(scn.inject(nothingFor(4 seconds), atOnceUsers(100)))
```

@@@ note
For more information about session, see this [documentation](https://gatling.io/docs/2.3/session/session_api/)
@@@

## Feeder

```scala
/**
 * Note: Please set `orders.csv` at resources directory.
 *
 * accountId, sym, qty, price
 * 1L, 7203.T, 10, 6700
 * 2L, 7100.T, 5, 322
 * 3L, 8100.T, 40, 788
 */
val orderFeed = csv("orders.csv").random

def callback: Session => Future[String] = { session =>
  clinet.echo(session("sym").as[String])
}

val scn = senario("Feeder Scenario").repeqt(100) {
  feed(orderFeed)
    .exec(callback.action)
}

setUp(scn.inject(nothingFor(4 seconds), atOnceUsers(100)))
```

@@@ note
For more information about feeder, see this [documentation](https://gatling.io/docs/2.3/session/feeder/).
@@@
