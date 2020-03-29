package structures;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Node {

    private NodeType nodeType;
    private List<@NotNull Node> children;
    private Token token;
    private String content;

    public Node(NodeType nodeType, List<@NotNull Node> children) {
        this.nodeType = nodeType;
        this.children = children;
    }

    public Node(NodeType nodeType, Token token, String content) {
        this.nodeType = nodeType;
        this.token = token;
        this.content = content;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Token getToken() {
        return token;
    }

    public String getContent() {
        return content;
    }

    public boolean withNodeType(NodeType expectedNodeType) {
        return nodeType == expectedNodeType;
    }

    public boolean isTerminalWithToken(Token expectedToken) {
        return nodeType == NodeType.TERMINAL && token == expectedToken;
    }
}
