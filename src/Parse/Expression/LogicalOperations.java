package Parse.Expression;

import Parse.Parser;
import Vocabulary.BaseType;
import Vocabulary.Token;

/**
 * Created by admin-iorigins on 15.03.17.
 */
public abstract class LogicalOperations extends Expression {

    protected Expression expression1, expression2;

    public LogicalOperations( Token typeExpression, Expression expression1, Expression expression2) {
        super(BaseType.BOO, typeExpression);
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public Expression rv() {
        Variable variable = new Variable(baseType);
        int f = newLable();
        int l2 = newLable();

        bv(null,0,f);
        out(variable + "=true");
        out("goto L" + l2);
        lable(f);
        out(variable + "=false");
        lable(l2);
        return variable;
    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException {
        expression1.typeChecking();
        expression2.typeChecking();

        if (expression1.baseType != BaseType.BOO || expression2.baseType != BaseType.BOO) {
            error("mismatch expected Boo");
        }
    }

}
