package model;

public class Notification extends BaseMessage{
    private final String status;
    public Notification(String message, String status) {
        super(message);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
