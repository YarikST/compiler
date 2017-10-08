package Parse.Instructions;

import Parse.Parser;
import Vocabulary.BaseType;

import java.util.ArrayList;

/**
 * Created by admin-iorigins on 16.03.17.
 */
public class Program extends Instruction {
    private ArrayList<Method> methods;
    private ArrayList<Instruction> descriptions;

    public Program(ArrayList<Method> methods, ArrayList<Instruction> descriptions) {
        this.methods = methods;
        this.descriptions = descriptions;
    }

    public ArrayList<Method> getMethods() {
        return methods;
    }

    @Override
    public void gen() {
        for (Instruction description : descriptions) {
            description.gen();
        }
        for (Method method : methods) {
            method.gen();
        }
    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException {
        for (Instruction description : descriptions) {
            description.typeChecking();
        }
        for (Method method : methods) {
            method.typeChecking();
        }
    }
}
