package advisor;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class AdvisorController {
    private AdvisorModel model;
    private AdvisorView view;

    public AdvisorController(AdvisorModel model, AdvisorView view) {
        this.model = model;
        this.view = view;
    }

    public void methodController(String method, String categoryName) {
        if(!method.equals("auth") && !model.checkAuth()) {
            System.out.println("Please, provide access for application.");
            return;
        }
        JsonObject jo = null;
        String catId = "";
        List<String> v4print = new ArrayList<>();

        switch (method) {
            case "auth":
                try {
                    model.doAuth();
                    view.print(model.checkAuth() ? "Success!" : "Error! Not passed token.");

                    jo = model.makeRequest("/v1/browse/categories", null);
                    if (model.checkResponse(jo)) {
                        model.getJO(jo);
                        jo = model.makeRequest("/v1/browse/categories", "?offset=0&limit=" + model.getCurrentTotal());
                        model.fillAllCategories(model.getJO(jo));
                    }
                    jo = null;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "new":
                jo = model.makeRequest("/v1/browse/new-releases", null);
                break;
            case "featured":
                jo = model.makeRequest("/v1/browse/featured-playlists", null);
                break;
            case "categories":
                jo = model.makeRequest("/v1/browse/categories", null);
                break;
            case "playlists":
                if ((catId = model.getCategoriesId(categoryName)) == null) {
                    System.out.println("Unknown category name.");
                } else {
                    jo = model.makeRequest("/v1/browse/categories/" + catId + "/playlists", null);
                }
                break;
            case "prev":
                if (model.getCurrentPage()== 1) {
                    jo = null;
                    view.print("No more pages.");
                } else {
                    jo = model.makeRequest(null, "-");
                }
                break;
            case "next":
                if (model.getCurrentPage() == model.getTotalPages()) {
                    jo = null;
                    view.print("No more pages.");
                } else {
                    jo = model.makeRequest(null, "+");
                }
                break;
            default:
                view.wrongCommand(method);
                break;
        }
        if (jo != null && model.checkResponse(jo)) {
            v4print.addAll(model.getSpotifyData(model.getJO(jo)));
            if (v4print.size() > 0) {
                view.printPagedList(v4print, model.getCurrentPage(), model.getTotalPages());
            }
        }
    }

}
