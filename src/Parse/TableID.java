package Parse;

import Assembler.BaseBloc;
import Parse.Expression.ID;
import Vocabulary.TypeToken;
import Vocabulary.Word;

/**
 * Created by admin-iorigins on 16.03.17.
 */
public class TableID extends Environment<Word,TableID.TableElement> {
   public static class TableElement {
        public ID id;
        public int size;

        public int address;
        public String typeAddress;//Stack Heap

        public String Reg;
        public String staticAddress;

        public BaseBloc.Element.Inf inf;

        public TableID tableID;

       public TableElement(ID id) {
           this.id = id;
           inf = new BaseBloc.Element.Inf();

           if (id.getTypeExpression().getType() == TypeToken.ID) {
               inf.live = true;
           }
       }

       @Override
       public String toString() {
           return "--" + "\t id \t" + id + "\t size \t" + size + "\t address \t" + address + "\t typeAddress \t" + typeAddress+ "\t Reg \t" + Reg+"\t staticAddress \t"+staticAddress+"\t -- \t";
       }

       @Override
       public boolean equals(Object o) {
           if (this == o) return true;
           if (o == null || getClass() != o.getClass()) return false;

           TableElement that = (TableElement) o;

           return id != null ? id.equals(that.id) : that.id == null;

       }

       @Override
       public int hashCode() {
           return id != null ? id.hashCode() : 0;
       }
   }

    private static TableID env;

    public TableID(TableID env) {
        super(env);
    }


    public static void push() {
        env = new TableID(env);
    }
    public static void set(TableID env) {
        TableID.env = env;
    }
    public static void pop() {
        env = (TableID) env.environment;
    }


    public static TableID get() {
        return env;
    }
}
