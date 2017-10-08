package Assembler;

import java.util.LinkedList;

/**
 * Created by admin-iorigins on 04.04.17.
 */
public class BaseBloc {

   public static class Element {
      public   static class Inf implements Cloneable{
            public boolean live, used;

          @Override
          public Inf clone(){
              try {
                  return (Inf) super.clone();
              } catch (CloneNotSupportedException e) {
                  e.printStackTrace();
              }
              throw new RuntimeException();
          }

          @Override
          public String toString() {
              return "live: " + live + "\t" + "used: " + used;
          }
      }

        public String string;
        public LinkedList<Inf> infs;

       public Element() {
           infs = new LinkedList<>();
       }

       @Override
        public String toString() {
            return string.toString();
        }
    }

    private LinkedList<Element> strings;
    private LinkedList<BaseBloc> blocsIn, blocsOut;

    public BaseBloc() {
        strings = new LinkedList<>();
        blocsIn = new LinkedList<>();
        blocsOut = new LinkedList<>();
    }

    public LinkedList<Element> getStrings() {
        return strings;
    }

    public LinkedList<BaseBloc> getBlocsOut() {
        return blocsOut;
    }

    public LinkedList<BaseBloc> getBlocsIn() {
        return blocsIn;
    }

    public boolean isEmpty() {
        return strings.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseBloc baseBloc = (BaseBloc) o;

        if (strings != null ? !strings.equals(baseBloc.strings) : baseBloc.strings != null) return false;
        if (blocsIn != null ? !blocsIn.equals(baseBloc.blocsIn) : baseBloc.blocsIn != null) return false;
        return blocsOut != null ? blocsOut.equals(baseBloc.blocsOut) : baseBloc.blocsOut == null;

    }

    @Override
    public int hashCode() {
        int result = strings != null ? strings.hashCode() : 0;
        result = 31 * result + (blocsIn != null ? blocsIn.hashCode() : 0);
        result = 31 * result + (blocsOut != null ? blocsOut.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return strings.toString();
    }
}
