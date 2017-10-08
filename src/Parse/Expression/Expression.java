package Parse.Expression;

import Parse.Node;
import Vocabulary.BaseType;
import Vocabulary.Token;

/**
 * Created by admin-iorigins on 14.03.17.
 */
public abstract class Expression extends Node {
    protected BaseType baseType;
    protected Token typeExpression;


    public Expression(BaseType baseType, Token typeExpression) {
        this.typeExpression = typeExpression;
        this.baseType = baseType;
    }

    public  ID lv(){
        Variable variable = new Variable(baseType);
        Expression rv = rv();
        out(variable + "= " + rv);
        return variable;
    }

    public abstract Expression rv();

    public void bv(String str,int t, int f) {

        if (t == 0 && f == 0) {
            return;
        } else  if (t != 0) {
            out("if " + str + " then goto "+lableStr()+t);
        } else if (f != 0) {
            out("iffalse " + str + " then goto "+lableStr()+f);
        }
    }

    public BaseType getBaseType() {
        return baseType;
    }

    public Token getTypeExpression() {
        return typeExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Expression that = (Expression) o;

        if (baseType != null ? !baseType.equals(that.baseType) : that.baseType != null) return false;
        return typeExpression != null ? typeExpression.equals(that.typeExpression) : that.typeExpression == null;

    }

    @Override
    public int hashCode() {
        int result = baseType != null ? baseType.hashCode() : 0;
        result = 31 * result + (typeExpression != null ? typeExpression.hashCode() : 0);
        return result;
    }
}
