package advisor;

import java.util.Scanner;

public class AdvisorMVCMain {

    public static void main(String[] args) {

        AdvisorModel model = AdvisorModel.getInstance();
        AdvisorView view = AdvisorView.getInstance();
        model.initModel(args);
        AdvisorController controller = new AdvisorController(model, view);

        Scanner input = new Scanner(System.in);
        String command;
        view.showView();

        while (!"exit".equalsIgnoreCase(command = input.next().toLowerCase())) {
            if ("playlists".equalsIgnoreCase(command)) {
                controller.methodController(command, input.next());
            } else {
                controller.methodController(command, null);
            }
        }
        view.showExitView();
    }
}
