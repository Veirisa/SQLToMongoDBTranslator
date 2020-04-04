package structures;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private NodeType nodeType;
    private List<Node> children;
    private Token token;
    private String content;

    public Node(@NotNull NodeType nodeType, @NotNull List<@NotNull Node> children) {
        this.nodeType = nodeType;
        this.children = children;
        this.token = Token.UNDEFINED;
        this.content = "";
    }

    public Node(@NotNull NodeType nodeType, @NotNull Token token, @NotNull String content) {
        this.nodeType = nodeType;
        this.children = new ArrayList<>();
        this.token = token;
        this.content = content;
    }

    @NotNull
    public NodeType getNodeType() {
        return nodeType;
    }

    @NotNull
    public List<@NotNull Node> getChildren() {
        return children;
    }

    @NotNull
    public Token getToken() {
        return token;
    }

    @NotNull
    public String getContent() {
        return content;
    }

    public boolean withNodeType(@NotNull NodeType expectedNodeType) {
        return nodeType == expectedNodeType;
    }

    public boolean isTerminalWithToken(@NotNull Token expectedToken) {
        return nodeType == NodeType.TERMINAL && token == expectedToken;
    }
}
