package Vocabulary;

/**
 * Created by admin-iorigins on 06.03.17.
 */
public class Token {

    protected final int  type;

    public Token(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Token && ((Token) obj).type == this.type;
    }

    @Override
    public int hashCode() {
        return type * 1 * 2 * 4 * 8;
    }

    @Override
    public String toString() {
        return String.valueOf((char)type);
    }


}
