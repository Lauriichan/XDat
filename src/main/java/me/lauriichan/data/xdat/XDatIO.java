package me.lauriichan.data.xdat;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;

import me.lauriichan.data.xdat.util.Arguments;

public abstract class XDatIO {

    private static final ConcurrentHashMap<String, XDatIO> KNOWN;

    static {
        KNOWN = new ConcurrentHashMap<>();
        XDatPrimitiveIO.IO.getClass();
        XDatStringIO.IO.getClass();
        XDatUUIDIO.IO.getClass();
    }

    public static XDatIO get(String id) {
        return KNOWN.get(id);
    }

    private final String id;

    public XDatIO(final String id) {
        if (KNOWN.containsKey(id)) {
            throw new IllegalArgumentException("The id '" + id + "' is already in use!");
        }
        KNOWN.put(id, this);
        this.id = id;
    }

    public final String id() {
        return id;
    }

    protected abstract int size(Class<?> type, Arguments arguments);

    protected abstract boolean supported(Class<?> type, Arguments arguments);

    protected abstract Object read0(InputStream stream, int size, Arguments arguments) throws Exception;

    public final Object read(InputStream stream, int size, Arguments arguments) throws Exception {
        if (stream.read() == 0) {
            stream.skip(size);
            return null;
        }
        return read0(stream, size, arguments);
    }

    protected abstract void write0(OutputStream stream, int size, Object value, Arguments arguments) throws Exception;

    public final void write(OutputStream stream, int size, Object value, Arguments arguments) throws Exception {
        if (value == null) {
            stream.write(0);
            stream.write(new byte[size]);
            return;
        }
        stream.write(1);
        write0(stream, size, value, arguments);
    }

}
