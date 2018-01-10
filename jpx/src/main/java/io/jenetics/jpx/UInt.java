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

import static java.lang.String.format;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Represents an unsigned integer value.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Value_object">Value object</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.0
 * @since 1.0
 */
public final class UInt
	extends Number
	implements
		Comparable<UInt>,
		Serializable
{

	private static final long serialVersionUID = 1L;

	private final int _value;

	/**
	 * Create a new unsigned integer object with the given value.
	 *
	 * @param value the {@code UInt} value
	 * @throws IllegalArgumentException if the given {@code value} is smaller
	 *         than zero
	 */
	private UInt(final int value) {
		if (value < 0) {
			throw new IllegalArgumentException(format("%d is negative.", value));
		}
		_value = value;
	}

	/**
	 * Return the unsigned integer value.
	 *
	 * @return the unsigned integer value
	 */
	public int getValue() {
		return _value;
	}

	@Override
	public int intValue() {
		return _value;
	}

	@Override
	public long longValue() {
		return _value;
	}

	@Override
	public float floatValue() {
		return _value;
	}

	@Override
	public double doubleValue() {
		return _value;
	}

	@Override
	public int compareTo(final UInt other) {
		return Integer.compare(_value, other._value);
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(_value);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof UInt &&
			((UInt)obj)._value == _value;
	}

	@Override
	public String toString() {
		return Integer.toString(_value);
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new unsigned integer object with the given value.
	 *
	 * @param value the {@code UInt} value
	 * @return a new unsigned integer object with the given value
	 * @throws IllegalArgumentException if the given {@code value} is smaller
	 *         than zero
	 */
	public static UInt of(final int value) {
		return new UInt(value);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private static final class SerializationProxy implements Serializable {
		private static final long serialVersionUID = 1L;

		private final int value;

		private SerializationProxy(final UInt uint) {
			value = uint.intValue();
		}

		private Object readResolve() {
			return new UInt(value);
		}
	}

	private Object writeReplace() {
		return new SerializationProxy(this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Proxy required.");
	}
}
