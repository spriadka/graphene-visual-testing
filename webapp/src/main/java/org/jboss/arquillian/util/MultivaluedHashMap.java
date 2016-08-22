package org.jboss.arquillian.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

public class MultivaluedHashMap<K, V> extends ForwardingMap<K, List<V>> implements MultivaluedMap<K, V> {

    public static MultivaluedMap<?, ?> EMPTY = new MultivaluedHashMap<Object, Object>();

    public static <K, V> MultivaluedHashMap<K, V> empty() {
        return (MultivaluedHashMap<K, V>) EMPTY;
    }

    private Map<K, List<V>> map = new HashMap<K, List<V>>();

    @Override
    protected Map<K, List<V>> delegate() {
        return map;
    }

    @Override
    public void putSingle(K key, V value) {
        List<V> l = new ArrayList<V>(1);
        l.add(value);
        put(key, l);
    }

    @Override
    public void add(K key, V value) {
        List<V> l = get(key);
        if (l == null) {
            l = new ArrayList<V>(1);
            put(key, l);
        }
        l.add(value);
    }

    @Override
    public V getFirst(K key) {
        List<V> l = get(key);
        return l == null ? null : l.get(0);
    }

    @Override
    public void addAll(K k, V... vs) {
        if (vs == null){
            throw new NullPointerException("Supplied array of values must not be null");
        }
        if (vs.length == 0){
            return;
        }
        List<V> values = map.get(k);
        for (V value : vs){
            if (value != null){
                values.add(value);
            }
            //IGNORE NULLS
        }
        map.put(k, values);
    }

    @Override
    public void addAll(K k, List<V> list) {
        if (list == null){
            throw new NullPointerException("Supplied list of values must not be null");
        }
        if (list.isEmpty()){
            return;
        }
        List<V> values = map.get(k);
        for (V value : values){
            if (value != null){
                values.add(value);
            }
        }
        map.put(k, values);
    }

    @Override
    public void addFirst(K k, V v) {
        List<V> vs = null;
        if (map.containsKey(k)){
            vs = map.get(k);
            vs.add(0, v);
            map.put(k, vs);
        }
        vs.add(v);
        map.put(k, vs);
    }

    @Override
    public boolean equalsIgnoreValueOrder(MultivaluedMap<K, V> mm) {
        if (this == mm){
            return true;
        }
        if (!this.keySet().equals(mm.keySet())){
            return false;
        }
        for (Entry<K, List<V>> e : map.entrySet()){
            List<V> list = mm.get(e.getKey());
            if (e.getValue().size() != list.size()){
                return false;
            }
            for (V v : e.getValue()){
                if (!list.contains(v)){
                    return false;
                }
            }
        }
        return true;
    }

}
