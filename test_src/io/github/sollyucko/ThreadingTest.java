package io.github.sollyucko;

import io.github.sollyucko.threading.*;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadingTest {
	@RepeatedTest(10)
	void testPausingSingletonQueue() {
		final PausingSingletonQueue<Integer> queue = new PausingSingletonQueue<>();
		final Thread sender = new Thread(() -> {
			System.out.println("Before sending");
			for(int i = 0; i < 10; ++i) {
				queue.beforeSend();
				System.out.println("Going to send " + i);
				queue.send(i);
				System.out.println("Sent " + i);
			}
		});
		System.out.println("Before starting");
		sender.start();
		System.out.println("Before receiving");
		for(int i = 0; i < 10; ++i) {
			queue.beforeReceive();
			System.out.println("Going to receive " + i);
			assertEquals(i, queue.receive());
			System.out.println("Received " + i);
		}
		sender.stop();
		System.out.flush();
		try {
			Thread.sleep(1000);
		} catch(final InterruptedException e) {
			e.printStackTrace();
		}
		System.out.flush();
	}
	
	@Test
	void testSimpleGenerator() {
		final Iterator<Integer> iterator = new Generator<>(yield -> {
			System.out.println("Before yielding");
			for(int i = 0; i < 10; ++i) {
				yield.accept(i);
				System.out.println("Yielded " + i);
			}
			System.out.println("Done yielding!");
		});
		System.out.println("Before getting");
		for(int i = 0; i < 10; ++i) {
			assertTrue(iterator.hasNext());
			System.out.println("Can get " + i);
			assertEquals(i, iterator.next());
			System.out.println("Got " + i);
		}
		System.out.println("Done getting!");
	}
	
	@RepeatedTest(10)
	void testSingletonQueue() {
		final SingletonQueue<Integer> queue = new SingletonQueue<>();
		final Thread sender = new Thread(() -> {
			System.out.println("Before sending");
			for(int i = 0; i < 10; ++i) {
				queue.send(i);
				System.out.println("Sent " + i);
			}
		});
		System.out.println("Before starting");
		sender.start();
		System.out.println("Before receiving");
		for(int i = 0; i < 10; ++i) {
			assertEquals(i, queue.receive());
			System.out.println("Received " + i);
		}
	}
}