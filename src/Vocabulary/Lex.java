package Vocabulary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by admin-iorigins on 07.03.17.
 */
public class Lex {

    public static int line;

    private BufferedReader reader;

    private char e=' ';

    private int isComent;

    private HashMap<String, Token> map;

    public Lex(InputStream inputStream) {
        reader = new BufferedReader(new InputStreamReader(inputStream),1024);
        map = new HashMap<>();

        map.put("true",Word.TRUE);
        map.put("false",Word.FALSE);
        map.put("null", Word.NULL);
        map.put("if",Word.IF);
        map.put("else",Word.ELSE);
        map.put("while",Word.WHILE);
        map.put("break",Word.BREAK);
        map.put("return",Word.RETURN);
        map.put("int", BaseType.INT);
        map.put("real", BaseType.REAL);
        map.put("boo", BaseType.BOO);
        map.put("char", BaseType.CHAR);
        map.put("void", BaseType.VOID);
        map.put("++", Word.Increment);
        map.put("--", Word.Decrement);
        map.put(">=", Word.Less_Exactly);
        map.put("<=", Word.More_Exactly);
        map.put("!=", Word.Not_Exactly);
        map.put("==", Word.Exactly);
        map.put("&&", Word.And);
        map.put("||", Word.Or);
    }

    public Token getToken() {
        for (;;read()) {
            if (e == '/') {
                beforeWrite();
                read();
                if (e == '*') {
                    isComent++;
                    e = ' ';
                    continue;
                }
                write();
                e = '/';
            } else if (e == '*') {
                beforeWrite();
                 read();
                if (e == '/') {
                    isComent--;
                    e  = ' ';
                    continue;
                }
                write();
                e = '*';
            }

            if (e == '\n') {
                line++;
                continue;
            }

            if (isComent > 0) {
                continue;
            }
            if (e == ' '||e=='\n'||e=='\t') {
                e = ' ';
                continue;
            }
            break;
        }

        if (Character.isDigit(e)) {
            int v = 0;

            while (Character.isDigit(e)) {
                v = v * 10 + Character.digit(e, 10);
                read();
            }

            if (e != TypeToken.Point) {
                return new Integer(v);
            }

            float vv = v;
            float d = 10;
            read();
            while (Character.isDigit(e)) {
                vv = vv + Character.digit(e, 10)/d;
                d *= 10;
                read();
            }
            return new Real(vv);
        }

        switch (e) {
            case '+':
                read();
                if (e == '+') {
                    e = ' ';
                    return map.get("++");
                }
                return new Token('+');

            case '-':
                read();
                if (e == '-') {
                    e  = ' ';
                    return map.get("--");
                }
                return new Token('-');

            case '*':
                e  = ' ';
                return new Token('*');

            case '/':
                e  = ' ';
                return new Token('/');

            case '>':
                read();
                if (e == '=') {
                    e  = ' ';
                    return map.get(">=");
                }
                return new Token('>');

            case '<':
                read();
                if (e == '=') {
                    e  = ' ';
                    return map.get("<=");
                }
                return new Token('<');

            case '=':
                read();
                if (e == '=') {
                    e  = ' ';
                    return map.get("==");
                }
                return new Token('=');

            case '!':
                read();
                if (e == '=') {
                    e  = ' ';
                    return map.get("!=");
                }
                return new Token('!');
            case '&':
                read();
                if (e == '&') {
                    e  = ' ';
                    return map.get("&&");
                }
                return new Token('&');
            case '|':
                read();
                if (e == '|') {
                    e  = ' ';
                    return map.get("||");
                }
                return new Token('|');
        }

        StringBuffer buffer = new StringBuffer();

        if (e == '\'') {
            read();
            while (e != '\'' && e != '\n') {
                char bu=e;
                read();
                if (e == 'n') {
                    buffer.append('\n');
                } else {
                    buffer.append(bu);
                    buffer.append(e);
                }

            }
            e = ' ';
            return new Integer(TypeToken.Char,  buffer.toString().toCharArray()[0]);
        } else if (Character.isJavaIdentifierStart(e)){
            buffer.append(e);
            read();
            while (Character.isJavaIdentifierPart(e)) {
                buffer.append(e);
                read();
            }
            Token token = map.get(buffer.toString());
            if (token == null) {
                token = new Word(TypeToken.ID, buffer.toString());
            }
            return token;

        }
        char e2 = e;
        e  = ' ';
        return new Token(e2);
    }

    private void read() {
        try {
            e = (char) reader.read();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void write() {
        try {
            reader.reset();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void beforeWrite() {
        try {
            reader.mark(1024);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Lex lex = new Lex(System.in);

        while (true)
            System.out.println(lex.getToken());
    }

}
