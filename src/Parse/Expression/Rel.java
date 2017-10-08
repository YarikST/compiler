package Parse.Expression;

import Parse.Parser;
import Vocabulary.Array;
import Vocabulary.BaseType;
import Vocabulary.Token;
import Vocabulary.Word;

/**
 * Created by admin-iorigins on 15.03.17.
 */
public class Rel extends LogicalOperations {
    public Rel( Token typeExpression, Expression expression1, Expression expression2) {
        super(typeExpression, expression1, expression2);
        baseType = BaseType.BOO;
    }

    @Override
    public void bv(String str, int t, int f) {
        Expression exp1 = expression1.lv(), exp2 = expression2.lv();
        str = exp1.toString() + getTypeExpression().toString() + exp2.toString();
         /*
        TableID.TableElement tableElement1 = TableID.get().get(new Word(TypeToken.TEMP, exp1.toString()));
        if (tableElement1 == null) {
            tableElement1=TableID.get().get(new Word(TypeToken.ID, exp1.toString()));
        }
        tableElement1.inf.live = true;

        TableID.TableElement tableElement2 = TableID.get().get(new Word(TypeToken.TEMP, exp2.toString()));
        if (tableElement2 == null) {
            tableElement2=TableID.get().get(new Word(TypeToken.ID, exp2.toString()));
        }
        tableElement2.inf.live = true;
        */
        super.bv(str, t, f);
    }

    @Override
    public void typeChecking() throws BaseType.MismatchException, BaseType.ConvertException, Parser.ParserException {
        expression1.typeChecking();
        expression2.typeChecking();

        BaseType max = BaseType.max(expression1.baseType, expression2.baseType);
        Expression convert1 = BaseType.convert(max, expression1);
        Expression convert2 = BaseType.convert(max, expression2);

        expression1 = convert1;
        expression2 = convert2;

        if (expression1.baseType instanceof Array || expression2.baseType instanceof Array) {
            error("expression can not be an array");
        }
        if (expression1.baseType != expression2.baseType) {
            error("mismatch type");
        }
        if (expression1.baseType == BaseType.BOO || expression2.baseType == BaseType.BOO) {
            if (expression1.baseType != BaseType.BOO &&expression2.baseType != BaseType.BOO )
            error("type boo expression nvalid");
        }

    }

    @Override
    public String toString() {
        return expression1 + " " + getTypeExpression() + " " + expression2;
    }
}
