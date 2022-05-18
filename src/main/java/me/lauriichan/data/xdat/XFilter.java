package me.lauriichan.data.xdat;

import java.util.Objects;
import java.util.function.Predicate;

public final class XFilter {

    private final String key;
    private final Predicate<Object> filter;

    private XFilter(final String key, final Predicate<Object> filter) {
        this.key = Objects.requireNonNull(key);
        this.filter = Objects.requireNonNull(filter);
    }

    public String getKey() {
        return key;
    }

    public Predicate<Object> getFilter() {
        return filter;
    }

    public boolean isAllowed(Object value) {
        return filter.test(value);
    }

    public static XFilter of(String key, Predicate<Object> filter) {
        return new XFilter(key, filter);
    }

}
