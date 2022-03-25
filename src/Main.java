import model.Request;
import service.TabletApplication;
import service.Webshop;

public class Main {
    public static void main(String[] args) {
        // simulates placing an order from the Webshop
        Request request1 = new Request("Print Dog(PLA)-Red");
        Webshop.placeOrder(request1);

        System.out.println("\n---------------------------\n");

        // simulates sending a request from the tablet application
        Request request2 = new Request("Print Spaceship(ABS)-Pink");
        TabletApplication.sendRequestToRestService(request2);
    }
}
