package me.lauriichan.data.xdat;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import me.lauriichan.data.xdat.util.Arguments;

public class XDatStringIO extends XDatIO {

    public static final String ID = "string";
    public static final XDatStringIO IO = new XDatStringIO();

    private XDatStringIO() {
        super(ID);
    }

    @Override
    protected int size(Class<?> type, Arguments arguments) {
        return Math.abs(arguments.getInt("size"));
    }

    @Override
    protected boolean supported(Class<?> type, Arguments arguments) {
        return type == String.class && arguments.has("size") && arguments.getInt("size") != 0;
    }

    @Override
    protected Object read0(InputStream stream, int size, Arguments arguments) throws Exception {
        byte[] bytes = new byte[size];
        stream.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    protected void write0(OutputStream stream, int size, Object value, Arguments arguments) throws Exception {
        byte[] bytes = value.toString().getBytes(StandardCharsets.UTF_8);
        byte[] write = new byte[size];
        System.arraycopy(bytes, 0, write, 0, Math.min(write.length, bytes.length));
        stream.write(write);
    }

}
