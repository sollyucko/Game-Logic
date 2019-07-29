package io.github.sollyucko.threading;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class Generator<E> implements Iterator<E> {
	private final Thread thread;
	private final PausingSingletonQueue<E> queue;
	private boolean isDone;
	private boolean hasValue;
	private E value;
	
	public Generator(final Consumer<Consumer<E>> func) {
		this.hasValue = false;
		this.isDone = false;
		this.queue = new PausingSingletonQueue<>();
		this.thread = new Thread(() -> {
			this.queue.beforeSend();
			func.accept(value -> {
				this.queue.send(value);
				this.queue.beforeSend();
			});
			this.isDone = true;
			this.queue.send(null);
		});
		this.thread.start();
	}
	
	@Override
	public boolean hasNext() {
		synchronized(this.thread) {
			if(this.hasValue) {
				return true;
			}
			
			this.queue.beforeReceive();
			this.value = this.queue.receive();
			this.hasValue = true;
			return !this.isDone;
		}
	}
	
	@Override
	public E next() {
		synchronized(this.thread) {
			if(!this.hasNext()) { // hasNext has critical side-effects!
				throw new NoSuchElementException();
			}
			this.hasValue = false;
			return this.value;
		}
	}
}
