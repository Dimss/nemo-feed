package io.vertx.feed.links;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

@ProxyGen
public interface LinksService {

  @Fluent
  LinksService getUserLinks(String authToken, Handler<AsyncResult<JsonObject>> resultHandler);

  static LinksService create(WebClient webClient, Handler<AsyncResult<LinksService>> readyHandler) {
    return new LinksServiceImpl(webClient, readyHandler);
  }

  static LinksService createProxy(Vertx vertx, String address) {
    return new LinksServiceVertxEBProxy(vertx, address);
  }

}
