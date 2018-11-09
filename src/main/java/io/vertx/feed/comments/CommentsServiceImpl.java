package io.vertx.feed.comments;

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


public class CommentsServiceImpl implements CommentsService {
  private final static Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
  private WebClient webClient;

  public CommentsServiceImpl(WebClient webClient, Handler<AsyncResult<CommentsService>> readyHandler) {
    this.webClient = webClient;
    readyHandler.handle(Future.succeededFuture(this));
  }

  public CommentsService getImagesComments(String authToken, JsonArray ja, Handler<AsyncResult<JsonArray>> resultHandler) {
    LOGGER.info("Fetching image comments. . .");
    JsonArray resultArray = new JsonArray();
    for (Object o : ja) {
      JsonObject link = (JsonObject) o;
      String imageId = link.getString("_id");
      webClient
        .get("comments", "/v1/comments/" + imageId)
        .putHeader("X-NEMO-AUTH", authToken)
        .send(ar -> {
          if (ar.succeeded()) {
            HttpResponse<Buffer> response = ar.result();
            if (response.statusCode() != 200) {
              LOGGER.error("Error during fetching image comments, Status code: " + response.statusCode());
              resultHandler.handle(Future.failedFuture(ar.cause()));
            } else {
              LOGGER.info("Image comments are here, sending response");
              link.put("comments", response.bodyAsJsonObject().getValue("data"));
              resultArray.add(link);
              // Finish future
              if (ja.size() == resultArray.size())
                resultHandler.handle(Future.succeededFuture(resultArray));
            }
          } else {
            LOGGER.error("Error during fetching image comments");
            resultHandler.handle(Future.failedFuture(ar.cause()));
          }
        });
    }
    return this;
  }

  public CommentsService addComment(String authToken, JsonObject commentPayload, Handler<AsyncResult<JsonObject>> resultHandler) {
    LOGGER.info("Add image comment . . .");
    webClient
      .post("comments", "/v1/comments")
      .putHeader("X-NEMO-AUTH", authToken)
      .sendJsonObject(commentPayload, ar -> {
        if (ar.succeeded()) {
          HttpResponse<Buffer> response = ar.result();
          if (response.statusCode() != 200) {
            LOGGER.error("Error during create new  image comment, Status code: " + response.statusCode());
            resultHandler.handle(Future.failedFuture(ar.cause()));
          } else {
            LOGGER.info("Successfully create new comment");
            resultHandler.handle(Future.succeededFuture(response.bodyAsJsonObject()));
          }
        } else {
          LOGGER.error("Error during create new image comment");
          resultHandler.handle(Future.failedFuture(ar.cause()));
        }
      });
    return this;
  }

}
