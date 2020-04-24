package advisor;

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
    private static String auth = "/authorize?" + CLIENT_ID + "&" + REDIRECT_URI;

    private static void print(String vStr, ArrayList<String> vList, boolean isAuth) {
        if(isAuth) {
            System.out.println(vStr);
            for (String out : vList) {
                System.out.println(out);
            }
        } else {
            System.out.println("Please, provide access for application.");
        }
    }

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
                //System.out.println("Response: " + connection.query.toString());
                System.out.println("code received");

                //new WebRequest().checkSrvRsp();
                connection.webStop(1);

                Thread.sleep(100);

                System.out.println("making http request for access_token...");
                WebRequest webRq = new WebRequest();
                webRq.webGo(access_server + API_SUB_LINK, vCode, CLIENT_ID, CLIENT_SECRET);

                isAuth = true;
                break;
            }
        } while (true);
    }

    public static void main(String[] args) throws IOException, InterruptedException, Exception {
        //BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        for (int i = 0; i < args.length; i += 2) {
            if ("-access".equals(args[i])) {
                access_server = args[i + 1];
            }
        }

        Scanner input = new Scanner(System.in);
        String command;

        ArrayList<String> featured = new ArrayList<String>();
        ArrayList<String> news = new ArrayList<String>();
        ArrayList<String> categories = new ArrayList<String>();
        ArrayList<String> playlists = new ArrayList<String>();

        news.add("Mountains [Sia, Diplo, Labrinth]");
        news.add("Runaway [Lil Peep]");
        news.add("The Greatest Show [Panic! At The Disco]");

        featured.add("Mellow Morning");

        categories.add("Top Lists");

        playlists.add("Walk Like A Badass");

        //System.out.print(">");

        while (!"exit".equalsIgnoreCase(command = input.next())) {
            switch (command.toLowerCase()) {
                case "new":
                    print("---NEW RELEASES---", news, isAuth);
                    break;
                case "featured":
                    print("---FEATURED---", featured, isAuth);
                    break;
                case "categories":
                    print("---CATEGORIES---", categories, isAuth);
                    break;
                case "playlists":
                    input.next();
                    print("---MOOD PLAYLISTS---", playlists, isAuth);
                    break;
                case "auth":
                    doAuth();
                    System.out.println(isAuth ? "---SUCCESS---" : "Error!");
                    break;
                default:
                    System.out.println("Unknown command '" + command + "'. Please try another one or type 'exit'.");
                    break;
            }
            //System.out.print(">");
        }
        System.out.println("---GOODBYE!---");
    }
}
