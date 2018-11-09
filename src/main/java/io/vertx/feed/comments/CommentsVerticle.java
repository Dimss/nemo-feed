package io.vertx.feed.comments;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;

public class CommentsVerticle extends AbstractVerticle {

  public void start(Future<Void> startFeature) throws Exception {
    
    CommentsService.create(WebClient.create(vertx), ready -> {
      ServiceBinder serviceBinder = new ServiceBinder(vertx);
      serviceBinder
        .setAddress("imageComments")
        .register(CommentsService.class, ready.result());
      startFeature.complete();
    });
  }
}
