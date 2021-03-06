package me.anitas.waitfree.bst;

import lombok.RequiredArgsConstructor;
import me.anitas.waitfree.Invocation;
import me.anitas.waitfree.Tuple;

@RequiredArgsConstructor
public class BstPutIfAbsent <K extends Comparable<K>, V> implements Invocation<BstNode<K, V>, V> {

    private final K search;

    private final V newValue;

    @Override
    public Tuple<BstNode<K, V>, V> apply(BstNode<K, V> kvBstNode) {
        return kvBstNode != null ? kvBstNode.putIfAbsent(search, newValue) :
                BstNode.putIfAbsentStatic(search, newValue);
    }
}
