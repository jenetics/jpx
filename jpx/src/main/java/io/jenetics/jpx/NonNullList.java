/*
 * Java GPX Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.jpx;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.1
 * @since 1.1
 */
final class NonNullList<E> implements List<E>, Serializable {
	private static final long serialVersionUID = 1L;

	private final List<E> _adoptee;

	NonNullList(final List<E> adoptee) {
		_adoptee = requireNonNull(adoptee);
	}

	@Override
	public int size() {
		return _adoptee.size();
	}

	@Override
	public boolean isEmpty() {
		return _adoptee.isEmpty();
	}

	@Override
	public boolean contains(final Object o) {
		return _adoptee.contains(o);
	}

	@Override
	public Object[] toArray() {
		return _adoptee.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return _adoptee.toArray(a);
	}

	@Override
	public String toString() {
		return _adoptee.toString();
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private final Iterator<E> _it = _adoptee.iterator();

			@Override
			public boolean hasNext() {
				return _it.hasNext();
			}

			@Override
			public E next() {
				return _it.next();
			}

			@Override
			public void remove() {
				_it.remove();
			}

			@Override
			public void forEachRemaining(final Consumer<? super E> action) {
				_it.forEachRemaining(action);
			}
		};
	}

	@Override
	public boolean add(final E e) {
		return _adoptee.add(requireNonNull(e));
	}

	@Override
	public boolean remove(final Object o) {
		return _adoptee.remove(requireNonNull(o));
	}

	@Override
	public boolean containsAll(final Collection<?> coll) {
		return _adoptee.containsAll(coll);
	}

	@Override
	public boolean addAll(final Collection<? extends E> coll) {
		coll.forEach(Objects::requireNonNull);
		return _adoptee.addAll(coll);
	}

	@Override
	public boolean removeAll(final Collection<?> coll) {
		return _adoptee.removeAll(coll);
	}

	@Override
	public boolean retainAll(final Collection<?> coll) {
		coll.forEach(Objects::requireNonNull);
		return _adoptee.retainAll(coll);
	}

	@Override
	public void clear() {
		_adoptee.clear();
	}

	@Override
	public void forEach(final Consumer<? super E> action) {
		_adoptee.forEach(action);
	}

	@Override
	public boolean removeIf(final Predicate<? super E> filter) {
		return _adoptee.removeIf(filter);
	}

	@Override
	public Spliterator<E> spliterator() {
		return _adoptee.spliterator();
	}

	@Override
	public Stream<E> stream() {
		return _adoptee.stream();
	}

	@Override
	public Stream<E> parallelStream() {
		return _adoptee.parallelStream();
	}

	@Override
	public E get(final int index) {
		return _adoptee.get(index);
	}

	@Override
	public E set(final int index, final E element) {
		return _adoptee.set(index, requireNonNull(element));
	}

	@Override
	public void add(final int index, final E element) {
		_adoptee.add(index, requireNonNull(element));
	}

	@Override
	public E remove(int index) {
		return _adoptee.remove(index);
	}

	@Override
	public int indexOf(final Object o) {
		return _adoptee.indexOf(requireNonNull(o));
	}

	@Override
	public int lastIndexOf(final Object o) {
		return _adoptee.lastIndexOf(requireNonNull(o));
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends E> coll) {
		coll.forEach(Objects::requireNonNull);
		return _adoptee.addAll(coll);
	}

	@Override
	public void replaceAll(final UnaryOperator<E> operator) {
		_adoptee.replaceAll(operator);
	}

	@Override
	public void sort(final Comparator<? super E> comparator) {
		_adoptee.sort(comparator);
	}

	@Override
	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<E> listIterator(final int index) {
		return new ListIterator<E>() {
			private final ListIterator<E> _it =
				_adoptee.listIterator(index);

			@Override
			public boolean hasNext() {
				return _it.hasNext();
			}

			@Override
			public E next() {
				return _it.next();
			}

			@Override
			public boolean hasPrevious() {
				return _it.hasPrevious();
			}

			@Override
			public E previous() {
				return _it.previous();
			}

			@Override
			public int nextIndex() {
				return _it.nextIndex();
			}

			@Override
			public int previousIndex() {
				return _it.previousIndex();
			}

			@Override
			public void remove() {
				_it.remove();
			}

			@Override
			public void set(final E e) {
				_it.set(requireNonNull(e));
			}

			@Override
			public void add(final E e) {
				_it.add(requireNonNull(e));
			}

			@Override
			public void forEachRemaining(final Consumer<? super E> action) {
				_it.forEachRemaining(action);
			}

		};
	}

	@Override
	public List<E> subList(final int from, final int to) {
		return new NonNullList<>(_adoptee.subList(from, to));
	}

	@Override
	public int hashCode() {
		return _adoptee.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		return o == this || _adoptee.equals(o);
	}

}
