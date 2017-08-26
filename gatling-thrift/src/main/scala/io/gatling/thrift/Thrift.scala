package io.gatling.thrift

import io.gatling.core.Predef.Simulation
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioBuilder

trait Thrift[A] extends Simulation {
  val client: A
  val thriftAction: ActionBuilder
  val scn: ScenarioBuilder
}
