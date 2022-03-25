package model;

public class PrintStatus extends BaseMessage {
    private final String status;

    public PrintStatus(String message, String status) {
        super(message);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
