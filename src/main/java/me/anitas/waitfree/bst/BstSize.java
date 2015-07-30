package me.anitas.waitfree.bst;

import me.anitas.waitfree.Tuple;

public class BstSize<K extends Comparable<K>, V> implements me.anitas.waitfree.Invocation<BstNode<K,V>, Integer> {

    @Override
    public Tuple<BstNode<K, V>, Integer> apply(BstNode<K, V> kvBstNode) {
        return new Tuple<>(kvBstNode, BstNode.size(kvBstNode));
    }
}
