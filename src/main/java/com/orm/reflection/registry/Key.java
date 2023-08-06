package com.orm.reflection.registry;

import java.util.Arrays;
import java.util.Objects;


public class Key {
    private final Object[] keys;
    private final int hashCode;

    public Key(Object... keys) {
        this.keys = keys;
        this.hashCode = Objects.hash(this.keys);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Key key = (Key) o;
        if (this.keys.length != key.keys.length) return false;
        for (int index = 0; index < this.keys.length; index++) {
            if (!Objects.equals(this.keys[index], key.keys[index])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return "Key{" +
                "keys=" + Arrays.toString(this.keys) +
                ", hashCode=" + this.hashCode +
                '}';
    }
}
