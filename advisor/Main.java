package advisor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        //BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        Scanner input = new Scanner(System.in);
        String command;

        List<String> featured = new ArrayList<String>();
        List<String> news = new ArrayList<String>();
        List<String> categories = new ArrayList<String>();
        List<String> playlists = new ArrayList<String>();

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
                    System.out.println("---NEW RELEASES---");
                    for (String out : news) {
                        System.out.println(out);
                    }
                    break;
                case "featured":
                    System.out.println("---FEATURED---");
                    for (String out : featured) {
                        System.out.println(out);
                    }
                    break;
                case "categories":
                    System.out.println("---CATEGORIES---");
                    for (String out : categories) {
                        System.out.println(out);
                    }
                    break;
                case "playlists":
                    input.next();
                    System.out.println("---MOOD PLAYLISTS---");
                    for (String out : playlists) {
                        System.out.println(out);
                    }
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
