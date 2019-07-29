package io.github.sollyucko;

import java.util.*;
import java.util.function.*;

public final class Utils {
	private Utils() {}
	
	/**
	 * { @code Iterable }s are thread-safe; { @code Iterator }s are not.
	 **/
	public static <E> Iterable<E> cache(final Iterable<E> iterable) {
		return new CachingIterable<>(iterable);
	}
	
	public static <T, A> Iterable<T> cartesianProduct(final Function<? super A, ? extends T> combiner,
	                                                  final Iterable<A> a) {
		return map(combiner, a);
	}
	
	/**
	 * Diagonalizes, allowing both iterables to be infinite, while still reaching each element in a finite number of steps.
	 **/
	public static <T, A, B> Iterable<T> cartesianProduct(final BiFunction<? super A, ? super B, ? extends T> combiner,
	                                                     final Iterable<A> a, final Iterable<B> b) {
		return () -> new Iterator<T>() {
			private final Iterator<A> aIterator = a.iterator();
			private final ArrayList<A> aCache = new ArrayList<>();
			private final Iterator<B> bIterator = b.iterator();
			private final ArrayList<B> bCache = new ArrayList<>();
			private int i = 0;
			private int jMin = 0;
			private int jMax = 1;
			private int j = 0;
			
			private A aGet(final int i) {
				if(i < this.aCache.size()) {
					return this.aCache.get(i);
				}
				final A result = this.aIterator.next();
				this.aCache.add(result);
				return result;
			}
			
			private B bGet(final int i) {
				if(i < this.bCache.size()) {
					return this.bCache.get(i);
				}
				final B result = this.bIterator.next();
				this.bCache.add(result);
				return result;
			}
			
			@Override
			public boolean hasNext() {
				if(this.j == this.jMax) {
					if(this.aIterator.hasNext()) {
						++this.jMax;
					}
					if(!this.bIterator.hasNext()) {
						++this.jMin;
					}
					this.j = this.jMin;
					++this.i;
				}
				//@formatter:off
				return    this.aIterator.hasNext()
				       || this.bIterator.hasNext()
				       || this.i < this.aCache.size() + this.bCache.size() - 1
				       || this.j < this.jMax;
				//@formatter:on
			}
			
			@Override
			public T next() {
				if(!this.hasNext()) // hasNext has useful side-effects!
					throw new NoSuchElementException();
				final T result = combiner.apply(this.aGet(this.j), this.bGet(this.i - this.j));
				++this.j;
				System.out.println(result);
				return result;
			}
		};
	}
	
	public static <T> Iterable<T> cartesianProduct(final Supplier<? extends T> combiner) {
		return new LazySingletonIterable<>(combiner);
	}
	
	public static <T, U> Consumer<T> compose(final Consumer<? super U> a, final Function<? super T, ? extends U> b) {
		return x -> a.accept(b.apply(x));
	}
	
	private static <U, T> Spliterator<U> map(final Function<? super T, ? extends U> mapper,
	                                         final Spliterator<? extends T> spliterator) {
		return new MappedSpliterator<>(spliterator, mapper);
	}
	
	private static <T, U> Iterator<U> map(final Function<? super T, ? extends U> mapper,
	                                      final Iterator<? extends T> iterator) {
		return new Iterator<U>() {
			@Override
			public void forEachRemaining(final Consumer<? super U> consumer) {
				iterator.forEachRemaining(compose(consumer, mapper));
			}
			
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}
			
			@Override
			public U next() {
				return mapper.apply(iterator.next());
			}
			
			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}
	
	private static <T, U> Iterable<U> map(final Function<? super T, ? extends U> mapper,
	                                      final Iterable<? extends T> iterable) {
		return new Iterable<U>() {
			@Override
			public void forEach(final Consumer<? super U> consumer) {
				iterable.forEach(compose(consumer, mapper));
			}
			
			@Override
			public Iterator<U> iterator() {
				return map(mapper, iterable.iterator());
			}
			
			@Override
			public Spliterator<U> spliterator() {
				return map(mapper, iterable.spliterator());
			}
		};
	}
	
	/**
	 * { @code Iterable }s are thread-safe; { @code Iterator }s are not.
	 **/
	private static class CachingIterable<E> implements Iterable<E> {
		private final Iterator<E> iterator;
		private final List<E> cache;
		
		public CachingIterable(final Iterable<E> iterable) {
			this.iterator = iterable.iterator();
			this.cache = new ArrayList<>();
		}
		
