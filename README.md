# gatling-thrift

This is a load testing tool for thrift server.

You can execute your load test in command line.

## How to do

### For Example

1. Clone this reposiory

```bash
$ git clone git@github.com:3tty0n/gatling-thrift.git
```

2. Compile and start the finatra server.

```bash
$ sbt compile run
```

3. Create the fat jar

```bash
$ sbt loadtest/assembly
```

4. Exectute the load test

``` bash
$ java -jar loadtest/target/scala-2.11/gatling-loadtest.jar -s io.gatling.thrift.testrunner.ThriftSimulation
```
  
## Customize your simulation

Implement `YourSimulation.scala` at `io.gatling.thrift.testrunner` package in `loadtest` module  like [this](https://github.com/3tty0n/gatling-thrift-testasjar/blob/master/loadtest/src/main/scala/io/gatling/thrift/testrunner/ThriftSimulation.scala).

And execte as below.

``` bash
$ sbt loadtest/assembly
$ java -jar loadtest/target/scala-2.11/gatling-loadtest.jar -s io.gatling.thrift.testrunner.YourSimulation
```

## Publish

In this project, `sbt-native-packager` is enabled. So you can publish the fat jar of `loadtest` module to execute following commands.

``` bash
$ sbt pubslih // in local, sbt publishLocal
```

## How to construct the scenario of the load testing

- [Inject](http://gatling.io/docs/current/general/simulation_setup/)
  - [Assersions](http://gatling.io/docs/current/general/assertions/#assertions)
- [Scenario](http://gatling.io/docs/current/general/scenario/)
