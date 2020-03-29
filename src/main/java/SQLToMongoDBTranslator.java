import converter.ConvertException;
import converter.Converter;
import converter.converter_delegate.MongoDBConverterDelegate;
import org.jetbrains.annotations.NotNull;
import parser.ParseException;
import structures.Node;
import parser.Parser;

public class SQLToMongoDBTranslator {

    private Parser parser;
    private Converter mongoDBConverter;

    SQLToMongoDBTranslator() {
        parser = new Parser();
        mongoDBConverter = new Converter(new MongoDBConverterDelegate());
    }

    @NotNull
    public String translate(@NotNull String sqlQuery) throws TranslateException {
        try {
            Node query = parser.parse(sqlQuery);
            return mongoDBConverter.convert(query);
        } catch (ParseException | ConvertException e) {
            throw new TranslateException();
        }
    }
}
