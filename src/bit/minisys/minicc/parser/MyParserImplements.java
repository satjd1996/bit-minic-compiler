package bit.minisys.minicc.parser;

import bit.minisys.minicc.scanner.Token;
import bit.minisys.minicc.scanner.TokenType;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by satjd on 2017/6/15.
 */
public class MyParserImplements implements IMiniCCParser {

    //XML writer
    BufferedWriter writer;

    //属性字列表
    List<Token> tokenList = new ArrayList<Token>();

    //当前指针
    int index = 0;

    //输出的语法树对应的XML
    Document tree = DocumentHelper.createDocument();
    //语法树根节点
    Element root = tree.addElement("root");
    //当前处理到的节点
    Element curEle = root;


    //用element的信息填充token
    private Token getToken(Element tokenElement) {
        String value = tokenElement.element("value").getText();

        TokenType type = TokenType.TOKEN_TYPE_CONSTCHAR;
        switch (tokenElement.element("type").getText()) {
            case "keyword":
                type = TokenType.TOKEN_TYPE_KEYWORD;
                break;
            case "identifier":
                type = TokenType.TOKEN_TYPE_INDENTIFIER;
                break;
            case "seprator":
                type = TokenType.TOKEN_TYPE_SEPRATOR;
                break;
            case "const_i":
                type = TokenType.TOKEN_TYPE_CONSTINT;
                break;
            case "const_c":
                type = TokenType.TOKEN_TYPE_CONSTCHAR;
                break;
            case "stringLiteral":
                type = TokenType.TOKEN_TYPE_STRING;
                break;
            case "operator":
                type = TokenType.TOKEN_TYPE_OPERATOR;
                break;
        }

        int number = Integer.parseInt(tokenElement.element("number").getText());
        int line = Integer.parseInt(tokenElement.element("line").getText());

        boolean isValid = Boolean.parseBoolean(tokenElement.element("valid").getText());

        Token t = new Token(number,value,type,line,isValid);

        return t;
    }

    private void error() {

        /*System.out.println("error!");
        System.exit(0);*/

        throw new RuntimeException();

    }

    private void writeXML() throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();

