package me.anitas.waitfree.bst;

import me.anitas.waitfree.Tuple;

public class BstNode<K extends Comparable<K>, V> {

    private final K key;
    private final V value;

    private final BstNode<K, V> left;

    private final BstNode<K, V> right;

    private final int length;

    public BstNode(K key, V value, BstNode<K, V> left, BstNode<K, V> right) {
        this.key = key;
        this.value = value;
        this.left = left;
        this.right = right;
        this.length = size(left) + size(right) + 1;
    }

    public static <K1 extends Comparable<K1>,  V1> int size(BstNode<K1, V1> node) {
        return node != null ? node.length : 0;
    }

    public  static <K1 extends Comparable<K1>, V1> Tuple<BstNode<K1,V1>, V1> getStatic(K1 search) {
        return new Tuple<BstNode<K1,V1>, V1>(null, null);
    }

    public Tuple<BstNode<K,V>, V> get(K search) {
        return new Tuple<>(this, this.getInternal(search));
    }

    public  static <K1 extends Comparable<K1>, V1> Tuple<BstNode<K1,V1>, V1> putStatic(K1 search, V1 newValue) {
        return new Tuple<BstNode<K1,V1>, V1>(new BstNode<K1, V1>(search, newValue, null, null), null);
    }

    public  static <K1 extends Comparable<K1>, V1> Tuple<BstNode<K1,V1>, V1> putIfAbsentStatic(K1 search, V1 newValue) {
        return putStatic(search, newValue);
    }

    public Tuple<BstNode<K,V>, V> put(K search, V newValue) {
        int c = key.compareTo(search);
        if (c == 0) {
            BstNode<K, V> bstNode = new BstNode<>(search, newValue, left, right);
            return new Tuple<>(bstNode, value);
        }

        if (c < 0) {
            Tuple<BstNode<K,V>, V> newLeftResult;
            if (left != null) {
                newLeftResult = left.put(search, newValue);
            } else {
                newLeftResult = putStatic(search, newValue);
            }

            return new Tuple<BstNode<K,V>, V>(new BstNode<K,V>(key, value, newLeftResult.getState(), right),
                    newLeftResult.getResult());
        } else {
            Tuple<BstNode<K,V>, V> newRightResult;
            if (right != null) {
                newRightResult = right.put(search, newValue);
            } else {
                newRightResult = putStatic(search, newValue);
            }

            return new Tuple<BstNode<K,V>, V>(new BstNode<K,V>(key, value, left, newRightResult.getState()),
                    newRightResult.getResult());
        }
    }

    public Tuple<BstNode<K,V>, V> putIfAbsent(K search, V newValue) {
        int c = key.compareTo(search);
        if (c == 0) {
            return new Tuple<>(this, value);
        }

        if (c < 0) {
            Tuple<BstNode<K,V>, V> newLeftResult;
            if (left != null) {
                newLeftResult = left.putIfAbsent(search, newValue);
            } else {
                newLeftResult = putIfAbsentStatic(search, newValue);
            }

            return new Tuple<BstNode<K,V>, V>(new BstNode<K,V>(key, value, newLeftResult.getState(), right),
                    newLeftResult.getResult());
        } else {
            Tuple<BstNode<K,V>, V> newRightResult;
            if (right != null) {
                newRightResult = right.putIfAbsent(search, newValue);
            } else {
                newRightResult = putIfAbsentStatic(search, newValue);
            }

            return new Tuple<BstNode<K,V>, V>(new BstNode<K,V>(key, value, left, newRightResult.getState()),
                    newRightResult.getResult());
        }
    }

    public Tuple<BstNode<K,V>, V> remove(K search) {
        int c = key.compareTo(search);
        if (c == 0) {
            return new Tuple<>(eraseHead(), value);
        }

        if (c < 0) {
            Tuple<BstNode<K,V>, V> newLeftResult;
            if (left != null) {
                newLeftResult = left.remove(search);
            } else {
                newLeftResult = new Tuple<>(null, null);
            }
            return new Tuple<BstNode<K,V>, V>(new BstNode<K,V>(key, value, newLeftResult.getState(), right),
                    newLeftResult.getResult());
        } else {
            Tuple<BstNode<K,V>, V> newRightResult;
            if (right != null) {
                newRightResult = right.remove(search);
            } else {
                newRightResult = new Tuple<>(null, null);
            }
            return new Tuple<BstNode<K,V>, V>(new BstNode<K,V>(key, value, left, newRightResult.getState()),
                    newRightResult.getResult());
        }

    }

    private BstNode<K,V> eraseHead() {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        int c = left.key.compareTo(right.key);
        if (c < 0) {
            return new BstNode<>(left.key, left.value, left.eraseHead(), right);
        } else {
            return new BstNode<>(right.key, right.value, left, right.eraseHead());
        }
    }

    private V getInternal(K search) {
        int c = key.compareTo(search);
        if (c == 0) {
            return this.value;
        }
        BstNode<K,V> deep = c < 0 ? left : right;
        if (deep != null) {
            return deep.getInternal(search);
        }
        return null;
    }

    public static <K extends Comparable<K>, V> boolean containsValue(V value) {
        return false;
    }
}
