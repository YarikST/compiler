package Parse;

import Vocabulary.BaseType;
import Vocabulary.Lex;

import java.io.*;

/**
 * Created by admin-iorigins on 14.03.17.
 */
public abstract class Node {
    public static BufferedWriter writer;
    private static int lable = 1;

    private int line;

    static {
        try {
            writer = new BufferedWriter(new FileWriter("/home/iorigins/Стільниця/компілер/code/mainOut.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Node() {
        line = Lex.line;
    }

    public abstract void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException;

    public  void error(String ob) {
        try {
            writer.write("error-> "+ob+"<line:"+line+">"+'\n');
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void out(String s) {
        try {
            writer.write(s + '\n');
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int newLable() {
        return lable++;
    }

    public void lable(int lable) {
        out(lableStr() + lable+":");
    }

    public String lableStr() {
        return "L";
    }
}
