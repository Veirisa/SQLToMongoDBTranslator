package converter.converter_delegate;

import structures.Token;

public class MongoDBConverterDelegate implements ConverterDelegate {

    public MongoDBConverterDelegate() {}

    private String invertComparingOperator(String mongoDBComparingOperator) {
        switch (mongoDBComparingOperator) {
            case "$lt":
                return "$gt";
            case "$gt":
                return "$lt";
            default:
                return mongoDBComparingOperator;
        }
    }

    @Override
    public String queryRule1(String select, String columnNames, String from, String databaseName, String wherePart, String skipLimitPart) {
        return "db." + databaseName + ".find(" + wherePart + columnNames + ")" + skipLimitPart;
    }

    @Override
    public String columnNamesRule1(String star) {
        return "";
    }

    @Override
    public String columnNamesRule2(String columnName, String columnNamesCont) {
        return ", {" + columnName + ": 1" + columnNamesCont + "}";
    }

    @Override
    public String columnNamesContRule1(String comma, String columnName, String columnNamesCont) {
        return ", " + columnName + ": 1" + columnNamesCont;
    }

    @Override
    public String columnNamesContRule2() {
        return "";
    }

    @Override
    public String wherePartRule1(String where, String condition) {
        return "{" + condition + "}";
    }

    @Override
    public String wherePartRule2() {
        return "{}";
    }

    @Override
    public String conditionRule1(String name, String comparingOp, String fieldValue) {
        return name + ": {" + comparingOp + ": " + fieldValue + "}";
    }

    @Override
    public String conditionRule2(String fieldValue, String comparingOp, String name) {
        return name + ": {" + invertComparingOperator(comparingOp) + ": " + fieldValue + "}";
    }

    @Override
    public String fieldValueRule1(String stringVal) {
        return stringVal;
    }

    @Override
    public String fieldValueRule2(String negIntVal) {
        return negIntVal;
    }

    @Override
    public String fieldValueRule3(String posIntVal) {
        return posIntVal;
    }

    @Override
    public String skipLimitPartRule1(String skipPart, String limitPart) {
        return skipPart + limitPart;
    }

    @Override
    public String skipLimitPartRule2(String limitPart, String skipPart) {
        return limitPart + skipPart;
    }

    @Override
    public String skipLimitPartRule3() {
        return "";
    }

    @Override
    public String skipPartRule1(String absSkipPart) {
        return absSkipPart;
    }

    @Override
    public String skipPartRule2() {
        return "";
    }

    @Override
    public String absSkipPartRule1(String skip, String posIntVal) {
        return ".skip(" + posIntVal + ")";
    }

    @Override
    public String limitPartRule1(String absLimitPart) {
        return absLimitPart;
    }

    @Override
    public String limitPartRule2() {
        return "";
    }

    @Override
    public String absLimitPartRule1(String limit, String posIntVal) {
        return ".limit(" + posIntVal + ")";
    }

    @Override
    public String terminalRepresentation(Token token, String content) {
        switch (token) {
            case COMPARING_OP:
                switch (content) {
                    case "=":
                        return "$eq";
                    case "<>":
                        return "$ne";
                    case "<":
                        return "$lt";
                    case ">":
                        return "$gt";
                    default:
                        return "";
                }
            default:
                return content;
        }
    }
}
