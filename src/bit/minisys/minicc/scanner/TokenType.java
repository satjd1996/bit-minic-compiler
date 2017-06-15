package bit.minisys.minicc.scanner;

/**
 * Created by satjd on 2017/6/15.
 */
public enum TokenType {

    TOKEN_TYPE_KEYWORD("keyword"),
    TOKEN_TYPE_INDENTIFIER("identifier"),
    TOKEN_TYPE_SEPRATOR("seprator"),
    TOKEN_TYPE_CONSTINT("const_i"),
    TOKEN_TYPE_CONSTCHAR("const_c"),
    TOKEN_TYPE_STRING("stringLiteral"),
    TOKEN_TYPE_OPERATOR("operator");

    private String typeName;

    TokenType(String name) {
        typeName = name;
    }


    @Override
    public String toString() {
        return typeName;
    }
}
