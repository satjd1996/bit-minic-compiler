package bit.minisys.minicc.scanner;

import java.util.ArrayList;

/**
 * Created by satjd on 2017/5/1.
 */
public class KeywordVariables {
    private static ArrayList<String> keyword = new ArrayList<String>();
    static {
        keyword.add("int");
        keyword.add("char");
        keyword.add("const");
        keyword.add("struct");
        keyword.add("if");
        keyword.add("else");
        keyword.add("while");
        keyword.add("continue");
        keyword.add("break");
        keyword.add("goto");
        keyword.add("do");
        keyword.add("return");
    }

    public static boolean isKeyword(String word) {
        return keyword.contains(word);
    }
}
