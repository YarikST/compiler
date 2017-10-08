package MyClass;

import Assembler.Assembler;
import Parse.Parser;
import Vocabulary.Lex;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;

/**
 * Created by iorigins on 05.06.17.
 */
public class Start {
    public static void main(String[] args) throws FileNotFoundException {

        if (args.length < 2) {
            System.out.println("arg error");
            return;
        }


        String nameFile = args[0];
        String name = args[1];



        Lex lex = new Lex(new FileInputStream(nameFile+".txt"));
        Parser parser = new Parser(lex);
        Assembler assembler = new Assembler(parser, nameFile,name, new OutputStreamWriter(System.out));
        assembler.assembler();
    }
}
