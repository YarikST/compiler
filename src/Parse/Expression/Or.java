package Parse.Expression;

import Vocabulary.Token;
import Vocabulary.TypeToken;

/**
 * Created by admin-iorigins on 15.03.17.
 */
public class Or extends LogicalOperations {
    public Or( Expression expression1, Expression expression2) {
        super(new Token(TypeToken.OR), expression1, expression2);
    }

    @Override
    public void bv(String str, int t, int f) {
        int  tt = t != 0 ? t : newLable();

        expression1.bv(null, tt, 0);
        expression2.bv(null, t, f);

        if (t == 0) {
            lable(tt);
        }

    }
}
