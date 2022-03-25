package service;

import model.Notification;
import model.Request;

public class Webshop {
    public static void placeOrder(Request request) {
        System.out.println("Webshop: Order request sent to RESTService");
        RESTService.handleOrderRequest(request);
    }

    public static void handleNotification(Notification notification) {
        System.out.println("Webshop: Notification received for print job \""
                + notification.getMessage()
                + "\" with status: " + notification.getStatus());
    }
}
