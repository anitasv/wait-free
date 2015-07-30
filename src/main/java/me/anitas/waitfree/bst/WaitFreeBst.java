package me.anitas.waitfree.bst;

import me.anitas.waitfree.Engine;
import me.anitas.waitfree.Tuple;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class WaitFreeBst<K extends Comparable<K>, V> implements ConcurrentMap<K, V> {

    private final Engine<BstNode<K, V>> engine;

    public WaitFreeBst(int maxThreads) {
        this.engine = new Engine<>(maxThreads, null);
    }

    private final AtomicInteger threadId = new AtomicInteger();

    private final ThreadLocal<Integer> threadIdMap = new ThreadLocal<Integer>() {
        @Override
        public Integer initialValue() {
            return threadId.incrementAndGet();
        }
    };
    @Override
    public int size() {
        return this.engine.apply(threadIdMap.get(), new BstSize<K, V>()).getResult();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsValue(Object value) {
        return this.engine.apply(threadIdMap.get(), new BstContainsValue<K, V>((V)value)).getResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        return this.engine.apply(threadIdMap.get(), new BstGet<K, V>((K)key)).getResult();
    }

    public V put(K key, V value) {
        return (V) this.engine.apply(threadIdMap.get(), new BstPut<K, V>(key, value)).getResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        return this.engine.apply(threadIdMap.get(), new BstRemove<K, V>((K) key)).getResult();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.engine.apply(threadIdMap.get(), kvBstNode -> new Tuple<>(null, null));
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet;
    }

    public V putIfAbsent(K key, V value) {
        return (V) this.engine.apply(threadIdMap.get(), new BstPutIfAbsent<K, V>(key, value)).getResult();
    }

    @Override
    public boolean remove(Object key, Object value) {
        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return false;
    }

    @Override
    public V replace(K key, V value) {
        return null;
    }

    private Set<K> keySet = new Set<K>() {

        @Override
        public int size() {
            return WaitFreeBst.this.size();
        }

        @Override
        public boolean isEmpty() {
            return WaitFreeBst.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return WaitFreeBst.this.containsKey(o);
        }

        @Override
        public Iterator<K> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(K k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            return WaitFreeBst.this.remove(o) != null;
        }

        @Override
        public boolean containsAll(Collection<?> c) {

            for (Object obj : c) {
                if (!contains(obj)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends K> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean atLeastOne = false;
            for (Object obj : c) {
                atLeastOne = atLeastOne || remove(obj);
            }

            return atLeastOne;
        }

        @Override
        public void clear() {
            WaitFreeBst.this.clear();
        }
    };

    private Set<Map.Entry<K, V>> entrySet = new Set<Map.Entry<K, V>>() {

        @Override
        public int size() {
            return WaitFreeBst.this.size();
        }

        @Override
        public boolean isEmpty() {
            return WaitFreeBst.this.isEmpty();
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            Map.Entry<K, V> entry =  (Map.Entry<K, V>) o;
            return entry.getValue().equals(WaitFreeBst.this.get(entry.getKey()));
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(Map.Entry<K, V> e) {
            // TODO: Proper implementation of this is a bit painful.
            return WaitFreeBst.this.put(e.getKey(), e.getValue()) != null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            Map.Entry<K, V> entry =  (Map.Entry<K, V>) o;
            return WaitFreeBst.this.remove(entry.getKey()) != null;
        }

        @Override
        public boolean containsAll(Collection<?> c) {

            for (Object obj : c) {
                if (!contains(obj)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Map.Entry<K, V>> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean atLeastOne = false;
            for (Object obj : c) {
                atLeastOne = atLeastOne || remove(obj);
            }

            return atLeastOne;
        }

        @Override
        public void clear() {
            WaitFreeBst.this.clear();
        }
    };

}
