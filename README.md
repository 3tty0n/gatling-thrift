# gatling-thrift-testasjar

This is a load testing tool for thrift server.

## How to do load test

First, compile and start the finatra server.

```bash
$ sbt compile run
```

Then, execute the following command in another session.

### In your command line

1. Create the jar

  ```bash
  $ sbt loadtest/assembly
  ```

2. Exectute

  ``` bash
  $ java -jar loadtest/target/scala-2.11/gatling-loadtest.jar -s io.gatling.thrift.testrunner.ThriftSimulation
  ```


## How to construct the scenario of the load testing

- [Inject](http://gatling.io/docs/current/general/simulation_setup/)
  - [Assersions](http://gatling.io/docs/current/general/assertions/#assertions)
- [Scenario](http://gatling.io/docs/current/general/scenario/)
