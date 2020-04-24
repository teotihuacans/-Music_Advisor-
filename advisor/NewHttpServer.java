package advisor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

public class NewHttpServer {

    private HttpServer httpServer;
    private final String context = "/";
    public String query;

    public NewHttpServer() {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(8080), 0);
            httpServer.createContext(context, new HttpHandler() {
                public void handle(HttpExchange exchange) throws IOException {
                    query = exchange.getRequestURI().getQuery();
                    //URI uri = exchange.getRequestURI();
                    //query = uri.getQuery();

                    String hello = "Got the code. Return back to your program.";
                    if (query != null && query.startsWith("code=")) {
                        hello = "Got the code. Return back to your program.";
                    }
                    else {
                        hello = "Not found authorization code. Try again.";
                    }
                    exchange.sendResponseHeaders(200, hello.length());
                    exchange.getResponseBody().write(hello.getBytes());
                    exchange.getResponseBody().close();
                    exchange.close();
                }
            });
            httpServer.setExecutor(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void webStart() {
        this.httpServer.start();
    }

    public void webStop(int i) {
        this.httpServer.stop(i);
    }
}
