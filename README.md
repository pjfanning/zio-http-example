# zio-http-example
Example of how to use zio-http with scala 3.

* Checkout this repo to your machine.
* There is a simpler version of this example in the `without-metrics` branch
* The `main` branch includes examples of how to use [zio-metrics-micrometer](https://github.com/pjfanning/zio-metrics-micrometer)

```
sbt run
```

```
curl -v http://localhost:8090/json
curl -v http://localhost:8090/text
```

After a few calls to these endpoints, you can get count metrics in [Prometheus](https://prometheus.io/) format by calling:

```
curl -v http://localhost:8090/metrics
```
