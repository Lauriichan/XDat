package me.lauriichan.data.xdat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import me.lauriichan.data.xdat.util.Arguments;
import me.lauriichan.data.xdat.util.PrimitiveType;

public class XDatPrimitiveIO extends XDatIO {

    public static final String ID = "primitive";
    public static final XDatPrimitiveIO IO = new XDatPrimitiveIO();

    private final PrimitiveType[] types = PrimitiveType.values();

    private XDatPrimitiveIO() {
        super(ID);
    }

    @Override
    protected int size(Class<?> type, Arguments arguments) {
        return PrimitiveType.get(type).size() + 1;
    }

    @Override
    protected boolean supported(Class<?> type, Arguments arguments) {
        return PrimitiveType.is(type);
    }

    @Override
    protected Object read0(InputStream stream, int size, Arguments arguments) throws Exception {
        DataInputStream input = new DataInputStream(stream);
        byte typeId = input.readByte();
        if (typeId < 0 || typeId >= types.length) {
            throw new IllegalStateException("Invalid type id '" + typeId + "'!");
        }
        switch (types[typeId]) {
        case BYTE:
            return input.readByte();
        case SHORT:
            return input.readShort();
        case INTEGER:
            return input.readInt();
        case LONG:
            return input.readLong();
        case FLOAT:
            return input.readFloat();
        case DOUBLE:
            return input.readDouble();
        }
        return null;
    }

    @Override
    protected void write0(OutputStream stream, int size, Object value, Arguments arguments) throws Exception {
        DataOutputStream output = new DataOutputStream(stream);
        PrimitiveType type = PrimitiveType.get(value.getClass());
        if (type == null) {
            output.writeByte(-1);
            return;
        }
        output.writeByte((byte) type.ordinal());
        Number num = (Number) value;
        switch (type) {
        case BYTE:
            output.writeByte(num.byteValue());
            return;
        case SHORT:
            output.writeShort(num.shortValue());
            return;
        case INTEGER:
            output.writeInt(num.intValue());
            return;
        case LONG:
            output.writeLong(num.longValue());
            return;
        case FLOAT:
            output.writeFloat(num.floatValue());
            return;
        case DOUBLE:
            output.writeDouble(num.doubleValue());
            return;
        }
    }

}
