package Parse;

import Parse.Instructions.Method;
import Vocabulary.Word;

/**
 * Created by admin-iorigins on 16.03.17.
 */
public class TableMethods extends Environment<Word,Method> {
    private static TableMethods env;

    public TableMethods(TableMethods env) {
        super(env);
    }

    public TableMethods() {
    }

    public static void push() {
        env = new TableMethods(env);
    }

    public static void pop() {
        env = (TableMethods) env.environment;
    }

    public static TableMethods get() {
        return env;
    }

}
