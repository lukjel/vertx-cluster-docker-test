package com.git.playground;

import java.util.ArrayList;
import java.util.List;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import rx.Observable;

public class MainVerticle extends AbstractVerticle {

	private static Logger log = LoggerFactory.getLogger(MainVerticle.class);
	private static final int TEST_LAYERS = 3;
	private static final int TEST_MESSAGES = 50;

	@Override
	public void start() throws Exception {
		log.debug("start Docker Test");
		startHttpServer();
		this.vertx.eventBus().<JsonObject> consumer("consume", msg -> consume(msg));
		log.debug("done");
	}

	private Observable<HttpServer> startHttpServer() {
		log.debug("[startHttpServer] start!");
		final HttpServerOptions httpServerOptions = new HttpServerOptions()
			.setHost("0.0.0.0")
			.setPort(8080);
		final HttpServer server = vertx.createHttpServer(httpServerOptions);
		return server.requestHandler(prepareRouter()::accept).listenObservable();
	}

	private Router prepareRouter() {
		final Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.get("/ping").handler(ctx -> {
			final long startTime = System.currentTimeMillis();
			vertx.eventBus().<JsonObject> send("consume", new JsonObject(), ar -> {
				if (ar.succeeded()) {
					long endTime = System.currentTimeMillis();
					long duration = endTime - startTime;
					int c = ar.result().body().getInteger("c", 1);
					ctx.response().end("Time for processing " + c + " messages in " + duration + "ms == " + (c / duration) + " events/ms");
				} else {
					ctx.response().end("Some problem: " + ar.cause());
				}
			});
		});
		router.get("/*").handler(ctx -> ctx.response().end("OK"));
		return router;
	}

	private void consume(Message<JsonObject> msg) {
		JsonObject body = msg.body();
		int counter = body.getInteger("counter", 0);
		if (counter < TEST_LAYERS) {
			body.put("counter", counter + 1);
			List<Observable<Message<JsonObject>>> list = new ArrayList<>();
			for (int i = 0; i < TEST_MESSAGES; i++) {
				list.add(this.vertx.eventBus().sendObservable("consume", body));
			}
			Observable
				.zip(list, z -> {
					int c = 0;
					for (Object o : z) {
						Message<JsonObject> m = (Message<JsonObject>) o;
						c += m.body().getInteger("c", 0);
					}
					return new JsonObject().put("c", c + 1);
				})
				.subscribe(j -> msg.reply(j),
						e -> msg.fail(-1, "" + e));
		} else {
			msg.reply(new JsonObject().put("c", 1));
		}
	}
}
