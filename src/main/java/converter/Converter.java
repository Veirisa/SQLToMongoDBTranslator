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
            String column_names = column_names(childrenIterator.next());
            String from = terminal(childrenIterator.next(), Token.FROM);
            String database_name = terminal(childrenIterator.next(), Token.NAME);
            String where_part = where_part(childrenIterator.next());
            String skip_limit_part = skip_limit_part(childrenIterator.next());
            return converterDelegate.query_rule1(select, column_names, from, database_name, where_part, skip_limit_part);
        }
        throw new ConvertException(NodeType.QUERY.toString(), "rule can't be matched");
    }

    private String column_names(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.COLUMN_NAMES);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 1 && children.get(0).isTerminalWithToken(Token.STAR)) {
            String star = terminal(childrenIterator.next(), Token.STAR);
            return converterDelegate.columns_names_rule1(star);
        }
        if (children.size() == 2 && children.get(0).isTerminalWithToken(Token.NAME)) {
            String column_name = terminal(childrenIterator.next(), Token.NAME);
            String column_names_cont = column_names_cont(childrenIterator.next());
            return converterDelegate.columns_names_rule2(column_name, column_names_cont);
        }
        throw new ConvertException(NodeType.COLUMN_NAMES.toString(), "rule can't be matched");
    }

    private String column_names_cont(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.COLUMN_NAMES_CONT);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 3 && children.get(0).isTerminalWithToken(Token.COMMA)) {
            String comma = terminal(childrenIterator.next(), Token.COMMA);
            String column_name = terminal(childrenIterator.next(), Token.NAME);
            String column_names_cont = column_names_cont(childrenIterator.next());
            return converterDelegate.columns_names_cont_rule1(comma, column_name, column_names_cont);
        }
        if (children.isEmpty()) {
            return converterDelegate.columns_names_cont_rule2();
        }
        throw new ConvertException(NodeType.COLUMN_NAMES_CONT.toString(), "rule can't be matched");
    }

    private String where_part(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.WHERE_PART);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 2 && children.get(0).isTerminalWithToken(Token.WHERE)) {
            String where = terminal(childrenIterator.next(), Token.WHERE);
            String condition = condition(childrenIterator.next());
            return converterDelegate.where_part_rule1(where, condition);
        }
        if (children.isEmpty()) {
            return converterDelegate.where_part_rule2();
        }
        throw new ConvertException(NodeType.WHERE_PART.toString(), "rule can't be matched");
    }

    private String condition(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.CONDITION);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 3 && children.get(0).isTerminalWithToken(Token.NAME)) {
            String name = terminal(childrenIterator.next(), Token.NAME);
            String comparing_op = terminal(childrenIterator.next(), Token.COMPARING_OP);
            String field_value = field_value(childrenIterator.next());
            return converterDelegate.condition_rule1(name, comparing_op, field_value);
        }
        if (children.size() == 3 && children.get(0).withNodeType(NodeType.FIELD_VALUE)) {
            String field_value = field_value(childrenIterator.next());
            String comparing_operator = terminal(childrenIterator.next(), Token.COMPARING_OP);
            String name = terminal(childrenIterator.next(), Token.NAME);
            return converterDelegate.condition_rule2(field_value, comparing_operator, name);
        }
        throw new ConvertException(NodeType.CONDITION.toString(), "rule can't be matched");
    }

    private String field_value(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.FIELD_VALUE);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 1 && children.get(0).isTerminalWithToken(Token.STRING)) {
            String string_val = terminal(childrenIterator.next(), Token.STRING);
            return converterDelegate.field_value_rule1(string_val);
        }
        if (children.size() == 1 && children.get(0).isTerminalWithToken(Token.NEG_INT)) {
            String neg_int_val = terminal(childrenIterator.next(), Token.NEG_INT);
            return converterDelegate.field_value_rule2(neg_int_val);
        }
        if (children.size() == 1 && children.get(0).isTerminalWithToken(Token.POS_INT)) {
            String pos_int_val = terminal(childrenIterator.next(), Token.POS_INT);
            return converterDelegate.field_value_rule3(pos_int_val);
        }
        throw new ConvertException(NodeType.FIELD_VALUE.toString(), "rule can't be matched");
    }

    private String skip_limit_part(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.SKIP_LIMIT_PART);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 2 && children.get(0).withNodeType(NodeType.ABS_SKIP_PART)) {
            String abs_skip_part = abs_skip_part(childrenIterator.next());
            String limit_part = limit_part(childrenIterator.next());
            return converterDelegate.skip_limit_part_rule1(abs_skip_part, limit_part);
        }
        if (children.size() == 2 && children.get(0).withNodeType(NodeType.ABS_LIMIT_PART)) {
            String abs_limit_part = abs_limit_part(childrenIterator.next());
            String skip_part = skip_part(childrenIterator.next());
            return converterDelegate.skip_limit_part_rule2(abs_limit_part, skip_part);
        }
        if (children.isEmpty()) {
            return converterDelegate.skip_limit_part_rule3();
        }
        throw new ConvertException(NodeType.SKIP_LIMIT_PART.toString(), "rule can't be matched");
    }

    private String skip_part(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.SKIP_PART);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 1 && children.get(0).withNodeType(NodeType.ABS_SKIP_PART)) {
            String abs_skip_part = abs_skip_part(childrenIterator.next());
            return converterDelegate.skip_part_rule1(abs_skip_part);
        }
        if (children.isEmpty()) {
            return converterDelegate.skip_part_rule2();
        }
        throw new ConvertException(NodeType.SKIP_PART.toString(), "rule can't be matched");
    }

    private String abs_skip_part(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.ABS_SKIP_PART);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 2 && children.get(0).isTerminalWithToken(Token.SKIP)) {
            String skip = terminal(childrenIterator.next(), Token.SKIP);
            String pos_int_val = terminal(childrenIterator.next(), Token.POS_INT);
            return converterDelegate.abs_skip_part_rule1(skip, pos_int_val);
        }
        throw new ConvertException(NodeType.ABS_SKIP_PART.toString(), "rule can't be matched");
    }

    private String limit_part(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.LIMIT_PART);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 1 && children.get(0).withNodeType(NodeType.ABS_LIMIT_PART)) {
            String abs_limit_part = abs_limit_part(childrenIterator.next());
            return converterDelegate.limit_part_rule1(abs_limit_part);
        }
        if (children.isEmpty()) {
            return converterDelegate.limit_part_rule2();
        }
        throw new ConvertException(NodeType.LIMIT_PART.toString(), "rule can't be matched");
    }

    private String abs_limit_part(@NotNull Node node) throws ConvertException {
        checkNodeType(node, NodeType.ABS_LIMIT_PART);
        List<@NotNull Node> children = node.getChildren();
        Iterator<@NotNull Node> childrenIterator = children.iterator();
        if (children.size() == 2 && children.get(0).isTerminalWithToken(Token.LIMIT)) {
            String limit = terminal(childrenIterator.next(), Token.LIMIT);
            String pos_int_val = terminal(childrenIterator.next(), Token.POS_INT);
            return converterDelegate.abs_limit_part_rule1(limit, pos_int_val);
        }
        throw new ConvertException(NodeType.ABS_LIMIT_PART.toString(), "rule can't be matched");
    }

    private String terminal(@NotNull Node node, Token expectedToken) throws ConvertException {
        checkNodeType(node, NodeType.TERMINAL);
        if (node.isTerminalWithToken(expectedToken)) {
            return converterDelegate.terminal_representation(node.getToken(), node.getContent());
        }
        String terminal = NodeType.TERMINAL.toString();
        throw new ConvertException(terminal + " with " + node.getToken().toString(),
                terminal + " with " + expectedToken.toString() + " expected instead of it");
    }
}
