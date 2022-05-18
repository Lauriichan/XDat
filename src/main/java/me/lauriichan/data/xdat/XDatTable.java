package me.lauriichan.data.xdat;

import java.util.concurrent.ConcurrentHashMap;

import me.lauriichan.data.xdat.util.JavaAccessor;

public final class XDatTable {

    private final ConcurrentHashMap<Long, XArchitecture> map = new ConcurrentHashMap<>();

    XDatTable() {}

    public void register(Class<?> type) {
        Long id = getId(type);
        if (map.containsKey(id)) {
            throw new IllegalArgumentException("Entity with id '" + id.toString() + "' is already registered!");
        }
        map.put(id, new XArchitecture(id, type));
    }

    public final Long getIdAndRegister(Class<?> type) {
        XEntity entity = JavaAccessor.getAnnotation(type, XEntity.class);
        if (entity == null) {
            return null;
        }
        if (!map.containsKey(entity.id())) {
            register(type);
        }
        return entity.id();
    }

    public final Long getId(Class<?> type) {
        XEntity entity = JavaAccessor.getAnnotation(type, XEntity.class);
        if (entity == null) {
            return null;
        }
        return entity.id();
    }

    public final XArchitecture getArchitecture(Class<?> type) {
        Long id = getIdAndRegister(type);
        if (id == null) {
            return null;
        }
        return map.get(id);
    }

    public final XArchitecture getArchitecture(long id) {
        return map.get(id);
    }

}
