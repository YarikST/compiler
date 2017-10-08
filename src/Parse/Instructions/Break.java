package Parse.Instructions;

/**
 * Created by admin-iorigins on 16.03.17.
 */
public class Break extends Instruction {
    @Override
    public void gen() {
        out("goto L"+integerStack.pop());
    }

    @Override
    public void typeChecking() {
        //type parser
    }
}
