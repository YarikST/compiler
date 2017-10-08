package Parse.Expression;

import Parse.Parser;
import Parse.TableID;
import Vocabulary.BaseType;
import Vocabulary.TypeToken;
import Vocabulary.Word;

import java.util.Iterator;

/**
 * Created by admin-iorigins on 14.03.17.
 */
public class Variable extends ID {

    private static int count;
    private int id;

    public Variable(BaseType baseType) {
        super(baseType, Word.TEMP);

        TableID.TableElement tableElement;
        do {
            id = ++count;
            tableElement = TableID.get().get(new Word(TypeToken.ID, this.toString()));
        } while (tableElement != null);


        tableElement = new TableID.TableElement(this);
        tableElement.size = baseType.size();
        tableElement.typeAddress = "Stack";
        TableID.TableElement lastTableElementStack = Parser.getLastTableElementStack(TableID.get());

        //System.out.println("variable_"+getId().getLex()+count+"_table:");
        //System.out.println(TableID.get());

        if (lastTableElementStack != null) {
            //tableElement.address = -(lastTableElementStack.address*-1 + lastTableElementStack.size);
            tableElement.address = -(lastTableElementStack.address*-1 + tableElement.size);
            //System.out.println("elm: "+tableElement.address +"size: "+tableElement.size+"\t table: "+Parser.getLastTableElementStack(TableID.get()));
        } else {
            tableElement.address = tableElement.size * -1;
        }


       /* if (tableElement.id.toString().equals("t14")||tableElement.id.toString().equals("t16")||tableElement.id.toString().equals("t15")||tableElement.id.toString().equals("t17")) {
            System.out.println("elm: "+tableElement +"size: "+tableElement.size+"\t table: "+Parser.getLastTableElementStack(TableID.get()));
        }
        if (tableElement.id.toString().equals("t14")||tableElement.id.toString().equals("t16")||tableElement.id.toString().equals("t15")||tableElement.id.toString().equals("t17")) {
            System.out.println("tab: "+TableID.get());
        }*/
        TableID.get().put(new Word(TypeToken.TEMP, this.toString()), tableElement);
       /* if (tableElement.id.toString().equals("t14")||tableElement.id.toString().equals("t16")||tableElement.id.toString().equals("t15")||tableElement.id.toString().equals("t17")) {
            System.out.println("tab: "+TableID.get());
        }*/

    }

    @Override
    public void bv(String str, int t, int f) {
        super.bv(this.toString(), t, f);
    }

    @Override
    public void typeChecking(){
        //type not
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Variable variable = (Variable) o;

        return id == variable.id;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        return ((Word)typeExpression).getLex()+ id;
    }
}
