package Vocabulary;

/**
 * Created by admin-iorigins on 06.03.17.
 */
public class Integer extends Token  {
    private int value;

    public Integer(int value) {
        this(TypeToken.INT,value);
    }
    public Integer(int typeToken,int value) {
        super(typeToken);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        boolean sup = super.equals(obj);
        if (!sup) {
            return false;
        }
        if (obj instanceof Integer) {
            Integer  integer = (Integer) obj;
            return this.value == integer.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int sup = super.hashCode();
        return sup + value;
    }


    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
