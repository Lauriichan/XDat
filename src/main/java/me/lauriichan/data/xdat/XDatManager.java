package me.lauriichan.data.xdat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.lauriichan.data.xdat.XArchitecture.XField;

public abstract class XDatManager {

    protected final XDatTable table;

    public XDatManager() {
        this(new XDatTable());
    }

    public XDatManager(final XDatTable table) {
        this.table = table;
    }

    public XDatTable getTable() {
        return table;
    }

    protected abstract void log(String message, Exception exception);

    protected abstract boolean exists(String dataKey) throws IOException;

    protected abstract void delete(String dataKey) throws IOException;

    protected InputStream openBufferedInput(String dataKey) throws IOException {
        return new BufferedInputStream(openInput(dataKey));
    }

    protected abstract InputStream openInput(String dataKey) throws IOException;

    protected OutputStream openBufferedOutput(String dataKey) throws IOException {
        return new BufferedOutputStream(openOutput(dataKey));
    }

    protected abstract OutputStream openOutput(String dataKey) throws IOException;

    public <T> List<T> get(Class<T> type) throws IOException {
        XArchitecture architecture = table.getArchitecture(type);
        if (architecture == null || !exists(architecture.getDataKey())) {
            return Collections.emptyList();
        }
        int size = architecture.getSize();
        ArrayList<T> list = new ArrayList<>();
        try (InputStream stream = openBufferedInput(architecture.getDataKey())) {
            while (stream.available() != 0) {
                stream.mark(size);
                try {
                    Object value = architecture.read(stream);
                    if (value != null && type.isAssignableFrom(value.getClass())) {
                        list.add(type.cast(value));
                        continue;
                    }
                } catch (Exception exp) {
                    log("Failed to load object of type '" + type.getSimpleName() + "'", exp);
                    stream.reset();
                    stream.skip(size);
                }
            }
        }
        return list;
    }

    public <T> List<T> get(Class<T> type, XFilter... filters) throws IOException {
        if (filters.length == 0) {
            return get(type);
        }
        XArchitecture architecture = table.getArchitecture(type);
        if (architecture == null || !exists(architecture.getDataKey())) {
            return Collections.emptyList();
        }
        ArrayList<String> missing = new ArrayList<>();
        missing.addAll(architecture.getFields().keySet());
        for (XFilter filter : filters) {
            if (filter == null) {
                continue;
            }
            missing.remove(filter.getKey());
        }
        if (missing.size() == architecture.getFields().size()) {
            return get(type);
        }
        int size = architecture.getSize();
        ArrayList<T> list = new ArrayList<>();
        InputStream stream = null;
        try {
            stream = openBufferedInput(architecture.getDataKey());
            loop:
            while (stream.available() != 0) {
                if (stream.available() < size) {
                    break;
                }
                try {
                    Object value = architecture.create();
                    if (value == null || !type.isAssignableFrom(value.getClass())) {
                        stream.skip(size);
                        continue;
                    }
                    for (XFilter filter : filters) {
                        if (filter == null) {
                            continue;
                        }
                        XField field = architecture.getField(filter.getKey());
                        if (field == null) {
                            continue;
                        }
                        stream.mark(size);
                        stream.skip(field.offset());
                        field.read(value, stream);
                        stream.reset();
                        if (!filter.isAllowed(value)) {
                            stream.skip(size);
                            continue loop;
                        }
                    }
                    for (String key : missing) {
                        XField field = architecture.getField(key);
                        if (field == null) {
                            continue;
                        }
                        stream.mark(size);
                        stream.skip(field.offset());
                        field.read(value, stream);
                        stream.reset();
                    }
                    stream.skip(size);
                    list.add(type.cast(value));
                } catch (Exception exp) {
                    log("Failed to load object of type '" + type.getSimpleName() + "'", exp);
                    stream.reset();
                    stream.skip(size);
                }
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return list;
    }

    public <T> int delete(Class<T> type, XFilter... filters) throws IOException {
        if (filters.length == 0) {
            return -2;
        }
        XArchitecture architecture = table.getArchitecture(type);
        if (architecture == null || !exists(architecture.getDataKey())) {
            return -1;
        }
        ArrayList<String> missing = new ArrayList<>();
        missing.addAll(architecture.getFields().keySet());
        for (XFilter filter : filters) {
            if (filter == null) {
                continue;
            }
            missing.remove(filter.getKey());
        }
        if (missing.size() == architecture.getFields().size()) {
            return -2;
        }
        int size = architecture.getSize();
        int deleted = 0;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            try (InputStream stream = openBufferedInput(architecture.getDataKey())) {
                byte[] buffer = new byte[size];
                loop:
                while (stream.available() != 0) {
                    if (stream.available() < size) {
                        break;
                    }
                    try {
                        Object value = architecture.create();
                        if (value == null || !type.isAssignableFrom(value.getClass())) {
                            stream.skip(size);
                            continue;
                        }
                        for (XFilter filter : filters) {
                            if (filter == null) {
                                continue;
                            }
                            XField field = architecture.getField(filter.getKey());
                            if (field == null) {
                                continue;
                            }
                            stream.mark(size);
                            stream.skip(field.offset());
                            field.read(value, stream);
                            stream.reset();
                            if (!filter.isAllowed(value)) {
                                stream.read(buffer);
                                output.write(buffer);
                                continue loop;
                            }
                        }
                        deleted++;
                        stream.skip(size);
                    } catch (Exception exp) {
                        stream.reset();
                        stream.read(buffer);
                        output.write(buffer);
                    }
                }
            }
            delete(architecture.getDataKey());
            try (OutputStream stream = openOutput(architecture.getDataKey())) {
                stream.write(output.toByteArray());
                stream.flush();
            }
        }
        return deleted;
    }

    public <T> boolean save(T object, Class<T> type) throws IOException {
        XArchitecture architecture = table.getArchitecture(type);
        if (architecture == null) {
            return false;
        }
        try (OutputStream stream = openOutput(architecture.getDataKey())) {
            architecture.write(object, stream);
            stream.flush();
            return true;
        } catch (Exception e) {
            log("Failed to save object of type '" + type.getSimpleName() + "'", e);
            return false;
        }
    }

}
