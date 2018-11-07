package io.vertx.feed.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.feed.MainVerticle;
import io.vertx.feed.links.LinksService;

public class HttpVerticle extends AbstractVerticle {
  private LinksService linksService;
  private final static Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  public void start(Future<Void> startFuture) throws Exception {
    linksService = LinksService.createProxy(vertx, "userLinks");
    HttpServer httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.get("/links").handler(this::usersLinksHandler);
    httpServer.requestHandler(router::accept).listen(8080, ar -> {
      if (ar.succeeded()) {
        LOGGER.info("HTTP server is running on port 8080");
        startFuture.complete();
      } else {
        LOGGER.error("Could not start HTTP server", ar.cause());
        startFuture.fail(ar.cause());
      }
    });

  }

  private void usersLinksHandler(RoutingContext ctx) {
    String authToken = ctx.request().getHeader("X-NEMO-AUTH");
    if (authToken == null) {
      LOGGER.error("Missing AUTH token");
      ctx.fail(new RuntimeException("Missing auth token"));
    } else {
      LOGGER.info("Fetching users links");
      linksService.getUserLinks(authToken, replay -> {
        if (replay.succeeded()) {
          JsonObject jo = replay.result();
          ctx.response().putHeader("content-type", "application/json").end(jo.toString());
        } else {
          ctx.fail(replay.cause());
        }
      });
    }
  }

}
