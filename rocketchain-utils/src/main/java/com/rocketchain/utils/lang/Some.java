package com.rocketchain.utils.lang;

public class Some<T> extends Option<T> {
    private T value;
    public Some(T value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value == null ? 0 : value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj instanceof Some) {
            return value == ((Some) obj).value;
        } else {
            return false;
        }
    }


    @Override
    public T toNullable() {
        return value;
    }

    public T getValue() {
        return value;
    }
}
