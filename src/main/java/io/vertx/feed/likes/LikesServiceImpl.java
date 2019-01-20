package io.vertx.feed.likes;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.feed.MainVerticle;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LikesServiceImpl implements LikesService {
  private final static Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
  private WebClient webClient;

  public LikesServiceImpl(WebClient webClient, Handler<AsyncResult<LikesService>> readyHandler) {
    this.webClient = webClient;
    readyHandler.handle(Future.succeededFuture(this));
  }

  public LikesService getImageLikes(String authToken, String imageId, Handler<AsyncResult<JsonObject>> resultHandler) {
    LOGGER.info("Fetching image likes . . .");
    webClient
      .get("likes", "/v1/likes/" + imageId)
      .putHeader("X-NEMO-AUTH", authToken)
      .send(ar -> {
        if (ar.succeeded()) {
          HttpResponse<Buffer> response = ar.result();
          if (response.statusCode() != 200) {
            LOGGER.error("Error during fetching image likes, Status code: " + response.statusCode());
            resultHandler.handle(Future.failedFuture(ar.cause()));
          } else {
            LOGGER.info("Image likes are here, sending response");
            resultHandler.handle(Future.succeededFuture(response.bodyAsJsonObject()));
          }
        } else {
          LOGGER.error("Error during fetching image likes");
          resultHandler.handle(Future.failedFuture(ar.cause()));
        }
      });
    return this;
  }

  public LikesService addLike(String authToken, String imageId, Handler<AsyncResult<JsonObject>> resultHandler) {
    LOGGER.info("Fetching image likes . . .");
    webClient
      .post("likes", "/v1/likes/" + imageId)
      .putHeader("X-NEMO-AUTH", authToken)
      .send(ar -> {
        if (ar.succeeded()) {
          HttpResponse<Buffer> response = ar.result();
          if (response.statusCode() != 200) {
            LOGGER.error("Error during fetching image likes, Status code: " + response.statusCode());
            resultHandler.handle(Future.failedFuture(ar.cause()));
          } else {
            LOGGER.info("Image likes are here, sending response");
            resultHandler.handle(Future.succeededFuture(response.bodyAsJsonObject()));
          }
        } else {
          LOGGER.error("Error during fetching image likes");
          resultHandler.handle(Future.failedFuture(ar.cause()));
        }
      });
    return this;
  }

  public LikesService getImagesLikes(String authToken, String testToken, JsonArray ja, Handler<AsyncResult<JsonArray>> resultHandler) {
    LOGGER.info("Fetching image likes . . .");
    JsonArray resultArray = new JsonArray();
    for (Object o : ja) {
      JsonObject link = (JsonObject) o;
      String imageId = link.getString("_id");
      webClient
        .get("likes", "/v1/likes/" + imageId)
        .putHeader("X-NEMO-AUTH", authToken)
        .putHeader("X-APP-TEST", testToken)
        .send(ar -> {
          if (ar.succeeded()) {
            HttpResponse<Buffer> response = ar.result();
            if (response.statusCode() != 200) {
              LOGGER.error("Error during fetching image likes, Status code: " + response.statusCode());
              resultHandler.handle(Future.failedFuture(ar.cause()));
            } else {
              LOGGER.info("Image likes are here, sending response");
              link.put("likes", response.bodyAsJsonObject().getValue("data"));
              resultArray.add(link);
              // Finish future
              if (ja.size() == resultArray.size())
                resultHandler.handle(Future.succeededFuture(resultArray));
            }
          } else {
            LOGGER.error("Error during fetching image likes");
            resultHandler.handle(Future.failedFuture(ar.cause()));
          }
        });
    }
    return this;
  }
}
