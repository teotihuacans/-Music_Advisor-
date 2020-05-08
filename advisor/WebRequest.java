package advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WebRequest {
    private HttpResponse<String> response;
    private HttpRequest request;
    private HttpClient client = HttpClient.newBuilder().build();

    public String webGetToken(String access_api_server, String code, String id, String secret) throws IOException, InterruptedException {
        String json;
        String access_token = "";
        String refresh_token;

        request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(access_api_server))
                .POST(HttpRequest.BodyPublishers.ofString(id + "&" + secret + "&grant_type=authorization_code&" + code + "&redirect_uri=http://localhost:8080"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = response.body();
            JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
            if (jo.has("access_token")) {
                access_token = jo.get("access_token").getAsString();
                refresh_token = jo.get("refresh_token").getAsString();
            }

        return access_token;
    }

    public String webApiRequest(String resource_api_server, String access_token) throws IOException, InterruptedException {

        request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + access_token)
                .uri(URI.create(resource_api_server))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //System.out.println("Api response:");
        //System.out.println(response.body());

        return response.body();
    }

    public void checkSrvRsp() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/?code=123"))
                .GET()
                .build();
        System.out.println("Server response: " + client.send(request, HttpResponse.BodyHandlers.ofString()).body());
    }

}
