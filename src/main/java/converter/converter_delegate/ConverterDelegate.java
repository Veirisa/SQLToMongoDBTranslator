package converter.converter_delegate;

import org.jetbrains.annotations.NotNull;
import structures.Token;

public interface ConverterDelegate {

    // Rule1: QUERY = select COLUMN_NAMES from name WHERE_PART SKIP_PART (rule1)
    String queryRule1(String select, String columnNamesList, String from, String databaseName, String wherePart, String skipPart);

    // Rule1: COLUMN_NAMES = star
    String columnsNamesRule1(String star);

    // Rule2: COLUMN_NAMES = name COLUMN_NAMES_CONT
    String columnsNamesRule2(String columnName, String columnNamesCont);

    // Rule1: COLUMN_NAMES_CONT = comma name COLUMN_NAMES_CONT
    String columnsNamesContRule1(String comma, String columnName, String columnNamesListCont);

    // Rule2: COLUMN_NAMES_CONT = eps
    String columnsNamesContRule2();

    // Rule1: WHERE_PART = where CONDITION
    String wherePartRule1(String where, String condition);

    // Rule2: WHERE_PART = eps
    String wherePartRule2();

    // Rule1: CONDITION = name comparing_op FIELD_VALUE
    String conditionRule1(String name, String comparingOp, String fieldValue);

    // Rule2: CONDITION = FIELD_VALUE comparing_op name
    String conditionRule2(String fieldValue, String comparingOp, String name);

    // Rule1: FIELD_VALUE = string
    String fieldValueRule1(String stringVal);

    // Rule2: FIELD_VALUE = neg_int
    String fieldValueRule2(String negIntVal);

    // Rule3: FIELD_VALUE = pos_int
    String fieldValueRule3(String posIntVal);

    // Rule1: SKIP_LIMIT_PART = ABS_SKIP_PART LIMIT_PART
    String skipLimitPartRule1(String absSkipPart, String limitPart);

    // Rule2: SKIP_LIMIT_PART = ABS_LIMIT_PART SKIP_PART
    String skipLimitPartRule2(String absLimitPart, String skipPart);

    // Rule3: SKIP_LIMIT_PART = eps
    String skipLimitPartRule3();

    // Rule1: SKIP_PART = ABS_SKIP_PART
    String skipPartRule1(String absSkipPart);

    // Rule2: SKIP_PART = eps
    String skipPartRule2();

    // Rule1: ABS_SKIP_PART = SKIP pos_int
    String absSkipPartRule1(String skip, String posIntVal);

    // Rule1: LIMIT_PART = ABS_LIMIT_PART
    String limitPartRule1(String absLimitPart);

    // Rule2: LIMIT_PART = eps
    String limitPartRule2();

    // Rule1: ABS_LIMIT_PART = limit pos_int
    String absLimitPartRule1(String limit, String posIntVal);

    // terminal representation
    String terminalRepresentation(@NotNull Token token, String content);
}
