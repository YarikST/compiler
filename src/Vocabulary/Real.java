package Vocabulary;

/**
 * Created by admin-iorigins on 06.03.17.
 */
public class Real extends Token  {
    private float value;

    public Real(float value) {
        super(TypeToken.REAL);
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        boolean sup = super.equals(obj);
        if (!sup) {
            return false;
        }
        if (obj instanceof Real) {
            Real real = (Real) obj;
            return this.value == real.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int sup = super.hashCode();
        return (int) (sup + value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
