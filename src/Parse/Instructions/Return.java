package Parse.Instructions;

import Parse.Expression.Expression;
import Parse.Expression.ID;
import Parse.Expression.Variable;
import Parse.Parser;
import Parse.TableID;
import Vocabulary.BaseType;
import Vocabulary.TypeToken;
import Vocabulary.Word;

/**
 * Created by admin-iorigins on 27.03.17.
 */
public class Return extends Instruction {
    private Expression expression;
    private BaseType methodType;

    public Return(Expression expression, BaseType methodType) {
        this.expression = expression;
        this.methodType = methodType;
    }

    protected void setExpression(Expression expression) {
        this.expression = expression;
    }

    protected Expression getExpression() {
        return expression;
    }

    @Override
    public void gen() {
        System.out.println("return_table:");
        System.out.println(TableID.get());
        ID lv = expression.lv();
        TableID.TableElement tableElement = TableID.get().get(new Word(TypeToken.TEMP, lv.toString()));
        if (tableElement == null) {
            tableElement=TableID.get().get(new Word(TypeToken.ID, lv.toString()));
        }
        tableElement.inf.live = true;
        out("return " + lv);
    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException {
        //System.out.println(expression);
        expression.typeChecking();
        expression = BaseType.convert(methodType, expression);
        //type parser
    }
}
