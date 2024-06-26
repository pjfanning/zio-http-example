package example

import com.github.pjfanning.zio.micrometer.safe.{Counter, Registry, Timer}
import io.micrometer.prometheusmetrics.{PrometheusConfig, PrometheusMeterRegistry}
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

  // Create HTTP routes
  val textApp = Routes(
    Method.GET / "text" -> handler { (_: Request) =>
      ZIO.succeed(Response.text("Hello World!")).zipPar(
        recordCount("get", "text").provideLayer(metricEnv))
    }
  )

  val jsonApp = Routes(
    Method.GET / "json" -> handler { (_: Request) =>
      ZIO.succeed(Response.json("""{"greetings": "Hello World!"}""")).zipPar(
        recordCount("get", "json").provideLayer(metricEnv))
    }
  )

  val timedApp = Routes(
    Method.GET / "timed" -> handler { (_: Request) =>
      val zio = for {
        t <- Timer.labelled("http_timed", Some("HTTP timed"), Seq("method", "path"))
        timer <- t(Seq("get", "timed")).startTimerSample()
        _ <- ZIO.sleep(Duration(Random.nextInt(500), TimeUnit.MILLISECONDS))
        _ <- timer.stop()
      } yield Response.text("Hello World!")
      zio.provideLayer(metricEnv)
    }
  )

  val metricsApp = Routes(
    Method.GET / "metrics" -> handler { (_: Request) =>
      Response.text(registry.scrape())
    }
  )

  val app = textApp ++ jsonApp ++ timedApp ++ metricsApp

  // Run it like any simple app
  override val run =
    Server.serve(app).provide(Server.defaultWithPort(8090))
}
