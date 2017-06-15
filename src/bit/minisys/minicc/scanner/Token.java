package bit.minisys.minicc.scanner;

import org.dom4j.Element;

/**
 * Created by satjd on 2017/4/29.
 */


public class Token {
    private int number;
    private String value;
    private TokenType type;
    private int line;
    private boolean valid;

    public Token(int number, String value, TokenType type, int line, boolean valid) {
        this.number = number;
        this.value = value;
        this.type = type;
        this.line = line;
        this.valid = valid;
    }

    public void writeXML(Element root)
    {
        Element token = root.addElement("token");
        token.addElement("number").addText(number+"");
        token.addElement("value").addText(value);
        token.addElement("type").addText(type.toString());
        token.addElement("line").addText(line+"");
        token.addElement("valid").addText(valid+"");

    }

    public int getNumber() {
        return number;
    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public boolean isValid() {
        return valid;
    }
}
