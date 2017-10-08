package Parse.Instructions;

import Parse.Expression.ID;
import Parse.Parser;
import Parse.TableID;
import Vocabulary.Array;
import Vocabulary.BaseType;
import Vocabulary.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by admin-iorigins on 16.03.17.
 */
public class Method extends Instruction {
    private BaseType returnType;
    protected ID[] arg;
    private Instruction instruction;
    private ID id;
    private boolean aReturn;

    public Method(BaseType returnType, ID id, ID[] arg, Instruction instruction, boolean aReturn) {
        this.returnType = returnType;
        this.arg = arg;
        this.instruction = instruction;
        this.id = id;
        this.aReturn = aReturn;
    }

    public ID id() {
        return id;
    }

    public BaseType getReturnType() {
        return returnType;
    }

    @Override
    public void gen() {
        TableID tableID = TableID.get();
        TableID.set(TableID.get().get(id.getId()).tableID);

       // System.out.println("method_" + id.getId().getLex()+"_table:");
       // System.out.println(TableID.get());

        out("method_" + id.getId().getLex()+":");
        instruction.gen();
        TableID.set(tableID);
    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException {

        TableID tableID = TableID.get();
        TableID.set(TableID.get().get(id.getId()).tableID);

        instruction.typeChecking();
        //System.out.println(returnType != BaseType.VOID && aReturn == null);
        if (returnType != BaseType.VOID && !aReturn) {
            throw new Parser.ParserException("there is not return",Word.RETURN, null);
        }

        TableID.set(tableID);
    }

   /* @Override
    public String toString() {
        return "retType: " + returnType + " ret: " + aReturn;
    }*/

    @Override
    public String toString() {
        return "";
    }
}
