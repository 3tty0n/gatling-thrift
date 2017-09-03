package io.gatling.thrift.data

case class Connection(host: String = "localhost",
                      port: Int = 9911,
                      requestName: String = "Thrift Action") {

  def validate(): Unit = {
    require(host.nonEmpty, "Host name for Thrift Server is not set.")
    require(port > 0, s"Port for Thrift Server is invalid: $port")
    require(requestName.nonEmpty, "Request name is not set")
  }
}
