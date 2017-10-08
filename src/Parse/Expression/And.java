package Parse.Expression;

import Vocabulary.Token;
import Vocabulary.TypeToken;

/**
 * Created by admin-iorigins on 15.03.17.
 */
public class And extends LogicalOperations {

    public And( Expression expression1, Expression expression2) {
        super(new Token(TypeToken.AND), expression1, expression2);
    }

    @Override
    public void bv(String str, int t, int f) {
        int  ff =f!=0?f:newLable();

        expression1.bv(null, 0, ff);
        expression2.bv(null, t, f);
        if (f == 0) {
            lable(ff);
        }
    }
}
