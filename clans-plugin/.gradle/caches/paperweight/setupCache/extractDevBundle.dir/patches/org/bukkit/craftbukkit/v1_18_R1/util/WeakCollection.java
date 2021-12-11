package org.bukkit.craftbukkit.v1_18_R1.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.lang.Validate;

public final class WeakCollection<T> implements Collection<T> {
    static final Object NO_VALUE = new Object();
    private final Collection<WeakReference<T>> collection;

    public WeakCollection() {
        this.collection = new ArrayList<WeakReference<T>>();
    }

    @Override
    public boolean add(T value) {
        Validate.notNull(value, "Cannot add null value");
        return this.collection.add(new WeakReference<T>(value));
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        Collection<WeakReference<T>> values = this.collection;
        boolean ret = false;
        for (T value : collection) {
            Validate.notNull(value, "Cannot add null value");
            ret |= values.add(new WeakReference<T>(value));
        }
        return ret;
    }

    @Override
    public void clear() {
        this.collection.clear();
    }

    @Override
    public boolean contains(Object object) {
        if (object == null) {
            return false;
        }
        for (T compare : this) {
            if (object.equals(compare)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.toCollection().containsAll(collection);
    }

    @Override
    public boolean isEmpty() {
        return !this.iterator().hasNext();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Iterator<WeakReference<T>> it = WeakCollection.this.collection.iterator();
            Object value = WeakCollection.NO_VALUE;

            @Override
            public boolean hasNext() {
                Object value = this.value;
                if (value != null && value != WeakCollection.NO_VALUE) {
                    return true;
                }

                Iterator<WeakReference<T>> it = this.it;
                value = null;

                while (it.hasNext()) {
                    WeakReference<T> ref = it.next();
                    value = ref.get();
                    if (value == null) {
                        it.remove();
                    } else {
                        this.value = value;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public T next() throws NoSuchElementException {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements");
                }

                @SuppressWarnings("unchecked")
                T value = (T) this.value;
                this.value = WeakCollection.NO_VALUE;
                return value;
            }

            @Override
            public void remove() throws IllegalStateException {
                if (value != WeakCollection.NO_VALUE) {
                    throw new IllegalStateException("No last element");
                }

                value = null;
                it.remove();
            }
        };
    }

    @Override
    public boolean remove(Object object) {
        if (object == null) {
            return false;
        }

        Iterator<T> it = this.iterator();
        while (it.hasNext()) {
            if (object.equals(it.next())) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        Iterator<T> it = this.iterator();
        boolean ret = false;
        while (it.hasNext()) {
            if (collection.contains(it.next())) {
                ret = true;
                it.remove();
            }
        }
        return ret;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        Iterator<T> it = this.iterator();
        boolean ret = false;
        while (it.hasNext()) {
            if (!collection.contains(it.next())) {
                ret = true;
                it.remove();
            }
        }
        return ret;
    }

    @Override
    public int size() {
        int s = 0;
        for (T value : this) {
            s++;
        }
        return s;
    }

    @Override
    public Object[] toArray() {
        return this.toArray(new Object[0]);
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return this.toCollection().toArray(array);
    }

    private Collection<T> toCollection() {
        ArrayList<T> collection = new ArrayList<T>();
        for (T value : this) {
            collection.add(value);
        }
        return collection;
    }
}
