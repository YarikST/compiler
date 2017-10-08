package Parse.Instructions;

import Parse.Expression.ID;
import Parse.Parser;
import Vocabulary.BaseType;

/**
 * Created by admin-iorigins on 16.03.17.
 */

public class Description extends Instruction {
    private BaseType baseType;
    private ID id;
    private Instruction instruction;

    public Description(BaseType baseType, ID id, Instruction instruction) {
        this.baseType = baseType;
        this.id = id;
        this.instruction = instruction;
    }

    @Override
    public void gen() {
        if (instruction == null) {
            return;
        }
        instruction.gen();
    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException {
        if (instruction == null) {
            return;
        }
        instruction.typeChecking();
    }
}
