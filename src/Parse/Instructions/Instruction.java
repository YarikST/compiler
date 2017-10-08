package Parse.Instructions;

import Parse.Node;

import java.util.Stack;

/**
 * Created by admin-iorigins on 16.03.17.
 */
public abstract class Instruction extends Node {

    protected static Stack<Integer> integerStack = new Stack<>();

    public abstract void gen();
}
