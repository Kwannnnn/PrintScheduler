package service;

import model.Notification;
import model.PrintStatus;

public class NotificationService {

    public static void handlePrintStatus(PrintStatus printStatus) {
        notifyWebshop(printStatus);
        notifyTabletApplication(printStatus);
    }

    private static void notifyWebshop(PrintStatus printStatus) {
        Notification notification = new Notification(printStatus.getMessage(), "Finished");
        System.out.println("NotificationService: Notification sent to Webshop");
        Webshop.handleNotification(notification);
    }

    private static void notifyTabletApplication(PrintStatus printStatus) {
        Notification notification = new Notification(printStatus.getMessage(), "Finished");
        System.out.println("NotificationService: Notification sent to TabletApplication");
        TabletApplication.handleNotification(notification);
    }
}
