package parser;

import org.jetbrains.annotations.NotNull;
import structures.Node;
import structures.NodeType;
import structures.Token;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private LexicalAnalyzer lexer;

    public Parser() {}

    @NotNull
    public Node parse(@NotNull String text) throws ParseException {
        lexer = new LexicalAnalyzer(text);
        return query();
    }

    @NotNull
    private Node query() throws ParseException {
        List<Node> children = new ArrayList<>();
        switch (lexer.getCurrentToken()) {
            case SELECT:
                children.add(terminal(Token.SELECT));
                children.add(column_names());
                children.add(terminal(Token.FROM));
                children.add(terminal(Token.NAME));
                children.add(where_part());
                children.add(skip_limit_part());
                break;
            default:
                throw new ParseException("Wrong first of query", lexer.getCurrentPos());
        }
        if (lexer.getCurrentToken() != Token.END) {
            throw new ParseException("Wrong follow of query", lexer.getCurrentPos());
        }
        return new Node(NodeType.QUERY, children);
    }

    @NotNull
    private Node column_names() throws ParseException {
        List<Node> children = new ArrayList<>();
        switch (lexer.getCurrentToken()) {
            case STAR:
                children.add(terminal(Token.STAR));
                break;
            case NAME:
                children.add(terminal(Token.NAME));
                children.add(column_names_cont());
                break;
            default:
                throw new ParseException("Wrong first of column_names", lexer.getCurrentPos());
        }
        if (lexer.getCurrentToken() != Token.FROM) {
            throw new ParseException("Wrong follow of column_names", lexer.getCurrentPos());
        }
        return new Node(NodeType.COLUMN_NAMES, children);
    }

    @NotNull
    private Node column_names_cont() throws ParseException {
        List<Node> children = new ArrayList<>();
        switch (lexer.getCurrentToken()) {
            case COMMA:
                children.add(terminal(Token.COMMA));
                children.add(terminal(Token.NAME));
                children.add(column_names_cont());
                break;
            case FROM:
                break;
            default:
                throw new ParseException("Wrong first of column_names_cont", lexer.getCurrentPos());
        }
        if (lexer.getCurrentToken() != Token.FROM) {
            throw new ParseException("Wrong follow of column_names_cont", lexer.getCurrentPos());
        }
        return new Node(NodeType.COLUMN_NAMES_CONT, children);
    }

    @NotNull
    private Node where_part() throws ParseException {
        List<Node> children = new ArrayList<>();
        switch (lexer.getCurrentToken()) {
            case WHERE:
                children.add(terminal(Token.WHERE));
                children.add(condition());
                break;
            case SKIP:
            case LIMIT:
            case END:
                break;
            default:
                throw new ParseException("Wrong first of where_part", lexer.getCurrentPos());
        }
        if (lexer.getCurrentToken() != Token.SKIP && lexer.getCurrentToken() != Token.LIMIT
                && lexer.getCurrentToken() != Token.END) {
            throw new ParseException("Wrong follow of where_part", lexer.getCurrentPos());
        }
        return new Node(NodeType.WHERE_PART, children);
    }

    @NotNull
    private Node condition() throws ParseException {
        List<Node> children = new ArrayList<>();
        switch (lexer.getCurrentToken()) {
            case NAME:
                children.add(terminal(Token.NAME));
                children.add(terminal(Token.COMPARING_OP));
                children.add(field_value());
                break;
            case STRING:
            case NEG_INT:
            case POS_INT:
                children.add(field_value());
                children.add(terminal(Token.COMPARING_OP));
                children.add(terminal(Token.NAME));
                break;
            default:
                throw new ParseException("Wrong first of condition", lexer.getCurrentPos());
        }
        if (lexer.getCurrentToken() != Token.SKIP && lexer.getCurrentToken() != Token.LIMIT
                && lexer.getCurrentToken() != Token.END) {
            throw new ParseException("Wrong follow of condition", lexer.getCurrentPos());
        }
        return new Node(NodeType.CONDITION, children);
    }

    @NotNull
    private Node field_value() throws ParseException {
        List<Node> children = new ArrayList<>();
        switch (lexer.getCurrentToken()) {
            case STRING:
                children.add(terminal(Token.STRING));
                break;
            case NEG_INT:
                children.add(terminal(Token.NEG_INT));
                break;
            case POS_INT:
                children.add(terminal(Token.POS_INT));
                break;
            default:
                throw new ParseException("Wrong first of field_value", lexer.getCurrentPos());
        }
        if (lexer.getCurrentToken() != Token.SKIP && lexer.getCurrentToken() != Token.LIMIT
                && lexer.getCurrentToken() != Token.COMPARING_OP && lexer.getCurrentToken() != Token.END) {
            throw new ParseException("Wrong follow of field_value", lexer.getCurrentPos());
        }
        return new Node(NodeType.FIELD_VALUE, children);
    }

    @NotNull
    private Node skip_limit_part() throws ParseException {
        List<Node> children = new ArrayList<>();
        switch (lexer.getCurrentToken()) {
            case SKIP:
                children.add(abs_skip_part());
                children.add(limit_part());
                break;
            case LIMIT:
                children.add(abs_limit_part());
                children.add(skip_part());
                break;
            case END:
                break;
            default:
                throw new ParseException("Wrong first of skip_limit_part", lexer.getCurrentPos());
        }
        if (lexer.getCurrentToken() != Token.END) {
            throw new ParseException("Wrong follow of skip_limit_part", lexer.getCurrentPos());
        }
        return new Node(NodeType.SKIP_LIMIT_PART, children);
    }

    @NotNull
    private Node skip_part() throws ParseException {
        List<Node> children = new ArrayList<>();
        switch (lexer.getCurrentToken()) {
            case SKIP:
                children.add(abs_skip_part());
                break;
            case END:
                break;
            default:
                throw new ParseException("Wrong first of skip_part", lexer.getCurrentPos());
        }
        if (lexer.getCurrentToken() != Token.END) {
            throw new ParseException("Wrong follow of skip_part", lexer.getCurrentPos());
        }
        return new Node(NodeType.SKIP_PART, children);
    }

    @NotNull
    private Node abs_skip_part() throws ParseException {
        List<Node> children = new ArrayList<>();
        switch (lexer.getCurrentToken()) {
            case SKIP:
                children.add(terminal(Token.SKIP));
                children.add(terminal(Token.POS_INT));
                break;
            default:
                throw new ParseException("Wrong first of abs_skip_part", lexer.getCurrentPos());
        }
        if (lexer.getCurrentToken() != Token.LIMIT && lexer.getCurrentToken() != Token.END) {
            throw new ParseException("Wrong follow of abs_skip_part", lexer.getCurrentPos());
        }
        return new Node(NodeType.ABS_SKIP_PART, children);
    }

    @NotNull
    private Node limit_part() throws ParseException {
        List<Node> children = new ArrayList<>();
        switch (lexer.getCurrentToken()) {
            case LIMIT:
                children.add(abs_limit_part());
                break;
            case END:
                break;
            default:
                throw new ParseException("Wrong first of limit_part", lexer.getCurrentPos());
        }
        if (lexer.getCurrentToken() != Token.END) {
            throw new ParseException("Wrong follow of limit_part", lexer.getCurrentPos());
        }
        return new Node(NodeType.LIMIT_PART, children);
    }

    @NotNull
    private Node abs_limit_part() throws ParseException {
        List<Node> children = new ArrayList<>();
        switch (lexer.getCurrentToken()) {
            case LIMIT:
                children.add(terminal(Token.LIMIT));
                children.add(terminal(Token.POS_INT));
                break;
            default:
                throw new ParseException("Wrong first of abs_limit_part", lexer.getCurrentPos());
        }
        if (lexer.getCurrentToken() != Token.SKIP && lexer.getCurrentToken() != Token.END) {
            throw new ParseException("Wrong follow of abs_limit_part", lexer.getCurrentPos());
        }
        return new Node(NodeType.ABS_LIMIT_PART, children);
    }

    @NotNull
    private Node terminal(@NotNull Token expectedToken) throws ParseException {
        if (lexer.getCurrentToken() == expectedToken) {
            Node terminalNode = new Node(NodeType.TERMINAL, expectedToken, lexer.getCurrentTokenString());
            lexer.nextToken();
            return terminalNode;
        } else {
            throw new ParseException(expectedToken.toString() + " expected instead of " + lexer.getCurrentToken().toString(), 0);
        }
    }
}
