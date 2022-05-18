package me.lauriichan.data.xdat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import me.lauriichan.data.xdat.util.Arguments;

public class XDatUUIDIO extends XDatIO {

    public static final String ID = "uuid";
    public static final XDatUUIDIO IO = new XDatUUIDIO();

    private XDatUUIDIO() {
        super(ID);
    }

    @Override
    protected int size(Class<?> type, Arguments arguments) {
        return Long.BYTES * 2;
    }

    @Override
    protected boolean supported(Class<?> type, Arguments arguments) {
        return type == UUID.class;
    }

    @Override
    protected Object read0(InputStream stream, int size, Arguments arguments) throws Exception {
        DataInputStream input = new DataInputStream(stream);
        long most = input.readLong();
        long least = input.readLong();
        return new UUID(most, least);
    }

    @Override
    protected void write0(OutputStream stream, int size, Object value, Arguments arguments) throws Exception {
        DataOutputStream output = new DataOutputStream(stream);
        UUID uuid = (UUID) value;
        output.writeLong(uuid.getMostSignificantBits());
        output.writeLong(uuid.getLeastSignificantBits());
    }

}
