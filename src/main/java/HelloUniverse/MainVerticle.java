package HelloUniverse;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.createHttpServer().requestHandler(req -> {
      MySQLConnectOptions connectOptions = new MySQLConnectOptions()
        .setPort(3306)
        .setHost("localhost")
        .setDatabase("app_db")
        .setUser("root")
        .setPassword("toor");

      // Pool options
      PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

      // Create the client pool
      MySQLPool client = MySQLPool.pool(vertx, connectOptions, poolOptions);

      // A simple query
      client
        .query("SELECT NOW() as time;")
        .execute(ar -> {
        if (ar.succeeded()) {
          RowSet<Row> result = ar.result();
          String responseContent = "";

          for (Row row : result) {
            responseContent = row.getValue("time").toString();
          }
          req.response()
            .putHeader("content-type", "text/plain")
            .end("Hello from Vert.x! Current Time is: " + responseContent);
        } else {
          System.out.println("Failure: " + ar.cause().getMessage());
        }

        // Now close the pool
        client.close();
      });
    }).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
