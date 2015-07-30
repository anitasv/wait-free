package me.anitas.waitfree;

import java.util.function.Function;

public interface Invocation<State, V> extends Function<State, Tuple<State, V>> {

}
