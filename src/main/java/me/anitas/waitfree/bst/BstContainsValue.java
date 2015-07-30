package me.anitas.waitfree.bst;

import lombok.RequiredArgsConstructor;
import me.anitas.waitfree.Invocation;
import me.anitas.waitfree.Tuple;

@RequiredArgsConstructor
public class BstContainsValue<K extends Comparable<K>, V> implements Invocation<BstNode<K, V>, Boolean> {

    private final V value;

    @Override
    public Tuple<BstNode<K, V>, Boolean> apply(BstNode<K, V> kvBstNode) {
        return new Tuple<>(kvBstNode, BstNode.containsValue(value));
    }
}
