package canhnam357.shortenurlproject.Url;

public class TooManyRequest extends RuntimeException {
    public TooManyRequest(String message) {
        super(message);
    }
}
