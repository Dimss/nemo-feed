package io.vertx.feed.likes;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;


@ProxyGen
public interface LikesService {

  @Fluent
  LikesService getImageLikes(String authToken, String imageId, Handler<AsyncResult<JsonObject>> resultHandler);

  @Fluent
  LikesService getImagesLikes(String authToken, JsonArray ja, Handler<AsyncResult<JsonArray>> resultHandler);

  @Fluent
  LikesService addLike(String authToken, String imageId, Handler<AsyncResult<JsonObject>> resultHandler);

  static LikesService create(WebClient webClient, Handler<AsyncResult<LikesService>> readyHandler) {
    return new LikesServiceImpl(webClient, readyHandler);
  }

  static LikesService createProxy(Vertx vertx, String address) {
    return new LikesServiceVertxEBProxy(vertx, address);
  }

}
