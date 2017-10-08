package Parse.Instructions;

import Parse.Expression.AccessArray;
import Parse.Expression.Expression;
import Parse.Expression.ID;
import Parse.Parser;
import Vocabulary.Array;
import Vocabulary.BaseType;
import Vocabulary.Reference;

/**
 * Created by admin-iorigins on 16.03.17.
 */
public class SetElement extends Instruction {

    private ID id;
    private Expression expressionIndex;
    private Expression expression;

    private AccessArray accessArray;

    public SetElement(Expression expression, AccessArray accessArray) {
        this.expression = expression;
        id = accessArray.getIdMas();
        this.accessArray = accessArray;
    }

    @Override
    public void gen() {
        accessArray = (AccessArray) accessArray.rv();

        expressionIndex = accessArray.getExpressionIndex();

        out(id+"["+expressionIndex.lv()+"]"+"= "+expression.lv());
    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException {

        accessArray.typeChecking();
        expression.typeChecking();

        BaseType.max(accessArray.getBaseType(), expression.getBaseType());


        expression = BaseType.convert(accessArray.getBaseType(), expression);
    }
}
