package Parse.Expression;

import Parse.Parser;
import Vocabulary.BaseType;
import Vocabulary.Token;

/**
 * Created by admin-iorigins on 15.03.17.
 */
public class UnaryArefmetychni extends Expression {

   private Expression expression;

    public UnaryArefmetychni(BaseType baseType, Token typeExpression, Expression expression) {
        super(baseType, typeExpression);
        this.expression = expression;
    }

    @Override
    public Expression rv() {
        return new UnaryArefmetychni(baseType,typeExpression,expression.lv());
    }

    @Override
    public void typeChecking() throws BaseType.MismatchException, BaseType.ConvertException, Parser.ParserException {
        expression.typeChecking();
        BaseType.max(baseType, expression.baseType);
    }

    @Override
    public String toString() {
        return typeExpression + " " + expression;
    }

}
