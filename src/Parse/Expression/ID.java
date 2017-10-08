package Parse.Expression;

import Parse.TableID;
import Vocabulary.BaseType;
import Vocabulary.TypeToken;
import Vocabulary.Word;

/**
 * Created by admin-iorigins on 14.03.17.
 */
public class ID extends Expression {


    public ID(BaseType baseType, Word typeExpression) {
        super(baseType, typeExpression);
    }

    @Override
    public void bv(String str, int t, int f) {
        super.bv(this.toString(), t, f);
    }

    @Override
    public ID lv() {
        return this;
    }

    @Override
    public Expression rv() {
        return this;
    }

    @Override
    public void typeChecking() {
        if (typeExpression.getType() != TypeToken.ID) {
            error("id is not ID");
        }
    }

    public Word getId() {
        return (Word) typeExpression;
    }



    @Override
    public String toString() {
        return ((Word) typeExpression).getLex();
    }




}
