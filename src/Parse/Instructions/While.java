package Parse.Instructions;

import Parse.Expression.Expression;
import Parse.Parser;
import Vocabulary.BaseType;

/**
 * Created by admin-iorigins on 16.03.17.
 */
public class While extends Instruction {
    private Expression expression;
    private Instruction instruction;

    public While(Expression expression, Instruction instruction) {
        this.expression = expression;
        this.instruction = instruction;
    }

    @Override
    public void gen() {
        int l = newLable();
        int f = newLable();

        lable(l);
        integerStack.push(f);
        expression.bv(null, 0, f);
        instruction.gen();
        out("goto L" + l);
        lable(f);
    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException {
        expression.typeChecking();

        if (expression.getBaseType() != BaseType.BOO) {
            error("mismatch expected Boo");
        }
    }
}
