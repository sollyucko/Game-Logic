package io.github.sollyucko;

@FunctionalInterface
public interface IntIntObjConsumer<T> {
	void accept(int _a, int _b, T _c);
}