		@Override
		public Iterator<E> iterator() {
			return new Iterator<E>() {
				private int i = 0;
				
				@Override
				public boolean hasNext() {
					if(this.i < CachingIterable.this.cache.size()) {
						return true;
					}
					
					synchronized(CachingIterable.this.cache) {
						return this.i < CachingIterable.this.cache.size() || CachingIterable.this.iterator.hasNext();
					}
				}
				
				@Override
				public E next() {
					if(this.i < CachingIterable.this.cache.size()) {
						return CachingIterable.this.cache.get(this.i++);
					} else {
						synchronized(CachingIterable.this.cache) {
							if(this.i < CachingIterable.this.cache.size()) {
								return CachingIterable.this.cache.get(this.i++);
							} else {
								final E result = CachingIterable.this.iterator.next();
								CachingIterable.this.cache.set(this.i++, result);
								return result;
							}
						}
					}
				}
			};
		}
	}
	
	private static class LazySingletonIterable<E> implements Iterable<E> {
		private final Supplier<? extends E> supplier;
		private E cache;
		private boolean cacheSet = false;
		
		public LazySingletonIterable(final Supplier<? extends E> supplier) {
			this.supplier = supplier;
		}
		
		@Override
		public void forEach(final Consumer<? super E> consumer) {
			consumer.accept(this.getValue());
		}
		
		public E getValue() {
			if(this.cacheSet) {
				return this.cache;
			}
			this.cacheSet = true;
			return this.cache = this.supplier.get();
		}
		
		@Override
		public Iterator<E> iterator() {
			return new Iterator<E>() {
				private final boolean hasNext = true;
				
				@Override
				public boolean hasNext() {
					return this.hasNext;
				}
				
				@Override
				public E next() {
					if(!this.hasNext) throw new NoSuchElementException();
					return LazySingletonIterable.this.getValue();
				}
			};
		}
		
		@Override
		public Spliterator<E> spliterator() {
			return new Spliterator<E>() {
				private final boolean hasNext = true;
				
				@Override
				public int characteristics() {
					return ORDERED | DISTINCT | SORTED | SIZED | IMMUTABLE;
				}
				
				@Override
				public long estimateSize() {
					return this.hasNext ? 1 : 0;
				}
				
				@Override
				public void forEachRemaining(final Consumer<? super E> consumer) {
					if(this.hasNext) consumer.accept(LazySingletonIterable.this.getValue());
				}
				
				@Override
				public Comparator<E> getComparator() {
					return (a, b) -> 0;
				}
				
				@Override
				public long getExactSizeIfKnown() {
					return this.hasNext ? 1 : 0;
				}
				
				@Override
				public boolean tryAdvance(final Consumer<? super E> consumer) {
					if(!this.hasNext) return false;
					consumer.accept(LazySingletonIterable.this.getValue());
					return true;
				}
				
				@Override
				public Spliterator<E> trySplit() {
					return null;
				}
			};
		}
	}
	
	private static class MappedSpliterator<T, U> implements Spliterator<U> {
		private final Spliterator<T> spliterator;
		private final Function<? super T, ? extends U> mapper;
		
		private MappedSpliterator(final Spliterator<T> spliterator, final Function<? super T, ? extends U> mapper) {
			this.spliterator = spliterator;
			this.mapper = mapper;
		}
		
		@Override
		public int characteristics() {
			return this.spliterator.characteristics() & ~DISTINCT & ~SORTED & ~NONNULL & ~IMMUTABLE;
		}
		
		@Override
		public long estimateSize() {
			return this.spliterator.estimateSize();
		}
		
		@Override
		public void forEachRemaining(final Consumer<? super U> consumer) {
			this.spliterator.forEachRemaining(compose(consumer, this.mapper));
		}
		
		@Override
		public Comparator<? super U> getComparator() {
			throw new IllegalStateException();
		}
		
		@Override
		public long getExactSizeIfKnown() {
			return this.spliterator.getExactSizeIfKnown();
		}
		
		@Override
		public boolean hasCharacteristics(final int i) {
			return this.spliterator.hasCharacteristics(i);
		}
		
		@Override
		public boolean tryAdvance(final Consumer<? super U> consumer) {
			return this.spliterator.tryAdvance(compose(consumer, this.mapper));
		}
		
		@Override
		public Spliterator<U> trySplit() {
			final Spliterator<? extends T> newSpliterator = this.spliterator.trySplit();
			return newSpliterator == null ? null : new MappedSpliterator<>(newSpliterator, this.mapper);
		}
	}
}
