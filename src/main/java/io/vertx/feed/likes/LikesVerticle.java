package io.vertx.feed.likes;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;

public class LikesVerticle extends AbstractVerticle {

  public void start(Future<Void> startFeature) throws Exception {
    
    LikesService.create(WebClient.create(vertx), ready -> {
      ServiceBinder serviceBinder = new ServiceBinder(vertx);
      serviceBinder
        .setAddress("imageLikes")
        .register(LikesService.class, ready.result());
      startFeature.complete();
    });
  }
}
