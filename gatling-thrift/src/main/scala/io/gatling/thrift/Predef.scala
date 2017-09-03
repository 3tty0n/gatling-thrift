package io.gatling.thrift

import io.gatling.core.Predef.Simulation

object Predef {
  trait ThriftSimulation extends Simulation with ThriftDsl
}
