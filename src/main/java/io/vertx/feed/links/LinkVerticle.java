package io.vertx.feed.links;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.client.WebClient;
import io.vertx.feed.likes.LikesService;
import io.vertx.serviceproxy.ServiceBinder;

public class LinkVerticle extends AbstractVerticle {
  private LikesService likesService;

  public void start(Future<Void> startFeature) throws Exception {
    likesService = LikesService.createProxy(vertx, "imageLikes");
    LinksService.create(WebClient.create(vertx), likesService, ready -> {
      ServiceBinder serviceBinder = new ServiceBinder(vertx);
      serviceBinder
        .setAddress("userLinks")
        .register(LinksService.class, ready.result());
      startFeature.complete();
    });
  }
}
