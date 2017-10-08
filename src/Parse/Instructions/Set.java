package Parse.Instructions;

import Parse.Expression.*;
import Parse.Expression.Call;
import Parse.Parser;
import Vocabulary.Array;
import Vocabulary.BaseType;
import Vocabulary.Reference;

/**
 * Created by admin-iorigins on 16.03.17.
 */
public class Set extends Instruction {
    private ID id;
    private Expression expression;

    public Set(ID id, Expression expression) {
        this.id = id;
        this.expression = expression;
    }

    @Override
    public void gen() {
        out(id + "= " + expression.rv());
    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException {
        expression.typeChecking();

        if (expression instanceof Parse.Expression.Call) {
            Parse.Expression.Call call = (Call) expression;
            if (call.getID().getId().getLex().equals("new")) {
                return;
            }
        }

        BaseType.max(id.getBaseType(), expression.getBaseType());

        expression = BaseType.convert(id.getBaseType(), expression);

    }
}
