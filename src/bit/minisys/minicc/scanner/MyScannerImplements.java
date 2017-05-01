package bit.minisys.minicc.scanner;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

import java.io.*;

/**
 * Created by satjd on 2017/4/29.
 */
public class MyScannerImplements implements  IMiniCCScanner {

    private BufferedReader reader;
    private BufferedWriter writer;

    private Document outputXML;

    private int lineNum = 0;
    private int wordNum = 0;

    private void scan() throws FileNotFoundException {

        String nextLine;


        try {
            while((nextLine = reader.readLine())!=null)
            {
                lineNum++;
                int indexOfLine = 0;
                nextLine = nextLine.concat("#");


                while(indexOfLine<nextLine.length() - 1) {
                    int endIndex = indexOfLine;
                    int beginIndex = indexOfLine;

                    //TODO dfa

                    //indentifier
                    if(nextLine.substring(endIndex,endIndex+1).matches("[a-z]|[A-Z]|_")) {
                        endIndex++;
                        while(nextLine.substring(endIndex,endIndex+1).matches("[a-z]|[A-Z]|_|[0-9]")) {
                            endIndex++;
                        }
                        String dst = nextLine.substring(beginIndex,endIndex).trim();
                        if(KeywordVariables.isKeyword(dst)) {
                            Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_KEYWORD,lineNum,true);
                            t.writeXML(outputXML.getRootElement());
                        }
                        else {
                            Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_INDENTIFIER,lineNum,true);
                            t.writeXML(outputXML.getRootElement());
                        }

                    }
                    //const_i
                    else if(nextLine.substring(endIndex,endIndex+1).matches("[1-9]")) {
                        endIndex++;
                        while(nextLine.substring(endIndex,endIndex+1).matches("[0-9]")) {
                            endIndex++;
                        }
                        //decimal integer
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_CONSTINT,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    else if(nextLine.substring(endIndex,endIndex+1).matches("0")) {
                        endIndex++;
                        if(nextLine.substring(endIndex,endIndex+1).matches("x|X")) {
                            endIndex++;
                            while(nextLine.substring(endIndex,endIndex+1).matches("[0-9]|[a-f]|[A-F]")) {
                                endIndex++;
                            }
                            //hexadecimal integer
                            Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_CONSTINT,lineNum,true);
                            t.writeXML(outputXML.getRootElement());
                        }
                        else {
                            while(nextLine.substring(endIndex,endIndex+1).matches("[0-7]")) {
                                endIndex++;
                            }
                            //octal integer
                            Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_CONSTINT,lineNum,true);
                            t.writeXML(outputXML.getRootElement());
                        }
                    }
                    //const_c
                    else if(nextLine.substring(endIndex,endIndex+1).matches("'")) {
                        endIndex++;
                        if(nextLine.substring(endIndex,endIndex+1).matches("\\\\")) {
                            endIndex++;
                            if(nextLine.substring(endIndex,endIndex+1).matches("\\\\|\'|\"|a|b|f|n|t|v|[?]")) {
                                endIndex++;
                            }
                            if(nextLine.substring(endIndex,endIndex+1).matches("'")) {
                                endIndex++;
                            }
                            Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_CONSTCHAR,lineNum,true);
                            t.writeXML(outputXML.getRootElement());
                        }
                        else {
                            endIndex++;
                            if(nextLine.substring(endIndex,endIndex+1).matches("'")) {
                                endIndex++;
                            }
                            Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_CONSTCHAR,lineNum,true);
                            t.writeXML(outputXML.getRootElement());
                        }
                    }
                    //string-literal
                    else if(nextLine.substring(endIndex,endIndex+1).matches("\"")) {
                        endIndex++;
                        while (true) {
                            if(nextLine.substring(endIndex,endIndex+1).matches("[^\"\\\\\n]")) {
                                endIndex++;
                            }
                            if(nextLine.substring(endIndex,endIndex+1).matches("\\\\")) {
                                endIndex++;
                                if(nextLine.substring(endIndex,endIndex+1).matches("\\\\|\'|\"|a|b|f|n|t|v|[?]")) {
                                    endIndex++;
                                }
                            }
                            if(nextLine.substring(endIndex,endIndex+1).matches("\"")) {
                                endIndex++;
                                break;
                            }
                        }
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_STRING,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    // =
                    else if(nextLine.substring(endIndex,endIndex+1).matches("=")) {
                        endIndex++;
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_OPERATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    //+ ++ +=
                    else if(nextLine.substring(endIndex,endIndex+1).matches("\\+")) {
                        endIndex++;
                        if(nextLine.substring(endIndex,endIndex+1).matches("\\+|=")) {
                            endIndex++;

                        }
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_OPERATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    //- -- -=
                    else if(nextLine.substring(endIndex,endIndex+1).matches("-")) {
                        endIndex++;
                        if(nextLine.substring(endIndex,endIndex+1).matches("-|=")) {
                            endIndex++;

                        }
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_OPERATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    //* *=
                    else if(nextLine.substring(endIndex,endIndex+1).matches("\\*")) {
                        endIndex++;
                        if(nextLine.substring(endIndex,endIndex+1).matches("=")) {
                            endIndex++;

                        }
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_OPERATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    // / /=
                    //TODO fix bugs in pp.jar to support /
                    else if(nextLine.substring(endIndex,endIndex+1).matches("/")) {
                        endIndex++;
                        if(nextLine.substring(endIndex,endIndex+1).matches("=")) {
                            endIndex++;
                        }
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_OPERATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    //% %=
                    //TODO fix bugs in pp.jar to support %
                    else if(nextLine.substring(endIndex,endIndex+1).matches("%")) {
                        endIndex++;
                        if(nextLine.substring(endIndex,endIndex+1).matches("=")) {
                            endIndex++;

                        }
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_OPERATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    //> >> >=
                    else if(nextLine.substring(endIndex,endIndex+1).matches(">")) {
                        endIndex++;
                        if(nextLine.substring(endIndex,endIndex+1).matches(">|=")) {
                            endIndex++;

                        }
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_OPERATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    //< << <=
                    else if(nextLine.substring(endIndex,endIndex+1).matches("<")) {
                        endIndex++;
                        if(nextLine.substring(endIndex,endIndex+1).matches("<|=")) {
                            endIndex++;

                        }
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_OPERATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    //& &&
                    else if(nextLine.substring(endIndex,endIndex+1).matches("&")) {
                        endIndex++;
                        if(nextLine.substring(endIndex,endIndex+1).matches("&")) {
                            endIndex++;

                        }
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_OPERATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    //| ||
                    else if(nextLine.substring(endIndex,endIndex+1).matches("\\|")) {
                        endIndex++;
                        if(nextLine.substring(endIndex,endIndex+1).matches("\\|")) {
                            endIndex++;

                        }
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_OPERATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    // !
                    else if(nextLine.substring(endIndex,endIndex+1).matches("!")) {
                        endIndex++;
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_OPERATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    // ~
                    else if(nextLine.substring(endIndex,endIndex+1).matches("~")) {
                        endIndex++;
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_OPERATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    // ? :
                    else if(nextLine.substring(endIndex,endIndex+1).matches("[?]")) {
                        endIndex++;
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_OPERATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    else if(nextLine.substring(endIndex,endIndex+1).matches(":")) {
                        endIndex++;
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_OPERATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    // ,
                    else if(nextLine.substring(endIndex,endIndex+1).matches(",")) {
                        endIndex++;
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_SEPRATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    // {
                    else if(nextLine.substring(endIndex,endIndex+1).matches("\\{")) {
                        endIndex++;
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_SEPRATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    // }
                    else if(nextLine.substring(endIndex,endIndex+1).matches("\\}")) {
                        endIndex++;
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_SEPRATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    // [
                    else if(nextLine.substring(endIndex,endIndex+1).matches("\\[")) {
                        endIndex++;
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_SEPRATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    // ]
                    else if(nextLine.substring(endIndex,endIndex+1).matches("\\]")) {
                        endIndex++;
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_SEPRATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    // (
                    else if(nextLine.substring(endIndex,endIndex+1).matches("\\(")) {
                        endIndex++;
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_SEPRATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    // )
                    else if(nextLine.substring(endIndex,endIndex+1).matches("\\)")) {
                        endIndex++;
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_SEPRATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    // ;
                    else if(nextLine.substring(endIndex,endIndex+1).matches(";")) {
                        endIndex++;
                        Token t = new Token(wordNum++,nextLine.substring(beginIndex,endIndex).trim(),TokenType.TOKEN_TYPE_SEPRATOR,lineNum,true);
                        t.writeXML(outputXML.getRootElement());
                    }
                    else endIndex++;
                    indexOfLine = endIndex;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initXML()
    {
        outputXML = DocumentHelper.createDocument();
        Element root = outputXML.addElement("project");
        root.addAttribute("name","test.l");
    }

    private void writeXML()
    {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter write = new XMLWriter(writer,format);
            write.write(outputXML);
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String iFile, String oFile) throws Exception {
        //TODO scanner implements
        reader = new BufferedReader(new FileReader(iFile));
        writer = new BufferedWriter(new FileWriter(oFile));
        initXML();
        scan();
        writeXML();


    }

    @Test
    public void test1()
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("test2.c"));
            String o = reader.readLine();
            System.out.println(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
