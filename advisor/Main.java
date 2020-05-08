package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static boolean isAuth = false;
    private static final String REDIRECT_URI = "redirect_uri=http://localhost:8080&response_type=code";
    private static final String CLIENT_ID = "client_id=8107a73979f7483eb237bc3be803c37b";
    private static final String CLIENT_SECRET = "client_secret=1888388a10c240109e56bb01c7b0c361";
    private static final String API_SUB_LINK = "/api/token";
    private static String access_token = "";
    private static String access_server = "https://accounts.spotify.com";
    private static String api_server = "https://api.spotify.com";
    private static String auth = "/authorize?" + CLIENT_ID + "&" + REDIRECT_URI;
    private static WebRequest webRq = new WebRequest();
    private static ArrayList<String> categories = new ArrayList<>();
    private static ArrayList<String> categoriesId = new ArrayList<>();

    private static void doAuth() throws Exception {
        NewHttpServer connection = new NewHttpServer();
        connection.webStart();
        String vCode = "";

        System.out.println("use this link to request the access code:");
        System.out.println(access_server + auth);
        System.out.println("waiting for code...");

        do {
            Thread.sleep(2000);
            vCode = connection.query;
            if (vCode == null || !vCode.startsWith("code=")) {
                Thread.sleep(100);
            } else {
                System.out.println("code received");
                //new WebRequest().checkSrvRsp();
                connection.webStop(1);
                Thread.sleep(100);
                System.out.println("making http request for access_token...");
                access_token = webRq.webGetToken(access_server + API_SUB_LINK, vCode, CLIENT_ID, CLIENT_SECRET);
                if (!access_token.isEmpty()) {
                    isAuth = true;
                    //System.out.println("Parsed token: " + access_token);
                }
                break;
            }
        } while (true);
    }

    private static boolean checkAuth() {
        if(isAuth) {
            return true;
        } else {
            System.out.println("Please, provide access for application.");
            return false;
        }
    }

    private static void getCategories() {
        categories.clear();
        categoriesId.clear();
        try {
            String sCategories = webRq.webApiRequest(api_server + "/v1/browse/categories", access_token);
            JsonObject jo = JsonParser.parseString(sCategories).getAsJsonObject();
            JsonObject categoriesObj = jo.getAsJsonObject("categories");
            for (JsonElement items : categoriesObj.getAsJsonArray("items")) {
                categories.add(items.getAsJsonObject().get("name").getAsString());
                categoriesId.add(items.getAsJsonObject().get("id").getAsString());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkResponse(String response) {
        JsonObject jo = JsonParser.parseString(response).getAsJsonObject();
        if (jo.has("error")) {
            JsonObject jerr = jo.getAsJsonObject("error");
            System.out.println(jerr.get("message").getAsString());
            return false;
        }

        return true;
    }

    public static void main(String[] args) throws IOException, InterruptedException, Exception {
        //BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        for (int i = 0; i < args.length; i += 2) {
            if ("-access".equals(args[i])) {
                access_server = args[i + 1];
            } else if ("-resource".equals(args[i])) {
                api_server = args[i + 1];
            }
        }

        Scanner input = new Scanner(System.in);
        String command;

        //System.out.println("---Spotify API Music Advisor app---");
        //System.out.print(">");

        while (!"exit".equalsIgnoreCase(command = input.next())) {
            switch (command.toLowerCase()) {
                case "new":
                    if (!checkAuth()) {
                        break;
                    }
                    try {
                        String sNew = webRq.webApiRequest(api_server + "/v1/browse/new-releases", access_token);
                        if (!checkResponse(sNew)) {
                            break;
                        }
                        JsonObject jo = JsonParser.parseString(sNew).getAsJsonObject();
                        JsonObject albumsObj = jo.getAsJsonObject("albums");
                        for (JsonElement items : albumsObj.getAsJsonArray("items")) {
                            System.out.println(items.getAsJsonObject().get("name").getAsString());
                            List<String> sArtists = new ArrayList<>();
                            for (JsonElement artists : items.getAsJsonObject().getAsJsonArray("artists")) {
                                sArtists.add(artists.getAsJsonObject().get("name").getAsString());
                            }
                            System.out.println(sArtists);
                            JsonObject extUrls = items.getAsJsonObject().getAsJsonObject("external_urls");
                            System.out.println(extUrls.get("spotify").getAsString() + "\n");
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "featured":
                    if (!checkAuth()) {
                        break;
                    }
                    try {
                        String sFeatured = webRq.webApiRequest(api_server + "/v1/browse/featured-playlists", access_token);
                        if (!checkResponse(sFeatured)) {
                            break;
                        }
                        JsonObject jo = JsonParser.parseString(sFeatured).getAsJsonObject();
                        JsonObject albumsObj = jo.getAsJsonObject("playlists");
                        for (JsonElement items : albumsObj.getAsJsonArray("items")) {
                            System.out.println(items.getAsJsonObject().get("name").getAsString());
                            JsonObject extUrls = items.getAsJsonObject().getAsJsonObject("external_urls");
                            System.out.println(extUrls.get("spotify").getAsString() + "\n");
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "categories":
                    if (!checkAuth()) {
                        break;
                    }
                    getCategories();
                    for (String out : categories) {
                        System.out.println(out);
                    }
                    break;
                case "playlists":
                    if (!checkAuth()) {
                        break;
                    }
                    String inCategory = input.next();
                    if (categories.contains(inCategory)) {
                        String catId = categoriesId.get(categories.indexOf(inCategory));
                        try {
                            String sPlaylists = webRq.webApiRequest(api_server + "/v1/browse/categories/" + catId + "/playlists", access_token);
                            if (!checkResponse(sPlaylists)) {
                                break;
                            }
                            JsonObject jo = JsonParser.parseString(sPlaylists).getAsJsonObject();
                            JsonObject albumsObj = jo.getAsJsonObject("playlists");
                            for (JsonElement items : albumsObj.getAsJsonArray("items")) {
                                System.out.println(items.getAsJsonObject().get("name").getAsString());
                                JsonObject extUrls = items.getAsJsonObject().getAsJsonObject("external_urls");
                                System.out.println(extUrls.get("spotify").getAsString() + "\n");
                            }
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("Unknown category name.");
                    }
                    break;
                case "auth":
                    doAuth();
                    if (isAuth) {
                        getCategories();
                    }
                    System.out.println(isAuth ? "Success!" : "Error! Not passed token.");
                    break;
                default:
                    System.out.println("Unknown command '" + command + "'. Please try another one or type 'exit'.");
                    break;
            }
            //System.out.print(">");
        }
        //System.out.println("---GOODBYE!---");
    }
}
