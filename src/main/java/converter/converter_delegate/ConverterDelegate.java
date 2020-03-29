package converter.converter_delegate;

import org.jetbrains.annotations.NotNull;
import structures.Token;

public interface ConverterDelegate {

    // Rule1: QUERY = select COLUMN_NAMES from name WHERE_PART SKIP_PART (rule1)
    String query_rule1(String select, String column_names_list, String from, String database_name, String where_part, String skip_part);

    // Rule1: COLUMN_NAMES = star
    String columns_names_rule1(String star);

    // Rule2: COLUMN_NAMES = name COLUMN_NAMES_CONT
    String columns_names_rule2(String column_name, String column_names_cont);

    // Rule1: COLUMN_NAMES_CONT = comma name COLUMN_NAMES_CONT
    String columns_names_cont_rule1(String comma, String column_name, String column_names_list_cont);

    // Rule2: COLUMN_NAMES_CONT = eps
    String columns_names_cont_rule2();

    // Rule1: WHERE_PART = where CONDITION
    String where_part_rule1(String where, String condition);

    // Rule2: WHERE_PART = eps
    String where_part_rule2();

    // Rule1: CONDITION = name comparing_op FIELD_VALUE
    String condition_rule1(String name, String comparing_op, String field_value);

    // Rule2: CONDITION = FIELD_VALUE comparing_op name
    String condition_rule2(String field_value, String comparing_op, String name);

    // Rule1: FIELD_VALUE = string
    String field_value_rule1(String string_val);

    // Rule2: FIELD_VALUE = neg_int
    String field_value_rule2(String neg_int_val);

    // Rule3: FIELD_VALUE = pos_int
    String field_value_rule3(String pos_int_val);

    // Rule1: SKIP_LIMIT_PART = ABS_SKIP_PART LIMIT_PART
    String skip_limit_part_rule1(String abs_skip_part, String limit_part);

    // Rule2: SKIP_LIMIT_PART = ABS_LIMIT_PART SKIP_PART
    String skip_limit_part_rule2(String abs_limit_part, String skip_part);

    // Rule3: SKIP_LIMIT_PART = eps
    String skip_limit_part_rule3();

    // Rule1: SKIP_PART = ABS_SKIP_PART
    String skip_part_rule1(String abs_skip_part);

    // Rule2: SKIP_PART = eps
    String skip_part_rule2();

    // Rule1: ABS_SKIP_PART = SKIP pos_int
    String abs_skip_part_rule1(String skip, String pos_int_val);

    // Rule1: LIMIT_PART = ABS_LIMIT_PART
    String limit_part_rule1(String abs_limit_part);

    // Rule2: LIMIT_PART = eps
    String limit_part_rule2();

    // Rule1: ABS_LIMIT_PART = limit pos_int
    String abs_limit_part_rule1(String limit, String pos_int_val);

    // terminal representation
    String terminal_representation(@NotNull Token token, String content);
}
