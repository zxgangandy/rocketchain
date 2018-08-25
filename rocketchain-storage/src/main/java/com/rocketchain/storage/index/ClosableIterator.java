package com.rocketchain.storage.index;

import java.util.Iterator;

public interface ClosableIterator<T> extends Iterator<T>, Cloneable {
    void close();
}
