package io.vertx.feed.links;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;

public class LinkVerticle extends AbstractVerticle {

  public void start(Future<Void> startFeature) throws Exception {

    LinksService.create(WebClient.create(vertx), ready -> {
      ServiceBinder serviceBinder = new ServiceBinder(vertx);
      serviceBinder
        .setAddress("userLinks")
        .register(LinksService.class, ready.result());
      startFeature.complete();
    });
  }
}
