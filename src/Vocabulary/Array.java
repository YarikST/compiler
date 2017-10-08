package Vocabulary;

import Parse.Expression.Expression;

import java.lang.*;

/**
 * Created by admin-iorigins on 09.03.17.
 */
public class Array extends BaseType {
    private BaseType baseType;

    public Array(BaseType baseType) {
        super("[]"+baseType.getLex(),TypeToken.INDEX,baseType.size());
        this.baseType = baseType;
    }

    public BaseType getBaseType() {
        return baseType;
    }

    @Override
    public String toString() {
        return getLex();
    }
}