        XMLWriter write = new XMLWriter(writer,format);
        write.write(root);
        write.close();
    }

    private void program() throws IOException {
        if(index>=tokenList.size())
        {
            //TODO 写xml
           writeXML();
            return;
        }
        else {
            compileUnit();
            program();
        }
    }

    private void compileUnit() {
        curEle = curEle.addElement("compileUnit");
        typeDef();
        if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_INDENTIFIER) {

            curEle.addElement("ID").addText(tokenList.get(index).getValue());
            index++;
        }
        else error();
        postfix();
    }

    private void postfix() {
        curEle = curEle.addElement("postfix");
        if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_SEPRATOR&&tokenList.get(index).getValue().equals(";")) {
            curEle.addElement("SEPARATOR").addText(";");
            index++;
        }
        else if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_SEPRATOR&&tokenList.get(index).getValue().equals("(")) {
            curEle.addElement("SEPARATOR").addText("(");
            index++;
            params();
            if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_SEPRATOR&&tokenList.get(index).getValue().equals(")")) {
                curEle.addElement("SEPARATOR").addText(")");
                index++;
            }
            codeBlock();
        }
        else error();
        curEle = curEle.getParent();
    }

    private void params() {
        if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_KEYWORD) {
            curEle = curEle.addElement("params");
            typeDef();
            if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_INDENTIFIER) {

                curEle.addElement("ID").addText(tokenList.get(index).getValue());
                index++;
            }
            else error();
            remainParams();
            curEle = curEle.getParent();
        }

    }

    private void remainParams() {
        if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_SEPRATOR&&tokenList.get(index).getValue().equals(",")) {
            curEle.addElement("SEPARATOR").addText(",");
            index++;
            params();
        }
    }

    private void codeBlock() {
        curEle = curEle.addElement("codeBlock");
        if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_SEPRATOR&&tokenList.get(index).getValue().equals("{")) {
            curEle.addElement("SEPARATOR").addText("{");
            index++;
            statements();
            if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_SEPRATOR&&tokenList.get(index).getValue().equals("}")) {
                curEle.addElement("SEPARATOR").addText("}");
                index++;
            }
            else error();
        }
        else error();
        curEle = curEle.getParent();

    }

    private void statements() {
        curEle = curEle.addElement("statements");
        while(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_KEYWORD|| tokenList.get(index).getType()==TokenType.TOKEN_TYPE_INDENTIFIER) {
            statement();
        }
        curEle = curEle.getParent();
    }

    private void statement() {
        curEle = curEle.addElement("statement");
        if(tokenList.get(index).getValue().matches("int|char|float|void")) {
            localVariableStatement();
        }
        else if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_KEYWORD) {
            switch(tokenList.get(index).getValue()) {
                case "return":
                    returnStatement();
                    break;
                case "while":
                    loopStatement();
                    break;
                case "if":
                    branchStatement();
                    break;
            }
        }
        else if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_INDENTIFIER) {
            assignmentStatement();
        }
        else error();



        curEle = curEle.getParent();

    }

    private void returnStatement() {
        curEle.addElement("RETURN").addText("return");
        index++;
        expression();

        if(tokenList.get(index).getValue().equals(";")) {
            curEle.addElement("SEPARATOR").addText(tokenList.get(index).getValue());
            index++;
        }
        else error();

    }

    private void loopStatement() {
        curEle.addElement("WHILE").addText("while");
        index++;
        if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_SEPRATOR&&tokenList.get(index).getValue().equals("(")) {
            curEle.addElement("SEPARATOR").addText("(");
            index++;
            expression();
            if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_SEPRATOR&&tokenList.get(index).getValue().equals(")")) {
                curEle.addElement("SEPARATOR").addText(")");
                index++;
            }
            else error();
        }
        else error();
        codeBlock();
    }

    private void branchStatement() {
        curEle.addElement("IF").addText("if");
        index++;
        if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_SEPRATOR&&tokenList.get(index).getValue().equals("(")) {
            curEle.addElement("SEPARATOR").addText("(");
            index++;
            expression();
            if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_SEPRATOR&&tokenList.get(index).getValue().equals(")")) {
                curEle.addElement("SEPARATOR").addText(")");
                index++;
            }
            else error();

        }
        else error();
        codeBlock();
        otherBranch();


    }

    private void otherBranch() {
        if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_KEYWORD&&tokenList.get(index).getValue().equals("else")) {
            curEle.addElement("ELSE").addText("else");
            index++;
            codeBlock();
        }
    }

    private void assignmentStatement() {
        curEle.addElement("ID").addText(tokenList.get(index).getValue());
        index++;
        if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_OPERATOR&&tokenList.get(index).getValue().equals("=")) {
            curEle.addElement("OPERATOR").addText(tokenList.get(index).getValue());
            index++;
            expression();
        }
        else error();

        if(tokenList.get(index).getValue().equals(";")) {
            curEle.addElement("SEPARATOR").addText(tokenList.get(index).getValue());
            index++;
        }
        else error();
    }

    private void localVariableStatement() {
        typeDef();
        if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_INDENTIFIER)
        {
            curEle.addElement("ID").addText(tokenList.get(index).getValue());
            index++;
            if(tokenList.get(index).getValue().equals(";")) {
                curEle.addElement("SEPARATOR").addText(tokenList.get(index).getValue());
                index++;
            }
            else error();
        }
        else error();


    }

    private void expression() {
        curEle = curEle.addElement("expression");
        term();
        expression2();
        curEle = curEle.getParent();
    }

    private void term() {
        factor();
        term2();
    }

    private void expression2() {
        if(tokenList.get(index).getValue().matches("\\+|-")) {
            curEle.addElement("OPERATOR").addText(tokenList.get(index).getValue());
            index++;
            term();
            expression2();
        }
    }

    private void factor() {
        if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_INDENTIFIER) {
            curEle.addElement("ID").addText(tokenList.get(index).getValue());
            index++;
        }
        else if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_CONSTINT) {
            curEle.addElement("CONSTI").addText(tokenList.get(index).getValue());
            index++;
        }
        else if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_SEPRATOR&&tokenList.get(index).getValue().equals("(")) {
            curEle.addElement("SEPARATOR").addText("(");
            index++;
            expression();
            if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_SEPRATOR&&tokenList.get(index).getValue().equals(")")) {
                curEle.addElement("SEPARATOR").addText(")");
                index++;
            }
            else error();

        }
        else error();

    }

    private void term2() {
        if(tokenList.get(index).getValue().matches("\\*|/")) {
            curEle.addElement("OPERATOR").addText(tokenList.get(index).getValue());
            index++;
            factor();
            term2();
        }
    }

    private void typeDef() {
        if(tokenList.get(index).getType()==TokenType.TOKEN_TYPE_KEYWORD) {
            if(tokenList.get(index).getValue().matches("int|char|float|void")) {
                curEle.addElement("type").addText(tokenList.get(index).getValue());
                index++;
            }
        }
        else error();
    }

    @Override
    public void run(String iFile, String oFile) throws ParserConfigurationException, SAXException, IOException, DocumentException {

        writer = new BufferedWriter(new FileWriter(oFile));

        //读入XML，得到属性字流
        SAXReader reader = new SAXReader();
        Document d = reader.read(iFile);

        List<Element> tokenElements = d.selectNodes("//token");
        tokenList = new ArrayList<Token>();

        //elements -> tokenList
        tokenList.addAll(tokenElements.stream().map(this::getToken).collect(Collectors.toList()));


        System.out.println(tokenList.get(0));
        program();
    }

}
