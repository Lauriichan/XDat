package me.lauriichan.data.xdat;

public abstract class XDatCache {

    public abstract boolean has(String dataKey);

    public abstract Object get(String dataKey);

    public abstract <E> E get(String dataKey, Class<E> type);

    public abstract void set(String dataKey, Object object);

}
