package Parse.Instructions;

import Parse.Expression.Expression;
import Parse.Expression.ID;
import Parse.Expression.Variable;
import Parse.Parser;
import Parse.TableID;
import Parse.TableMethods;
import Vocabulary.BaseType;
import Vocabulary.TypeToken;
import Vocabulary.Word;

/**
 * Created by admin-iorigins on 16.03.17.
 */
public class Call extends Instruction {
    private ID id;
    private Expression arg[];
    private Method method;
    private Variable rez;

    public Call(ID id) {
        this.id = id;
        arg = new Expression[0];
    }

    public Call(ID id, Expression[] arg) {
        this(id);
        this.arg = arg;
    }


    public Variable getRez() {
        return rez;
    }

    public ID getId() {
        return id;
    }

    public void setRez(Variable rez) {
        this.rez = rez;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public void gen() {
       /* Expression argLV[] = new Expression[arg.length];
        for (int i = 0; i < arg.length; i++) {
            argLV[i] = arg[i].lv();
        }
        for (Expression expression : argLV) {
            out("params " + expression);
        }*/
        Variable variable;
       // System.out.println("call "+id.getId().getLex()+" arg "+arg.length);

        if (id.getId().getLex().equals("out_i")||id.getId().getLex().equals("out_d")||id.getId().getLex().equals("out_c")||id.getId().getLex().equals("out_s")||id.getId().getLex().equals("new")
                ||id.getId().getLex().equals("in_i")||id.getId().getLex().equals("in_d")||id.getId().getLex().equals("in_c")||id.getId().getLex().equals("ins_s")) {
           // System.out.println("type Checking static");
            for (Expression expression : arg) {
                try {
                    expression.typeChecking();
                } catch (BaseType.ConvertException e) {
                    e.printStackTrace();
                } catch (BaseType.MismatchException e) {
                    e.printStackTrace();
                } catch (Parser.ParserException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Expression expression : arg) {
           // System.out.println(expression.getClass());
//            System.out.println(expression);
            Expression lv = expression.lv();
          //  System.out.println(lv);
            out("params " + lv);
            TableID.TableElement tableElement = TableID.get().get(new Word(TypeToken.TEMP, lv.toString()));
            if (tableElement!=null)
            tableElement.inf.live = true;
        }
        if (rez == null) {
            out("call method_" + id.getId().getLex() + "," + arg.length);
        } else {
            out(rez+"= call method_" + id.getId().getLex() + "," + arg.length);
        }
    }

    @Override
    public void typeChecking() throws BaseType.ConvertException, BaseType.MismatchException, Parser.ParserException {
        method = TableMethods.get().get(id.getId());
        if (method == null) {
            if (!id.getId().getLex().equals("out_c")&&!id.getId().getLex().equals("out_i")&&!id.getId().getLex().equals("out_d")&&!id.getId().getLex().equals("out_s")&&!id.getId().getLex().equals("new")
                    &&!id.getId().getLex().equals("in_i")&&!id.getId().getLex().equals("in_d")&&!id.getId().getLex().equals("in_c")&&!id.getId().getLex().equals("in_s"))
            error("method is not described");
            return;
        }
        ID[] arg = method.arg;
        if (this.arg.length != arg.length) {
            error("argumentCount");
            return;
        }

      //  System.out.println("call "+id.getId().getLex()+" innst typeChecking");

        for (Expression expression : arg) {
         //   System.out.println(expression.getClass());
        }

        for (int i = 0; i < arg.length; i++) {
            Expression expression = this.arg[i];
            ID id = arg[i];

            expression.typeChecking();
            id.typeChecking();

            expression = BaseType.convert(id.getBaseType(), expression);
            this.arg[i] = expression;
        }
    }
}
