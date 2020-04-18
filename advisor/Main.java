package advisor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static void print(String vStr, ArrayList<String> vList, boolean isauth) {
        if(isauth) {
            System.out.println(vStr);
            for (String out : vList) {
                System.out.println(out);
            }
        } else {
            System.out.println("Please, provide access for application.");
        }
    }

    public static void main(String[] args) throws IOException {
        //BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        Scanner input = new Scanner(System.in);
        String command;
        String redirect_uri = "http://localhost:8080&response_type=code";
        String client_id = "8107a73979f7483eb237bc3be803c37b";
        boolean isauth = false;
        String auth = "https://accounts.spotify.com/authorize?client_id=" + client_id + "&redirect_uri=" + redirect_uri;

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

        System.out.print(">");

        while (!"exit".equalsIgnoreCase(command = input.next())) {
            switch (command.toLowerCase()) {
                case "new":
                    print("---NEW RELEASES---", news, isauth);
                    break;
                case "featured":
                    print("---FEATURED---", featured, isauth);
                    break;
                case "categories":
                    print("---CATEGORIES---", categories, isauth);
                    break;
                case "playlists":
                    input.next();
                    print("---MOOD PLAYLISTS---", playlists, isauth);
                    break;
                case "auth":
                    System.out.println(auth);
                    isauth = true;
                    System.out.println(isauth ? "---SUCCESS---" : "Error!");
                    break;
                default:
                    System.out.println("Unknown command '" + command + "'. Please try another one or type 'exit'.");
                    break;
            }
            System.out.print(">");
        }
        System.out.println("---GOODBYE!---");
    }
}
