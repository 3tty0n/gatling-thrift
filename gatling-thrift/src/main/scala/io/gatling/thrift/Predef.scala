package io.gatling.thrift

import io.gatling.core.Predef.Simulation
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.thrift.protocol.ThriftProtocol

object Predef {

  def thrift(address: String, port: Int) = new ThriftProtocol(address, port)

  trait ThriftSimulation[A] extends Simulation {
    val client: A
    val thriftAction: ActionBuilder
    val scn: ScenarioBuilder
  }
}
