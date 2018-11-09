package io.vertx.feed;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


public class MainVerticle extends AbstractVerticle {
  private final static Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    vertx.deployVerticle("io.vertx.feed.http.HttpVerticle", ar -> {
      LOGGER.info("*** HTTP Verticle deployed: " + ar.result());
    });
    vertx.deployVerticle("io.vertx.feed.links.LinkVerticle", ar -> {
      LOGGER.info("*** LINKS Verticle deployed: " + ar.result());
    });
    vertx.deployVerticle("io.vertx.feed.likes.LikesVerticle", ar -> {
      LOGGER.info("*** LIKES Verticle deployed: " + ar.result());
    });
    vertx.deployVerticle("io.vertx.feed.comments.CommentsVerticle", ar -> {
      LOGGER.info("*** COMMENTS Verticle deployed: " + ar.result());
    });

  }
}
