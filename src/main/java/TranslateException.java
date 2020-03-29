public class TranslateException extends Exception {

    TranslateException() {
        super("Incorrect SQL query for translation");
    }
}
