package Parse.Instructions;

import Parse.Expression.Expression;
import Parse.Parser;
import Vocabulary.BaseType;

/**
 * Created by admin-iorigins on 20.03.17.
 */
public class Else extends Instruction {
    private Expression expression;
    private Instruction instruction1, instruction2;

    public Else(Expression expression, Instruction instruction1, Instruction instruction2) {
        this.expression = expression;
        this.instruction1 = instruction1;
        this.instruction2 = instruction2;
    }

    @Override
    public void gen() {
        int f = newLable();
        int l = newLable();
        expression.bv(null, 0, f);
        instruction1.gen();
        out("goto L"+l);
        lable(f);
        instruction2.gen();
        lable(l);
    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException {
        expression.typeChecking();
        if (expression.getBaseType() != BaseType.BOO) {
            throw new BaseType.MismatchException(BaseType.BOO, expression.getBaseType());
        }
        instruction1.typeChecking();
        instruction2.typeChecking();
    }

}
