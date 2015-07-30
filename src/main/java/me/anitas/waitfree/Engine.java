package me.anitas.waitfree;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class Engine<State> {

    public class Cell<V> {

        volatile int seq;

        volatile Invocation<State, V> invocation;

        final AtomicReference<Tuple<State, V>> newState = new AtomicReference<>();

        final AtomicReference<Cell> after = new AtomicReference<>();
    }

    private final AtomicReferenceArray<Cell> heads;

    private final AtomicReferenceArray<Cell> announce;

    @SuppressWarnings("unchecked")
    public Engine(int maxThreads,
                  State initialState) {

        Cell anchor = new Cell();

        anchor.seq = 1;
        anchor.newState.set(new Tuple<>(initialState, null));

        this.heads = new AtomicReferenceArray<>(maxThreads);
        this.announce = new AtomicReferenceArray<>(maxThreads);

        for (int i = 0; i < this.heads.length(); i++) {
            this.heads.set(i, anchor);
            this.announce.set(i, anchor);
        }
    }

    public  <V> Tuple<State, V> apply(int p, Invocation<State, V> what) {

        assert 0 < p && p < heads.length();

        Cell<V> mine = new Cell<>();
        mine.seq = 0;
        mine.invocation = what;

        Cell<?> myHead = heads.get(p);
        for (int q = 0; q < this.heads.length(); q++) {
            Cell<?> head = this.heads.get(q);
            myHead = myHead.seq < head.seq ? head : myHead;
        }
        heads.set(p, myHead);

        announce.set(p, mine);

        while (mine.seq == 0) {
            Cell<?> prefer;
            Cell<?> c = heads.get(p);
            Cell<?> help = announce.get(c.seq % announce.length());
            if (help.seq == 0) {
                prefer = help;
            } else {
                prefer = mine;
            }
            c.after.compareAndSet(null, prefer);
            Cell<?> d = c.after.get();
            setNewState(d, c.newState.get().getState());
            d.seq = c.seq + 1;
            heads.set(p, d);
        }

        return mine.newState.get();
    }

    private<V> void setNewState(Cell<V> d, State prevState) {
        Tuple<State, V> result = d.invocation.apply(prevState);
        d.newState.compareAndSet(null, result);
    }
}
