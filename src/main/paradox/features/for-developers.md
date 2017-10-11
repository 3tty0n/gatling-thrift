# For developers

## Set up

Fork the [repo](https://github.com/3tty0n/gatling-thrift) and clone it.

## Start the server

```bash
$ sbt gatling-thrift-example/docker:publishLocal
$ export VERSION=$(cat version.sbt | sed -e "s/[^0-9.]//g")
$ docker run -it -p 127.0.0.1:9911:9911 --rm -d micchon/gatling-thrift-example:$VERSION bin/gatling-thrift-example
```

## Execute the test

```bash
$ sbt gatling-thrift-example/gatling:test
```
