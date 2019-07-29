package io.github.sollyucko.threading;

public class PausingSingletonQueue<E> {
	private final Object lock = new Object();
	private boolean valueSet = false;
	private boolean valueNeeded = false;
	private E value;
	
	public void beforeReceive() {
		synchronized(this.lock) {
			assert !this.valueNeeded;
			this.valueNeeded = true;
			while(!this.valueSet) {
				try {
					this.lock.notifyAll();
					this.lock.wait();
				} catch(final InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public void beforeSend() {
		synchronized(this.lock) {
			while(!this.valueNeeded || this.valueSet) {
				try {
					this.lock.notifyAll();
					this.lock.wait();
				} catch(final InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public E receive() {
		synchronized(this.lock) {
			final E result = this.value;
			this.valueSet = false;
			this.valueNeeded = false;
			this.value = null;
			this.lock.notifyAll();
			return result;
		}
	}
	
	public void send(final E value) {
		synchronized(this.lock) {
			this.valueSet = true;
			this.value = value;
			this.lock.notifyAll();
		}
	}
}
