package converter.converter_delegate;

import org.jetbrains.annotations.NotNull;
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
    public String query_rule1(String select, String column_names, String from, String database_name, String where_part, String skip_limit_part) {
        return "db." + database_name + ".find(" + where_part + column_names + ")" + skip_limit_part;
    }

    @Override
    public String columns_names_rule1(String star) {
        return "";
    }

    @Override
    public String columns_names_rule2(String column_name, String column_names_cont) {
        return ", {" + column_name + ": 1" + column_names_cont + "}";
    }

    @Override
    public String columns_names_cont_rule1(String comma, String column_name, String column_names_cont) {
        return ", " + column_name + ": 1" + column_names_cont;
    }

    @Override
    public String columns_names_cont_rule2() {
        return "";
    }

    @Override
    public String where_part_rule1(String where, String condition) {
        return "{" + condition + "}";
    }

    @Override
    public String where_part_rule2() {
        return "{}";
    }

    @Override
    public String condition_rule1(String name, String comparing_op, String field_value) {
        return name + ": {" + comparing_op + ": " + field_value + "}";
    }

    @Override
    public String condition_rule2(String field_value, String comparing_op, String name) {
        return name + ": {" + invertComparingOperator(comparing_op) + ": " + field_value + "}";
    }

    @Override
    public String field_value_rule1(String string_val) {
        return string_val;
    }

    @Override
    public String field_value_rule2(String neg_int_val) {
        return neg_int_val;
    }

    @Override
    public String field_value_rule3(String pos_int_val) {
        return pos_int_val;
    }

    @Override
    public String skip_limit_part_rule1(String skip_part, String limit_part) {
        return skip_part + limit_part;
    }

    @Override
    public String skip_limit_part_rule2(String limit_part, String skip_part) {
        return limit_part + skip_part;
    }

    @Override
    public String skip_limit_part_rule3() {
        return "";
    }

    @Override
    public String skip_part_rule1(String abs_skip_part) {
        return abs_skip_part;
    }

    @Override
    public String skip_part_rule2() {
        return "";
    }

    @Override
    public String abs_skip_part_rule1(String skip, String pos_int_val) {
        return ".skip(" + pos_int_val + ")";
    }

    @Override
    public String limit_part_rule1(String abs_limit_part) {
        return abs_limit_part;
    }

    @Override
    public String limit_part_rule2() {
        return "";
    }

    @Override
    public String abs_limit_part_rule1(String limit, String pos_int_val) {
        return ".limit(" + pos_int_val + ")";
    }

    @Override
    public String terminal_representation(@NotNull Token token, String content) {
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
