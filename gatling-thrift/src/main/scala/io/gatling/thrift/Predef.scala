package io.gatling.thrift

import io.gatling.core.Predef.Simulation
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.thrift.protocol.ThriftProtocol

object Predef {
  case class Address(value: String = "localhost")

  case class Port(value: Int = 9911)

  def thrift(address: Address, port: Port) = new ThriftProtocol(address, port)

  trait ThriftSimulation[A] extends Simulation {
    val client: A
    val thriftAction: ActionBuilder
    val scn: ScenarioBuilder
  }
}
