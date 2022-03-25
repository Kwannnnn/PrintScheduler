package model;

public abstract class BaseMessage {
    private final String message;

    public BaseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
