import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        SQLToMongoDBTranslator translator = new SQLToMongoDBTranslator();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String sqlQuery = scanner.nextLine();
            System.out.print(sqlQuery + " -> ");
            try {
                System.out.println(translator.translate(sqlQuery));
            } catch (TranslateException e) {
                System.out.println("X");
            }
        }
    }
}
