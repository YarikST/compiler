package Assembler;

import java.util.LinkedList;

/**
 * Created by admin-iorigins on 04.04.17.
 */
public class Graph {
    private SpecialBloc start, end;
    private BaseBloc baseBloc;

    public Graph(SpecialBloc start, SpecialBloc end, BaseBloc baseBloc) {
        this.start = start;
        this.end = end;
        this.baseBloc = baseBloc;
    }

    public SpecialBloc getStart() {
        return start;
    }

    public SpecialBloc getEnd() {
        return end;
    }

    public BaseBloc getBaseBloc() {
        return baseBloc;
    }

    public LinkedList<BaseBloc> linkedList() {
        LinkedList<BaseBloc> list = new LinkedList<>();
        list.add(baseBloc);
        list = rek(list,new LinkedList<BaseBloc>());
        return list;
    }

    private LinkedList<BaseBloc> rek(LinkedList<BaseBloc> list, LinkedList<BaseBloc> list2) {

        LinkedList<BaseBloc> linkedList = new LinkedList<>();

        for (BaseBloc baseBloc : list) {
            if (list2.contains(baseBloc)) {
                continue;
            } else {
                list2.add(baseBloc);
            }
            linkedList.add(baseBloc);
            linkedList.addAll(rek(baseBloc.getBlocsIn(),list2));
            linkedList.addAll(rek(baseBloc.getBlocsOut(), list2));
        }
        return linkedList;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        LinkedList<BaseBloc> blocsIn = baseBloc.getBlocsIn();
        LinkedList<BaseBloc> blocsOut = baseBloc.getBlocsOut();

        LinkedList<BaseBloc> list = linkedList();


        BaseBloc bloc;
        while ((bloc = list.poll()) != null) {
            buffer.append("bloc");
            buffer.append("\t");
            buffer.append(bloc);

            buffer.append("\n");
            buffer.append(bloc.getBlocsIn());
            buffer.append("\n");
            buffer.append(bloc.getBlocsOut());
            buffer.append("\n");
            buffer.append("\n");
        }
        return buffer.toString();
    }


}
