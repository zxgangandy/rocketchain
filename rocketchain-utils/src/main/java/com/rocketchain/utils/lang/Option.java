package com.rocketchain.utils.lang;

public abstract class Option<T> {

    public abstract  T toNullable();

    public static <T> Option<T> from(T value )  {
        if (value == null) {
            return new  None();
        } else {
            return new Some(value);
        }
    }
}
