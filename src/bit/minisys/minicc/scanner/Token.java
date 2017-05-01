package bit.minisys.minicc.scanner;

import org.dom4j.Element;

/**
 * Created by satjd on 2017/4/29.
 */
enum TokenType {

    TOKEN_TYPE_KEYWORD("keyword"),
    TOKEN_TYPE_INDENTIFIER("identifier"),
    TOKEN_TYPE_SEPRATOR("seprator"),
    TOKEN_TYPE_CONSTINT("const_i"),
    TOKEN_TYPE_CONSTCHAR("const_c"),
    TOKEN_TYPE_STRING("stringLiteral"),
    TOKEN_TYPE_OPERATOR("operator");

    private String typeName;

    private TokenType(String name) {
        typeName = name;
    }


    @Override
    public String toString() {
        return typeName;
    }
}

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
}
