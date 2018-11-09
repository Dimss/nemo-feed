package io.vertx.feed.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.feed.MainVerticle;
import io.vertx.feed.likes.LikesService;
import io.vertx.feed.links.LinksService;

public class HttpVerticle extends AbstractVerticle {
  private LinksService linksService;
  private LikesService likesService;
  private final static Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  public void start(Future<Void> startFuture) throws Exception {
    linksService = LinksService.createProxy(vertx, "userLinks");
    likesService = LikesService.createProxy(vertx, "imageLikes");
    HttpServer httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.get("/feed").handler(this::getFeedHandler);
    router.get("/links").handler(this::usersLinksHandler);
    router.get("/status").handler(this::serviceStatus);
    router.get("/likes/:imageId").handler(this::getImageLikesHandler);
    router.post("/likes/:imageId").handler(this::addImageLikesHandler);
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

  private void getFeedHandler(RoutingContext ctx) {

  }

  private void addImageLikesHandler(RoutingContext ctx) {
    String authToken = ctx.request().getHeader("X-NEMO-AUTH");
    String imageId = ctx.request().getParam("imageId");
    if (authToken == null || imageId == null) {
      LOGGER.error("Missing AUTH token or Image ID");
      LOGGER.error("Auth token: " + authToken);
      LOGGER.error("Image ID: " + imageId);
      ctx.fail(new RuntimeException("Missing auth token or Image id"));
    } else {
      LOGGER.info("Add image likes");
      likesService.addLike(authToken, imageId, replay -> {
        if (replay.succeeded()) {
          JsonObject jo = replay.result();
          ctx.response().putHeader("content-type", "application/json").end(jo.toString());
        } else {
          ctx.fail(replay.cause());
        }
      });
    }
  }

  private void getImageLikesHandler(RoutingContext ctx) {
    String authToken = ctx.request().getHeader("X-NEMO-AUTH");
    String imageId = ctx.request().getParam("imageId");
    if (authToken == null || imageId == null) {
      LOGGER.error("Missing AUTH token or Image ID");
      LOGGER.error("Auth token: " + authToken);
      LOGGER.error("Image ID: " + imageId);
      ctx.fail(new RuntimeException("Missing auth token or Image id"));
    } else {
      LOGGER.info("Fetching image likes");
      likesService.getImageLikes(authToken, imageId, replay -> {
        if (replay.succeeded()) {
          JsonObject jo = replay.result();
          ctx.response().putHeader("content-type", "application/json").end(jo.toString());
        } else {
          ctx.fail(replay.cause());
        }
      });
    }
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
          JsonArray resultArray = replay.result();
          ctx.response().putHeader("content-type", "application/json").end(resultArray.toString());
        } else {
          ctx.fail(replay.cause());
        }
      });
    }
  }

  private void serviceStatus(RoutingContext ctx) {
    ctx.response().end("ok");
  }
}
