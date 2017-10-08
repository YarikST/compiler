package Parse;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by admin-iorigins on 16.03.17.
 */
public class Environment<T, E> implements Iterable<E> {


    private HashMap<T, E> hashMap;
    protected Environment<T, E> environment;

    public Environment() {
        hashMap = new HashMap<>();
    }

    public Environment(Environment<T, E> environment) {
        this();
        this.environment = environment;
    }

    public void put(T t, E e) {
        hashMap.put(t, e);
    }

    public E get(T t) {
        E e = hashMap.get(t);
        if (e == null && environment != null) {
           e= environment.get(t);
        }
        return e;
    }

    public boolean isElement(T t) {
        return hashMap.get(t) == null ? false : true;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            Environment<T, E> env = environment;
            Iterator<E> iterator = hashMap.values().iterator();
            @Override
            public boolean hasNext() {
                if (iterator.hasNext()) {
                    return true;
                } else {
                    if (env == null) {
                        return false;
                    }
                    iterator = env.iterator();
                    env = env.environment;
                    return hasNext();
                }
            }

            @Override
            public E next() {
               return iterator.next();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    public Iterator<E> thisIterator(){
        return new Iterator<E>() {
            Iterator<E> iterator = hashMap.values().iterator();
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public E next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }


    @Override
    public String toString() {
        return hashMap.values() + " env " + environment;
    }

}
