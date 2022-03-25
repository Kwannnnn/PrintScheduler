package service;

import model.Notification;
import model.Request;

public class TabletApplication {
    public static void sendRequestToRestService(Request request) {
        System.out.println("TabletApplication: Request sent to RESTService");
        RESTService.handleTabletRequest(request);
    }

    public static void handleNotification(Notification notification) {
        System.out.println("TabletApplication: Notification received for print job \""
                + notification.getMessage()
                + "\" with status: " + notification.getStatus());
    }
}
