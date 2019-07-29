package io.github.sollyucko.threading;

public class SingletonQueue<E> {
	private final Object lock = new Object();
	private boolean valueSet;
	private E value;
	
	public E receive() {
		synchronized(this.lock) {
			while(!this.valueSet) {
				try {
					this.lock.wait();
				} catch(final InterruptedException ex) {}
			}
			this.valueSet = false;
			this.lock.notifyAll();
			final E result = this.value;
			this.value = null;
			return result;
		}
	}
	
	public void send(final E value) {
		synchronized(this.lock) {
			while(this.valueSet) {
				try {
					this.lock.wait();
				} catch(final InterruptedException ex) {}
			}
			this.valueSet = true;
			this.value = value;
			this.lock.notifyAll();
		}
	}
}
