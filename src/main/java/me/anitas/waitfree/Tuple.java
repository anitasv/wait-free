package me.anitas.waitfree;

import lombok.Data;

@Data
public class Tuple<State, Result> {

    private final State state;

    private final Result result;
}
