package Parse.Expression;

import Parse.Instructions.Method;
import Parse.Parser;
import Parse.TableID;
import Vocabulary.BaseType;
import Vocabulary.TypeToken;
import Vocabulary.Word;

/**
 * Created by admin-iorigins on 27.03.17.
 */
public class Call extends Expression {

    private Parse.Instructions.Call call;

    public Call(Parse.Instructions.Call call) {
        super(null, null);
        this.call = call;
    }

    @Override
    public Expression rv() {
        //System.out.println("call ex");
        call.gen();
        return this;
    }

    public ID getID() {
        return call.getId();
    }

    @Override
    public void typeChecking() throws BaseType.MismatchException, BaseType.ConvertException, Parser.ParserException {
       // System.out.println("call "+""+" ex typeChecking");
        call.typeChecking();
        Method method = call.getMethod();
        if (!call.getId().getId().getLex().equals("new")) {
            if (method.getReturnType() != (BaseType.VOID)) {
                call.setRez(new Variable(method.getReturnType()));
                TableID.TableElement tableElement = TableID.get().get(new Word(TypeToken.TEMP, call.getRez().toString()));
                tableElement.inf.live = true;
            }
            baseType = call.getRez().baseType;
            typeExpression = call.getRez().typeExpression;
        } else {
            call.setRez(new Variable(BaseType.INT));
            TableID.TableElement tableElement = TableID.get().get(new Word(TypeToken.TEMP, call.getRez().toString()));
            tableElement.inf.live = true;
            baseType = call.getRez().baseType;
            typeExpression = call.getRez().typeExpression;
        }
    }

    @Override
    public String toString() {
        /*if (call.getId().getId().getLex().equals("new")) {
            return "new";
        }*/
        return call.getRez().toString();
    }
}
