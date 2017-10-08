package Vocabulary;

import Parse.Expression.Constant;
import Parse.Expression.Expression;
import Parse.Expression.ID;
import Parse.Expression.UnaryArefmetychni;
import Parse.Instructions.Set;

/**
 * Created by admin-iorigins on 07.03.17.
 */
public class BaseType extends Word {

    public static class ConvertException extends Exception {
        private BaseType baseType;
        private Expression expression;

        public ConvertException(BaseType baseType, Expression expression) {
            this.baseType = baseType;
            this.expression = expression;
        }

        public BaseType getBaseType() {
            return baseType;
        }

        public Expression getExpression() {
            return expression;
        }

        @Override
        public String toString() {
            return "is not convert type " + baseType + " expression " + expression;
        }
    }

    public static class MismatchException extends Exception {
        private BaseType baseType1, baseType2;

        public MismatchException(BaseType baseType1, BaseType baseType2) {
            this.baseType1 = baseType1;
            this.baseType2 = baseType2;
        }

        public BaseType getBaseType1() {
            return baseType1;
        }

        public BaseType getBaseType2() {
            return baseType2;
        }

        @Override
        public String toString() {
            return "mismatch type (" + baseType1 + " " + baseType2 + ")";
        }
    }

    public static final BaseType
            INT =  new BaseType("int",   TypeToken.BASE_TYPE, 4),
            REAL = new BaseType("real", TypeToken.BASE_TYPE, 8),

            BOO =  new BaseType("boo",   TypeToken.BASE_TYPE, 4),
            CHAR =  new BaseType("char",   TypeToken.BASE_TYPE, 4),

            VOID =  new BaseType("void",   TypeToken.BASE_TYPE, 4);

    private int width;

    public BaseType(String lex, int teg, int width) {
        super(teg, lex);
        this.width = width;
    }


    public int size() {
        return width;
    }


    public static boolean num(BaseType baseType) {
        if (baseType == INT || baseType == REAL||baseType==CHAR) {
            return true;
        }
        return false;
    }

    public static BaseType max(BaseType baseType1, BaseType baseType2)throws MismatchException {
        if (BaseType.num(baseType1) && BaseType.num(baseType2)) {
            if (baseType1 == REAL || baseType2 == REAL) {
                return REAL;
            } else if (baseType1 == INT || baseType2 == INT) {
                return INT;
            } else {
                return CHAR;
            }

        } else if (baseType1==BOO&&baseType2==BOO){
            return BOO;
        } else if (baseType1.equals(baseType2)) {
            return baseType1;
        }
        throw new MismatchException(baseType1,baseType2);
    }

    public static Expression convert(BaseType baseType, Expression expression) throws ConvertException {
        if (baseType==(expression.getBaseType())) {
            return expression;
        }
        ConvertException convertException = new ConvertException(baseType, expression);
        BaseType typeE = expression.getBaseType();


        if (baseType == INT) {
            if (typeE == CHAR) {
                return new UnaryArefmetychni(INT,new Word(TypeToken.Convert,"(int)") , expression);
            }
            throw convertException;
        }
         if (baseType == REAL) {
             if (typeE == INT) {
                 return new UnaryArefmetychni(REAL,new Word(TypeToken.Convert,"(float)") , expression);
             }
             /*if (typeE == CHAR) {
                 return new UnaryArefmetychni(REAL,new Word(TypeToken.Convert,"(float)") , expression);
             }*/
             throw convertException;
        }
        if (baseType == CHAR) {
            if (typeE == INT) {
                return new UnaryArefmetychni(CHAR,new Word(TypeToken.Convert,"(char)") , expression);
            }
            throw convertException;
        }

        if (baseType == BOO) {
            throw convertException;
        }

        if (baseType.equals(new Reference(typeE))) {
            return expression;
        }
        throw convertException;

    }


    public static Set assignedDefault(ID id) {
        Set set = null;
        BaseType baseType = id.getBaseType();

        if (baseType == BaseType.INT) {
            set = new Set(id, new Constant(BaseType.INT, new Integer(0)));
        } else if (baseType == BaseType.REAL) {
            set = new Set(id, new Constant(BaseType.REAL, new Real(0.0f)));
        } else if (baseType == BaseType.CHAR) {
            set = new Set(id, new Constant(BaseType.CHAR, new Word(TypeToken.Char,"65")));
        } else if (baseType == BaseType.BOO) {
            set = new Set(id, new Constant(BaseType.BOO, Word.FALSE));
        }

        return set;
    }

    @Override
    public boolean equals(Object obj) {
        boolean boo = super.equals(obj);
        if (!boo) {
            return false;
        }
        if (obj instanceof BaseType) {
            BaseType baseType = (BaseType) obj;
            return this.width == baseType.width;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int sup = super.hashCode();
        return sup + width;
    }

    @Override
    public String toString() {
        return getLex();
    }


}
