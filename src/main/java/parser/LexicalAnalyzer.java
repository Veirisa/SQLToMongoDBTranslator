package parser;

import org.jetbrains.annotations.NotNull;
import structures.Token;

import java.util.HashMap;
import java.util.Map;

public class LexicalAnalyzer {

    private String text;
    private Token currentToken;
    private String currentTokenString;
    private int currentPos;
    private int currentSymbol;

    private final Map<String, Token> keywordTokens  = new HashMap<String, Token>() {{
        put("SELECT", Token.SELECT);
        put("FROM", Token.FROM);
        put("WHERE", Token.WHERE);
        put("LIMIT", Token.LIMIT);
        put("SKIP", Token.SKIP);
    }};

    public LexicalAnalyzer(@NotNull String text) throws ParseException {
        this.text = text;
        currentPos = -1;
        nextSymbol();
        nextToken();
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public Token getCurrentToken() {
        return currentToken;
    }

    public String getCurrentTokenString() {
        return currentTokenString;
    }

    private void nextSymbol() throws ParseException {
        ++currentPos;
        if (currentPos < text.length()) {
            currentSymbol = text.charAt(currentPos);
            return;
        }
        if (currentPos == text.length()) {
            currentSymbol = -1;
            return;
        }
        throw new ParseException("Out of range");
    }

    private boolean isBlank() {
        return Character.isWhitespace(currentSymbol);
    }

    private boolean isEnd() {
        return currentSymbol == -1;
    }

    private boolean isStar() {
        return currentSymbol == '*';
    }

    private boolean isComma() {
        return currentSymbol == ',';
    }

    private boolean isComparingOperator() {
        return currentSymbol == '=' || currentSymbol == '<' || currentSymbol == '>';
    }

    private boolean isDash() {
        return currentSymbol == '-';
    }

    private boolean isDigit() {
        return Character.isDigit(currentSymbol);
    }

    private boolean isDoubleQuote() {
        return currentSymbol == '"';
    }

    private boolean isFirstNameCharacter() {
        return Character.isLetter(currentSymbol) || currentSymbol == '_';
    }

    private boolean isNameCharacter() {
        return isFirstNameCharacter() || isDigit();
    }

    @NotNull
    private String tryGetNumber() throws ParseException {
        StringBuilder sb = new StringBuilder();
        if (isDash()) {
            sb.append((char)currentSymbol);
            nextSymbol();
        }
        while (isDigit()) {
            sb.append((char)currentSymbol);
            nextSymbol();
        }
        if (sb.length() == 0 || isFirstNameCharacter()) {
            throw new ParseException("Illegal token", currentPos);
        }
        return sb.toString();
    }

    @NotNull
    private String tryGetStringInDoubleQuotes() throws ParseException {
        StringBuilder sb = new StringBuilder();
        if (isDoubleQuote()) {
            sb.append((char)currentSymbol);
            nextSymbol();
        } else {
            throw new ParseException("Illegal token", currentPos);
        }
        while (!isEnd() && !isDoubleQuote()) {
            sb.append((char)currentSymbol);
            nextSymbol();
        }
        if (isDoubleQuote()) {
            sb.append((char)currentSymbol);
            nextSymbol();
        } else {
            throw new ParseException("Illegal token", currentPos);
        }
        return sb.toString();
    }

    @NotNull
    private String tryGetNameString() throws ParseException {
        StringBuilder sb = new StringBuilder();
        if (isFirstNameCharacter()) {
            sb.append((char)currentSymbol);
            nextSymbol();
        } else {
            throw new ParseException("Illegal token", currentPos);
        }
        while (isNameCharacter()) {
            sb.append((char)currentSymbol);
            nextSymbol();
        }
        return sb.toString();
    }

    public void nextToken() throws ParseException {
        while (isBlank()) {
            nextSymbol();
        }
        if (isEnd()) {
            currentTokenString = "";
            currentToken = Token.END;
            return;
        }
        if (isStar()) {
            nextSymbol();
            currentTokenString = "*";
            currentToken = Token.STAR;
            return;
        }
        if (isComma()) {
            nextSymbol();
            currentTokenString = ",";
            currentToken = Token.COMMA;
            return;
        }
        if (isComparingOperator()) {
            int saveSymbol = currentSymbol;
            nextSymbol();
            if (saveSymbol == '<' && currentSymbol == '>') {
                nextSymbol();
                currentTokenString = "<>";
            } else {
                currentTokenString = Character.toString((char)saveSymbol);
            }
            currentToken = Token.COMPARING_OP;
            return;
        }
        if (isDoubleQuote()) {
            currentTokenString = tryGetStringInDoubleQuotes();
            currentToken = Token.STRING;
            return;
        }
        if (isDash()) {
            currentTokenString = tryGetNumber();
            currentToken = Token.NEG_INT;
            return;
        }
        if (isDigit()) {
            currentTokenString = tryGetNumber();
            currentToken = Token.POS_INT;
            return;
        }
        if (isFirstNameCharacter()) {
            currentTokenString = tryGetNameString();
            Token keywordToken = keywordTokens.get(currentTokenString.toUpperCase());
            if (keywordToken != null) {
                currentToken = keywordToken;
                return;
            }
            currentToken = Token.NAME;
            return;
        }
        throw new ParseException("Illegal symbol '" + (char)currentSymbol +  "'", currentPos);
    }
}
