package Parse.Instructions;

import Parse.Parser;
import Vocabulary.BaseType;

import java.util.LinkedList;

/**
 * Created by admin-iorigins on 16.03.17.
 */
public class ListInstruction extends Instruction {
    private LinkedList<Instruction> instructions;

    public ListInstruction() {
        instructions = new LinkedList<>();
    }

    public ListInstruction(Instruction ... arg) {
        this();
        for (Instruction instruction : arg) {
            put(instruction);
        }
    }

    @Override
    public void gen() {
        for (Instruction instruction : instructions) {
            instruction.gen();
        }
    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException {
        for (Instruction instruction : instructions) {
            instruction.typeChecking();
        }
    }


    public void put(Instruction instruction) {
        instructions.add(instruction);
    }

}
