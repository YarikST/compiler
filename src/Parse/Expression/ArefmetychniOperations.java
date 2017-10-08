package Parse.Expression;

import Parse.Parser;
import Vocabulary.*;

/**
 * Created by admin-iorigins on 15.03.17.
 */
public class ArefmetychniOperations extends Expression {
    protected Expression expression1, expression2;

    public ArefmetychniOperations( Token typeExpression, Expression expression1, Expression expression2) {
        super(null, typeExpression);
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public Expression rv() {
        return new ArefmetychniOperations(typeExpression, expression1.lv(), expression2.lv());
    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException {
        expression1.typeChecking();
        expression2.typeChecking();

        baseType = BaseType.max(expression1.baseType, expression2.baseType);
        expression1 = BaseType.convert(baseType, expression1);
        expression2 = BaseType.convert(baseType, expression2);
    }

    @Override
    public String toString() {
        return expression1 + " " + typeExpression + " " + expression2;
    }

}
