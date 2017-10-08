package Parse.Expression;

import Parse.Parser;
import Vocabulary.*;
import Vocabulary.Integer;

/**
 * Created by admin-iorigins on 15.03.17.
 */
public class Constant extends Expression {

    public Constant(BaseType baseType,Token typeExpression) {
        super(baseType, typeExpression);
    }

    @Override
    public void bv(String str, int t, int f) {
        super.bv(this.toString(), t, f);
    }

    @Override
    public Expression rv() {
        return this;
    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, Parser.ParserException {
        if (typeExpression instanceof Integer || typeExpression instanceof Real || typeExpression.getType() == TypeToken.FALSE || typeExpression.getType() == TypeToken.TRUE || typeExpression.getType() == TypeToken.Char) {
            return;
        }
        throw new Parser.ParserException(null, typeExpression);
    }

    @Override
    public String toString() {
        return typeExpression.toString();
    }
}
