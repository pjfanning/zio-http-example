package example

import com.github.pjfanning.zio.micrometer.Counter
import com.github.pjfanning.zio.micrometer.safe.{Counter, Registry}
import io.micrometer.prometheus.{PrometheusConfig, PrometheusMeterRegistry}
import zhttp.http.*
import zhttp.service.Server
import zio.{Clock, ZIO, ZIOAppDefault}

object HelloWorld extends ZIOAppDefault {

  private val registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
  private val metricEnv = Clock.live ++ Registry.makeWith(registry)

  private def recordCount(method: String, path: String) = {
    for {
      c <- Counter.labelled("http", Some("HTTP counts"), Seq("method", "path"))
      result <- c(Seq("get", "text")).inc
    } yield result
  }

  // Create HTTP route
  val app: HttpApp[Any, Nothing] = Http.collectZIO[Request] {
    case Method.GET -> !! / "text" => {
      ZIO.succeed(Response.text("Hello World!")).zipPar(
        recordCount("get", "text").provideLayer(metricEnv))
    }
    case Method.GET -> !! / "json" => {
      ZIO.succeed(Response.json("""{"greetings": "Hello World!"}""")).zipPar(
        recordCount("get", "json").provideLayer(metricEnv))
    }
    case Method.GET -> !! / "metrics" => {
      ZIO.succeed(Response.text(registry.scrape()))
    }
  }

  // Run it like any simple app
  override val run =
    Server.start(8090, app)
}
