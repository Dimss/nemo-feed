package io.vertx.feed;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.feed.links.LinkVertical;
import io.vertx.feed.links.LinksService;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    Future<String> linkVerticalDeployment = Future.future();
    vertx.deployVerticle(new LinkVertical(), linkVerticalDeployment.completer());

    linkVerticalDeployment.compose(id -> {
      Future<String> httpVerticalDeployment = Future.future();
      vertx.deployVerticle(
        "io.vertx.feed.http.HttpVerticle",
        new DeploymentOptions().setInstances(1),
        httpVerticalDeployment.completer());
      return httpVerticalDeployment;
    }).setHandler(ar -> {
      if (ar.succeeded()) {
        startFuture.complete();
      } else {
        startFuture.fail(ar.cause());
      }
    });


  }

}
