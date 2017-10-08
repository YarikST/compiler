package Parse.Expression;

import Vocabulary.Token;
import Vocabulary.TypeToken;

/**
 * Created by admin-iorigins on 15.03.17.
 */
public class Not extends LogicalOperations {
    public Not(Expression expression) {
        super(new Token(TypeToken.NOT), expression, expression);
    }

    @Override
    public void bv(String str, int t, int f) {
        expression1.bv(null,f,t);
    }
}
