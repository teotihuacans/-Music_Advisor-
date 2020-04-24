package advisor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WebRequest {
    private HttpResponse<String> response;
    private HttpRequest request;
    private HttpClient client = HttpClient.newBuilder().build();

    public void webGo(String access_api_server, String code, String id, String secret) throws IOException, InterruptedException {

        request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(access_api_server))
                .POST(HttpRequest.BodyPublishers.ofString(id + "&" + secret + "&grant_type=authorization_code&" + code + "&redirect_uri=http://localhost:8080"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("response:");
        System.out.println(response.body());
    }

    public void checkSrvRsp() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/?code=123"))
                .GET()
                .build();
        System.out.println("Server response: " + client.send(request, HttpResponse.BodyHandlers.ofString()).body());
    }

}
