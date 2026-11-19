package net.environmentz.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class Event<T> {

    private final Function<List<T>, T> invokerFactory;
    private final List<T> listeners = new ArrayList<>();
    private T invoker;

    private Event(Function<List<T>, T> invokerFactory) {
        this.invokerFactory = invokerFactory;
        this.invoker = invokerFactory.apply(List.of());
    }

    public static <T> Event<T> create(Function<List<T>, T> invokerFactory) {
        return new Event<>(invokerFactory);
    }

    public synchronized void register(T listener) {
        this.listeners.add(listener);
        this.invoker = this.invokerFactory.apply(List.copyOf(this.listeners));
    }

    public T invoker() {
        return this.invoker;
    }
}
