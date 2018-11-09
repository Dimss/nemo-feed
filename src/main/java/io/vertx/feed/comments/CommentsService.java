package io.vertx.feed.comments;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;


@ProxyGen
public interface CommentsService {


  @Fluent
  CommentsService getImagesComments(String authToken, JsonArray ja, Handler<AsyncResult<JsonArray>> resultHandler);

  @Fluent
  CommentsService addComment(String authToken, JsonObject commentPayload, Handler<AsyncResult<JsonObject>> resultHandler);

  static CommentsService create(WebClient webClient, Handler<AsyncResult<CommentsService>> readyHandler) {
    return new CommentsServiceImpl(webClient, readyHandler);
  }

  static CommentsService createProxy(Vertx vertx, String address) {
    return new CommentsServiceVertxEBProxy(vertx, address);
  }

}
