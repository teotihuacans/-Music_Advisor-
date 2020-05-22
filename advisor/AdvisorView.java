package advisor;

import java.util.List;

public class AdvisorView {
    private static AdvisorView advViewInstance;

    private AdvisorView() { }

    public static AdvisorView getInstance() {
        if (advViewInstance == null) {
            advViewInstance = new AdvisorView();
        }
        return advViewInstance;
    }

    public void showView() {
        System.out.println("---Spotify API Music Advisor app---");
        //System.out.print(">");
    }

    public void showExitView() {
        System.out.println("---GOODBYE!---");

    }

    public void printList(List<String> strOut) {
        System.out.println(strOut.toString());
    }

    public void printPagedList(List<String> strOut, int pageNum, int totalPages) {
        for (String vSP : strOut) {
            System.out.println(vSP);
        }
        System.out.println("---PAGE " + pageNum + " OF " + totalPages + "---");
    }

    public void wrongCommand(String command) {
        System.out.println("Unknown command '" + command + "'. Please try another one or type 'exit'.");
    }

    public void print(String str) {
        System.out.println(str);
    }

}
