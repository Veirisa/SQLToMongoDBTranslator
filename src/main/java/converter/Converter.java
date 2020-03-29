package converter;

import converter.converter_delegate.ConverterDelegate;
import org.jetbrains.annotations.NotNull;
import structures.Node;
import structures.NodeType;
import structures.Token;

import java.util.Iterator;
import java.util.List;

public class Converter {

    private ConverterDelegate converterDelegate;

    public Converter(ConverterDelegate converterDelegate) {
        this.converterDelegate = converterDelegate;
    }

    private void checkNodeType(@NotNull Node node, NodeType expectedNodeType) throws ConvertException {
        if (!node.withNodeType(expectedNodeType)) {
            throw new ConvertException(node.getNodeType().toString(), expectedNodeType.toString()
                    + " expected instead of it");
        }
    }

    public String convert(@NotNull Node node) throws ConvertException {
        return query(node);
    }

    private String query(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.QUERY);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 6 && children.get(0).isTerminalWithToken(Token.SELECT)) {
            String select = terminal(childrenIterator.next(), Token.SELECT);
            String columnNames = columnNames(childrenIterator.next());
            String from = terminal(childrenIterator.next(), Token.FROM);
            String databaseName = terminal(childrenIterator.next(), Token.NAME);
            String wherePart = wherePart(childrenIterator.next());
            String skipLimitPart = skipLimitPart(childrenIterator.next());
            return converterDelegate.queryRule1(select, columnNames, from, databaseName, wherePart, skipLimitPart);
        }
        throw new ConvertException(NodeType.QUERY.toString(), "rule can't be matched");
    }

    private String columnNames(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.COLUMN_NAMES);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 1 && children.get(0).isTerminalWithToken(Token.STAR)) {
            String star = terminal(childrenIterator.next(), Token.STAR);
            return converterDelegate.columnNamesRule1(star);
        }
        if (children.size() == 2 && children.get(0).isTerminalWithToken(Token.NAME)) {
            String columnName = terminal(childrenIterator.next(), Token.NAME);
            String columnNamesCont = columnNamesCont(childrenIterator.next());
            return converterDelegate.columnNamesRule2(columnName, columnNamesCont);
        }
        throw new ConvertException(NodeType.COLUMN_NAMES.toString(), "rule can't be matched");
    }

    private String columnNamesCont(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.COLUMN_NAMES_CONT);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 3 && children.get(0).isTerminalWithToken(Token.COMMA)) {
            String comma = terminal(childrenIterator.next(), Token.COMMA);
            String columnName = terminal(childrenIterator.next(), Token.NAME);
            String columnNamesCont = columnNamesCont(childrenIterator.next());
            return converterDelegate.columnNamesContRule1(comma, columnName, columnNamesCont);
        }
        if (children.isEmpty()) {
            return converterDelegate.columnNamesContRule2();
        }
        throw new ConvertException(NodeType.COLUMN_NAMES_CONT.toString(), "rule can't be matched");
    }

    private String wherePart(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.WHERE_PART);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 2 && children.get(0).isTerminalWithToken(Token.WHERE)) {
            String where = terminal(childrenIterator.next(), Token.WHERE);
            String condition = condition(childrenIterator.next());
            return converterDelegate.wherePartRule1(where, condition);
        }
        if (children.isEmpty()) {
            return converterDelegate.wherePartRule2();
        }
        throw new ConvertException(NodeType.WHERE_PART.toString(), "rule can't be matched");
    }

    private String condition(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.CONDITION);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 3 && children.get(0).isTerminalWithToken(Token.NAME)) {
            String name = terminal(childrenIterator.next(), Token.NAME);
            String comparingOp = terminal(childrenIterator.next(), Token.COMPARING_OP);
            String fieldValue = fieldValue(childrenIterator.next());
            return converterDelegate.conditionRule1(name, comparingOp, fieldValue);
        }
        if (children.size() == 3 && children.get(0).withNodeType(NodeType.FIELD_VALUE)) {
            String fieldValue = fieldValue(childrenIterator.next());
            String comparingOperator = terminal(childrenIterator.next(), Token.COMPARING_OP);
            String name = terminal(childrenIterator.next(), Token.NAME);
            return converterDelegate.conditionRule2(fieldValue, comparingOperator, name);
        }
        throw new ConvertException(NodeType.CONDITION.toString(), "rule can't be matched");
    }

    private String fieldValue(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.FIELD_VALUE);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 1 && children.get(0).isTerminalWithToken(Token.STRING)) {
            String stringVal = terminal(childrenIterator.next(), Token.STRING);
            return converterDelegate.fieldValueRule1(stringVal);
        }
        if (children.size() == 1 && children.get(0).isTerminalWithToken(Token.NEG_INT)) {
            String negIntVal = terminal(childrenIterator.next(), Token.NEG_INT);
            return converterDelegate.fieldValueRule2(negIntVal);
        }
        if (children.size() == 1 && children.get(0).isTerminalWithToken(Token.POS_INT)) {
            String posIntVal = terminal(childrenIterator.next(), Token.POS_INT);
            return converterDelegate.fieldValueRule3(posIntVal);
        }
        throw new ConvertException(NodeType.FIELD_VALUE.toString(), "rule can't be matched");
    }

    private String skipLimitPart(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.SKIP_LIMIT_PART);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 2 && children.get(0).withNodeType(NodeType.ABS_SKIP_PART)) {
            String absSkipPart = absSkipPart(childrenIterator.next());
            String limitPart = limitPart(childrenIterator.next());
            return converterDelegate.skipLimitPartRule1(absSkipPart, limitPart);
        }
        if (children.size() == 2 && children.get(0).withNodeType(NodeType.ABS_LIMIT_PART)) {
            String absLimitPart = absLimitPart(childrenIterator.next());
            String skipPart = skipPart(childrenIterator.next());
            return converterDelegate.skipLimitPartRule2(absLimitPart, skipPart);
        }
        if (children.isEmpty()) {
            return converterDelegate.skipLimitPartRule3();
        }
        throw new ConvertException(NodeType.SKIP_LIMIT_PART.toString(), "rule can't be matched");
    }

    private String skipPart(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.SKIP_PART);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 1 && children.get(0).withNodeType(NodeType.ABS_SKIP_PART)) {
            String absSkipPart = absSkipPart(childrenIterator.next());
            return converterDelegate.skipPartRule1(absSkipPart);
        }
        if (children.isEmpty()) {
            return converterDelegate.skipPartRule2();
        }
        throw new ConvertException(NodeType.SKIP_PART.toString(), "rule can't be matched");
    }

    private String absSkipPart(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.ABS_SKIP_PART);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 2 && children.get(0).isTerminalWithToken(Token.SKIP)) {
            String skip = terminal(childrenIterator.next(), Token.SKIP);
            String posIntVal = terminal(childrenIterator.next(), Token.POS_INT);
            return converterDelegate.absSkipPartRule1(skip, posIntVal);
        }
        throw new ConvertException(NodeType.ABS_SKIP_PART.toString(), "rule can't be matched");
    }

    private String limitPart(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.LIMIT_PART);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 1 && children.get(0).withNodeType(NodeType.ABS_LIMIT_PART)) {
            String absLimitPart = absLimitPart(childrenIterator.next());
            return converterDelegate.limitPartRule1(absLimitPart);
        }
        if (children.isEmpty()) {
            return converterDelegate.limitPartRule2();
        }
        throw new ConvertException(NodeType.LIMIT_PART.toString(), "rule can't be matched");
    }

    private String absLimitPart(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.ABS_LIMIT_PART);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 2 && children.get(0).isTerminalWithToken(Token.LIMIT)) {
            String limit = terminal(childrenIterator.next(), Token.LIMIT);
            String posIntVal = terminal(childrenIterator.next(), Token.POS_INT);
            return converterDelegate.absLimitPartRule1(limit, posIntVal);
        }
        throw new ConvertException(NodeType.ABS_LIMIT_PART.toString(), "rule can't be matched");
    }

    private String terminal(@NotNull Node node, Token expectedToken) throws ConvertException {
        checkNodeType(node, NodeType.TERMINAL);
        if (node.isTerminalWithToken(expectedToken)) {
            return converterDelegate.terminalRepresentation(node.getToken(), node.getContent());
        }
        String terminal = NodeType.TERMINAL.toString();
        throw new ConvertException(terminal + " with " + node.getToken().toString(),
                terminal + " with " + expectedToken.toString() + " expected instead of it");
    }
}
