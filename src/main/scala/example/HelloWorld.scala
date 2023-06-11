package example

import com.github.pjfanning.zio.micrometer.Counter
import com.github.pjfanning.zio.micrometer.safe.{Counter, Registry, Timer}
import io.micrometer.prometheus.{PrometheusConfig, PrometheusMeterRegistry}
import zio.http.*
import zio.{Duration, ZIO, ZIOAppDefault}

import java.util.concurrent.TimeUnit
import scala.util.Random

object HelloWorld extends ZIOAppDefault {

  private val registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
  private val metricEnv = Registry.makeWith(registry)

  private def recordCount(method: String, path: String) = {
    for {
      c <- Counter.labelled("http", Some("HTTP counts"), Seq("method", "path"))
      result <- c(Seq(method, path)).inc()
    } yield result
  }

  // Create HTTP route
  val app: HttpApp[Any, Nothing] = Http.collectZIO[Request] {
    case Method.GET -> Root / "text" => {
      ZIO.succeed(Response.text("Hello World!")).zipPar(
        recordCount("get", "text").provideLayer(metricEnv))
    }
    case Method.GET -> Root / "json" => {
      ZIO.succeed(Response.json("""{"greetings": "Hello World!"}""")).zipPar(
        recordCount("get", "json").provideLayer(metricEnv))
    }
    case Method.GET -> Root / "timed" => {
      val zio = for {
        t <- Timer.labelled("http_timed", Some("HTTP timed"), Seq("method", "path"))
        timer <- t(Seq("get", "timed")).startTimerSample()
        _ <- ZIO.sleep(Duration(Random.nextInt(500), TimeUnit.MILLISECONDS))
        _ <- timer.stop()
      } yield Response.text("Hello World!")
      zio.provideLayer(metricEnv)
    }
    case Method.GET -> Root / "metrics" => {
      ZIO.succeed(Response.text(registry.scrape()))
    }
  }

  // Run it like any simple app
  override val run =
    Server.serve(app).provide(Server.defaultWithPort(8090))
}
