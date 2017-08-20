package com.folio

import com.google.inject.Stage
import com.twitter.finatra.thrift.EmbeddedThriftServer
import com.twitter.inject.server.FeatureTest

class ExampleServerStartupTest extends FeatureTest {

  val server = new EmbeddedThriftServer(
    twitterServer = new ExampleServer,
    stage = Stage.PRODUCTION
  )

  test("server#startup") {
    server.assertHealthy()
  }
}
