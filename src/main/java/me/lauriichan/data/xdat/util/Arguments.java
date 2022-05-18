package me.lauriichan.data.xdat.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Arguments {

    private final Map<String, String> map;

    public Arguments(String[] arguments) {
        String key = null;
        final HashMap<String, String> map = new HashMap<>();
        for (String argument : arguments) {
            if (key != null) {
                map.put(key, argument);
                key = null;
                continue;
            }
            key = argument;
        }
        this.map = Collections.unmodifiableMap(map);
    }

    public Map<String, String> getMap() {
        return map;
    }

    public boolean has(String key) {
        return map.containsKey(key);
    }

    public String get(String key) {
        return map.get(key);
    }

    public byte getByte(String key) {
        return Byte.parseByte(map.get(key));
    }

    public short getShort(String key) {
        return Short.parseShort(map.get(key));
    }

    public int getInt(String key) {
        return Integer.parseInt(map.get(key));
    }

    public long getLong(String key) {
        return Long.parseLong(map.get(key));
    }

    public float getFloat(String key) {
        return Float.parseFloat(map.get(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(map.get(key));
    }

}
