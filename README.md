# gatling-thrift-mono

This is a load testing tool for thrift server.

The version of gatling in this project is __2.1.7__ .

## How to do load test

First, start the finatra server.

```bash
$ sbt run
```

Then, execute the following command in another session.

```bash
$ sbt loadtest/gatling:test
```

## How to construct the scenario of the load testing

- [Inject](http://gatling.io/docs/current/general/simulation_setup/)
  - [Assersions](http://gatling.io/docs/current/general/assertions/#assertions)
- [Scenario](http://gatling.io/docs/current/general/scenario/)
