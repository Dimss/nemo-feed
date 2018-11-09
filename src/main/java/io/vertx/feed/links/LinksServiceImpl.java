package io.vertx.feed.links;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.Logger;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.feed.MainVerticle;
import io.vertx.feed.likes.LikesService;

import java.util.ArrayList;


public class LinksServiceImpl implements LinksService {
  private final static Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
  private WebClient webClient;
  private LikesService likesService;

  public LinksServiceImpl(WebClient webClient, LikesService likesService, Handler<AsyncResult<LinksService>> readyHandler) {
    this.webClient = webClient;
    this.likesService = likesService;
    readyHandler.handle(Future.succeededFuture(this));
  }

  public LinksService getUserLinks(String authToken, Handler<AsyncResult<JsonArray>> resultHandler) {
    LOGGER.info("Fetching user links. . .");
    webClient
      .get("links", "/v1/links")
      .putHeader("X-NEMO-AUTH", authToken)
      .send(ar -> {
        if (ar.succeeded()) {
          HttpResponse<Buffer> response = ar.result();
          if (response.statusCode() != 200) {
            LOGGER.error("Error during fetching user's links, Status code: " + response.statusCode());
            resultHandler.handle(Future.failedFuture(ar.cause()));
          } else {
            LOGGER.info("User links are here, sending response");
            JsonArray ja = response.bodyAsJsonObject().getJsonArray("data");
            resultHandler.handle(Future.succeededFuture(ja));

//            likesService.getImagesLikes(authToken, ja, asyncRes -> {
//              if (asyncRes.succeeded()) {
//                resultHandler.handle(Future.succeededFuture(asyncRes.result()));
//              } else {
//                resultHandler.handle(Future.failedFuture(ar.cause()));
//              }
//            });
          }
        } else {
          LOGGER.error("Error during fetching user's links");
          resultHandler.handle(Future.failedFuture(ar.cause()));
        }
      });
    return this;
  }
}
