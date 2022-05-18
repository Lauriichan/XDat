package me.lauriichan.data.xdat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import me.lauriichan.data.xdat.util.Arguments;
import me.lauriichan.data.xdat.util.JavaAccessor;
import me.lauriichan.data.xdat.util.PrimitiveType;

public final class XArchitecture {

    private final long id;
    private final Class<?> type;

    private final String dataKey;

    private final Map<String, XField> fields;
    private final int size;

    public XArchitecture(final long id, final Class<?> type) {
        this.id = id;
        this.type = type;
        this.dataKey = type.getSimpleName() + '_' + id;
        if (JavaAccessor.getConstructor(type) == null) {
            throw new IllegalArgumentException(
                "Type '" + type.getSimpleName() + "' can't be build because there is constructor without arguments");
        }
        final TreeMap<String, XField> fieldMap = new TreeMap<>();
        Field[] fields = JavaAccessor.getFields(type);
        int size = 0;
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            XData info = JavaAccessor.getAnnotation(field, XData.class);
            if (info == null) {
                continue;
            }
            XField xField = new XField(size, info, field);
            if (xField.ioSize() == -1) {
                continue;
            }
            fieldMap.put(field.getName(), xField);
            size += xField.ioSize();
        }
        if (size == 0) {
            throw new IllegalArgumentException("Empty Type '" + type.getSimpleName() + "'!");
        }
        this.size = size;
        this.fields = Collections.unmodifiableMap(fieldMap);
    }

    public long getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public String getDataKey() {
        return dataKey;
    }

    public Map<String, XField> getFields() {
        return fields;
    }

    public XField getField(String key) {
        return fields.get(key);
    }

    public boolean hasField(String key) {
        return fields.containsKey(key);
    }

    public Class<?> getType() {
        return type;
    }

    public Object create() {
        return JavaAccessor.instance(type);
    }

    public void write(Object target, OutputStream stream) throws Exception {
        for (XField field : fields.values()) {
            field.write(target, stream);
        }
    }

    public Object readBuff(InputStream stream) throws Exception {
        if (!stream.markSupported()) {
            return null;
        }
        Object object = JavaAccessor.instance(type);
        if (object == null) {
            return null;
        }
        readBuff(object, stream);
        return object;
    }

    public void readBuff(Object target, InputStream stream) throws Exception {
        if (!stream.markSupported()) {
            return;
        }
        for (XField field : fields.values()) {
            field.readBuff(target, stream);
        }
    }

    public Object read(InputStream stream) throws Exception {
        Object object = JavaAccessor.instance(type);
        if (object == null) {
            return null;
        }
        read(object, stream);
        return object;
    }

    public void read(Object target, InputStream stream) throws Exception {
        for (XField field : fields.values()) {
            field.read(target, stream);
        }
    }

    public static final class XField {

        private final int offset;
        private final XData info;
        private final Field field;

        private final XDatIO ioHandler;
        private final Arguments ioArguments;

        private final int ioSize;
        private final PrimitiveType primitive;

        XField(int offset, XData info, Field field) {
            this.info = info;
            this.field = field;
            this.offset = offset;
            this.ioHandler = XDatIO.get(info.ioId());
            this.ioArguments = new Arguments(info.ioArgs());
            this.ioSize = (ioHandler == null || !ioHandler.supported(field.getType(), ioArguments)) ? -1
                : ioHandler.size(field.getType(), ioArguments);
            this.primitive = PrimitiveType.get(field.getType());
        }

        public int offset() {
            return offset;
        }

        public XData info() {
            return info;
        }

        public Field field() {
            return field;
        }

        public int ioSize() {
            return ioSize;
        }

        public XDatIO ioHandler() {
            return ioHandler;
        }

        public Arguments ioArguments() {
            return ioArguments;
        }

        public void readBuff(Object target, InputStream stream) throws IOException {
            if (ioSize == -1 || !stream.markSupported()) {
                return;
            }
            stream.mark(ioSize);
            try {
                read(target, stream);
            } catch (Exception exp) {
                stream.reset();
            }
        }

        public void read(Object target, InputStream stream) throws Exception {
            if (ioSize == -1) {
                return;
            }
            Object object = ioHandler.read(stream, ioSize, ioArguments);
            if (Number.class.isAssignableFrom(field.getType())) {
                if (object == null) {
                    JavaAccessor.setObjectValue(target, field, 0);
                    return;
                }
                if (primitive != null) {
                    Number num = (Number) object;
                    switch (primitive) {
                    case BYTE:
                        JavaAccessor.setObjectValue(target, field, num.byteValue());
                        break;
                    case SHORT:
                        JavaAccessor.setObjectValue(target, field, num.shortValue());
                        break;
                    case INTEGER:
                        JavaAccessor.setObjectValue(target, field, num.intValue());
                        break;
                    case LONG:
                        JavaAccessor.setObjectValue(target, field, num.longValue());
                        break;
                    case FLOAT:
                        JavaAccessor.setObjectValue(target, field, num.floatValue());
                        break;
                    case DOUBLE:
                        JavaAccessor.setObjectValue(target, field, num.doubleValue());
                        break;
                    }
                    return;
                }
            }
            JavaAccessor.setObjectValue(target, field, object);
        }

        public void write(Object target, OutputStream stream) throws Exception {
            if (ioSize == -1) {
                return;
            }
            Object value = JavaAccessor.getObjectValue(target, field);
            ioHandler.write(stream, ioSize, value, ioArguments);
        }

    }

}
