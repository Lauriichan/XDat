package me.lauriichan.data.xdat;

public final class XDatVoidCache extends XDatCache {

    public static final XDatVoidCache INSTANCE = new XDatVoidCache();

    private XDatVoidCache() {}

    @Override
    public boolean has(String dataKey) {
        return false;
    }

    @Override
    public Object get(String dataKey) {
        return null;
    }

    @Override
    public <E> E get(String dataKey, Class<E> type) {
        return null;
    }

    @Override
    public void set(String dataKey, Object object) {}

}
