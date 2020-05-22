package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/*import java.io.File;
import java.io.FileWriter;*/
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdvisorModel {
    private static final String REDIRECT_URI = "redirect_uri=http://localhost:8080&response_type=code";
    private static final String CLIENT_ID = "client_id=8107a73979f7483eb237bc3be803c37b";
    private static final String CLIENT_SECRET = "client_secret=1888388a10c240109e56bb01c7b0c361";
    private static final String API_SUB_LINK = "/api/token";
    private boolean isAuth = false;
    private String access_token = "";
    private String access_server = "https://accounts.spotify.com";
    private String api_server = "https://api.spotify.com";
    private StringBuilder last_request_suffix = new StringBuilder();
    private Integer page_num = 5;
    private String auth = "/authorize?" + CLIENT_ID + "&" + REDIRECT_URI;
    private WebRequest webRq = WebRequest.getInstance();
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<String> categoriesId = new ArrayList<>();
    private static AdvisorModel advModelInstance;
    private Integer currentTotal = 0;
    private Integer currentOffset = 0;
    private Integer currentLimit = 0;

    //private static int tempI = 0; //FOR TESt FILES Writing

    private AdvisorModel() { }

    public static AdvisorModel getInstance() {
        if (advModelInstance == null) {
            advModelInstance = new AdvisorModel();
        }
        return advModelInstance;
    }

    protected void doAuth() throws Exception {
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

    protected boolean checkAuth() {
        return isAuth;
    }

    protected String getCategoriesId(String categoryName) {
        if (categories.contains(categoryName.toLowerCase())) {
            return categoriesId.get(categories.indexOf(categoryName.toLowerCase()));
        } else {
            return null;
        }
    }

    protected Integer getCurrentTotal() {
        return currentTotal;
    }

    protected Integer getCurrentOffset() {
        return currentOffset;
    }

    protected Integer getCurrentLimit() {
        return currentLimit;
    }

    protected Integer getPage_num() {
        return page_num;
    }

    protected int getCurrentPage() {
        if (currentOffset > 0) {
            return  (currentOffset / page_num) + 1;
        }
        return 1;
    }

    protected int getTotalPages() {
        return (int) Math.ceil((double) currentTotal / page_num);
    }

    protected JsonObject makeRequest(String addParam, String addAmount) {
        try {
            if (addAmount == null) {
                addAmount = "?offset=0&limit=" + page_num;
            } else if ("+".equals(addAmount)) {
                addAmount = "?offset=" + (currentOffset + page_num) + "&limit=" + page_num;
            } else if ("-".equals(addAmount)) {
                addAmount = "?offset=" + (currentOffset - page_num) + "&limit=" + page_num;
            }

            if (addParam == null) {
                addParam = last_request_suffix.toString();
            } else {
                last_request_suffix.delete(0, last_request_suffix.length());
                last_request_suffix.append(addParam);
            }
            String sReq = webRq.webApiRequest(api_server + addParam + addAmount, access_token);

            /*String[] vSS = addParam.split("/+"); //FOR TEST
            writeUsingFileWriter(sReq, vSS[vSS.length - 1]);*/

            return JsonParser.parseString(sReq).getAsJsonObject();
        } catch (IOException | InterruptedException e) {
        e.printStackTrace();
    }
    return null;
    }

    protected boolean checkResponse(JsonObject response) {
        if (response.has("error")) {
            JsonObject jerr = response.getAsJsonObject("error");
            System.out.println(jerr.get("message").getAsString());
            return false;
        }

        return true;
    }

    protected JsonObject getJO(JsonObject response) {
        JsonObject jout = null;
        String next = "";
        String prev = "";
        if (response.has("categories")) {
            jout = response.getAsJsonObject("categories");
        } else if (response.has("albums")) {
            jout = response.getAsJsonObject("albums");
        } else if (response.has("playlists")) {
            jout = response.getAsJsonObject("playlists");
        }

        if (jout != null) {
            currentLimit = jout.get("limit").getAsInt();
            currentOffset = jout.get("offset").getAsInt();
            currentTotal = jout.get("total").getAsInt();
            if (!jout.get("next").isJsonNull()) {
                next = jout.get("next").getAsString();
            }
            if (!jout.get("previous").isJsonNull()) {
                prev = jout.get("previous").getAsString();
            }
        }

        return jout;
    }

    protected void fillAllCategories(JsonObject jo) { //"/v1/browse/categories"
        categories.clear();
        categoriesId.clear();

        for (JsonElement items : jo.getAsJsonArray("items")) {
            categories.add(items.getAsJsonObject().get("name").getAsString().toLowerCase());
            categoriesId.add(items.getAsJsonObject().get("id").getAsString());
        }

        //writeUsingFileWriter(categories.toString(), "allcateg"); //FOR TEST

    }

    protected List<String> getSpotifyData(JsonObject jo) {
        List<String> result = new ArrayList<>();
        for (JsonElement items : jo.getAsJsonArray("items")) {
            result.add(items.getAsJsonObject().get("name").getAsString());
            if (items.getAsJsonObject().has("artists")) {
                List<String> sArtists = new ArrayList<>();
                for (JsonElement artists : items.getAsJsonObject().getAsJsonArray("artists")) {
                    sArtists.add(artists.getAsJsonObject().get("name").getAsString());
                }
                result.add(sArtists.toString());
                sArtists.clear();
            }
            if (items.getAsJsonObject().has("external_urls")) {
                JsonObject extUrls = items.getAsJsonObject().getAsJsonObject("external_urls");
                result.add(extUrls.get("spotify").getAsString() + "\n");
            }

        }
        return result;
    }

    protected void initModel(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            if ("-access".equals(args[i])) {
                access_server = args[i + 1];
            } else if ("-resource".equals(args[i])) {
                api_server = args[i + 1];
            } else if ("-page".equals(args[i])) {
                page_num = Integer.parseInt(args[i + 1]);
            }
        }
    }

    /*private static void writeUsingFileWriter(String data, String fileName) {
        File file = new File("D:\\" + fileName + "_" + tempI + ".json");
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                tempI++;
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

}
