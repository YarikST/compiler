package Vocabulary;

/**
 * Created by admin-iorigins on 06.03.17.
 */
public class Word extends Token {

    public static final Word TRUE = new Word(TypeToken.TRUE, "true");
    public static final Word FALSE = new Word(TypeToken.FALSE, "false");
    public static final Word NULL = new Word(TypeToken.NULL, "null");
    public static final Word IF = new Word(TypeToken.IF, "if");
    public static final Word ELSE = new Word(TypeToken.ELSE, "else");
    public static final Word WHILE = new Word(TypeToken.WHILE, "while");
    public static final Word BREAK = new Word(TypeToken.BREAK, "break");
    public static final Word RETURN = new Word(TypeToken.RETURN, "return");
    public static final Word TEMP = new Word(TypeToken.TEMP, "t");
    public static final Word Increment = new Word(TypeToken.Increment, "++");
    public static final Word Decrement = new Word(TypeToken.Decrement, "--");
    public static final Word More_Exactly = new Word(TypeToken.More_Exactly, "<=");
    public static final Word Less_Exactly = new Word(TypeToken.Less_Exactly, ">=");
    public static final Word Not_Exactly = new Word(TypeToken.Not_Exactly, "!=");
    public static final Word Exactly = new Word(TypeToken.Exactly, "==");
    public static final Word Or = new Word(TypeToken.OR, "||");
    public static final Word And = new Word(TypeToken.AND, "&&");



    private String lex;

    public Word(int typeToken,String lex) {
        super(typeToken);
        this.lex = lex;
    }

    public String getLex() {
        return lex;
    }

    @Override
    public boolean equals(Object obj) {
        boolean sup = super.equals(obj);
        if (!sup) {
            return false;
        }
        if (obj instanceof Word) {
            Word word = (Word) obj;
            return this.lex.equals(word.lex);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int sup = super.hashCode();
        if (lex == null) {
            return sup;
        }
        return sup + lex.hashCode();
    }

    @Override
    public String toString() {
        return lex;
    }

}
