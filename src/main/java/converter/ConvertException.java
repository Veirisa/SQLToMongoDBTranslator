package converter;

public class ConvertException extends Exception {

    public ConvertException(String message) {
        super(message);
    }

    public ConvertException(String nodeType, String message) {
        super("Incorrect " + nodeType + " node: " + message);
    }
}

