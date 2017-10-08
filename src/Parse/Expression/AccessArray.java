package Parse.Expression;

import Parse.Parser;
import Vocabulary.BaseType;
import Vocabulary.Integer;
import Vocabulary.Token;
import Vocabulary.TypeToken;

/**
 * Created by admin-iorigins on 16.03.17.
 */
public class AccessArray extends Expression {
    private ID idMas;
    private Expression expressionIndex;

    public AccessArray(BaseType baseType,ID idMas, Expression expressionIndex) {
        super(baseType, new Token(TypeToken.INDEX));
        this.idMas = idMas;
        this.expressionIndex = expressionIndex;
    }

    @Override
    public Expression rv() {
        ArefmetychniOperations arefmetychniOperations = new ArefmetychniOperations(new Token(TypeToken.Multiplication), expressionIndex.lv(), new Constant(BaseType.INT, new Integer(baseType.size())));
        try {
            arefmetychniOperations.typeChecking();
        } catch (BaseType.ConvertException e) {
            e.printStackTrace();
        } catch (BaseType.MismatchException e) {
            e.printStackTrace();
        } catch (Parser.ParserException e) {
            e.printStackTrace();
        }
        expressionIndex = arefmetychniOperations.lv();
        return new AccessArray(baseType, idMas, expressionIndex);
    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException {
        expressionIndex.typeChecking();
    }

    public ID getIdMas() {
        return idMas;
    }

    public Expression getExpressionIndex() {
        return expressionIndex;
    }

    @Override
    public String toString() {
        return idMas + "[" + expressionIndex + "]";
    }
}
