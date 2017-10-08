package Parse.Instructions;

import Parse.Expression.Expression;
import Parse.Parser;
import Vocabulary.BaseType;

/**
 * Created by admin-iorigins on 16.03.17.
 */
public class If extends Instruction{
    private Expression expression;
    private Instruction instruction;

    public If(Expression expression, Instruction instruction) {
        this.expression = expression;
        this.instruction = instruction;
    }

    @Override
    public void gen() {
        int f = newLable();
        expression.bv(null,0,f);
        instruction.gen();
        lable(f);

    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException {
        expression.typeChecking();

        if (expression.getBaseType() != BaseType.BOO) {
            throw new BaseType.MismatchException(BaseType.BOO, expression.getBaseType());
        }
        instruction.typeChecking();
    }
}
