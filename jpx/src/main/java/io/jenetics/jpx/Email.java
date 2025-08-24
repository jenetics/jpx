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
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * An email address. Broken into two parts (id and domain) to help prevent email
 * harvesting.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.2
 * @since 1.0
 */
public final class Email implements Comparable<Email>, Serializable {

	@Serial
	private static final long serialVersionUID = 2L;

	private final String _id;
	private final String _domain;

	/**
	 * Create a new {@code Email} object with the given {@code id} and
	 * {@code domain}.
	 *
	 * @param id id half of email address (billgates2004)
	 * @param domain domain half of email address (hotmail.com)
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	private Email(final String id, final String domain) {
		_id = requireNonNull(id);
		_domain = requireNonNull(domain);
	}

	/**
	 * Return the id half of the email address.
	 *
	 * @return the id half of the email address
	 */
	public String getID() {
		return _id;
	}

	/**
	 * Return the domain half of the email address.
	 *
	 * @return the domain half of the email address
	 */
	public String getDomain() {
		return _domain;
	}

	/**
	 * Return the full EMail address: id + "@" + domain.
	 *
	 * @return the full EMail address: id + "@" + domain
	 */
	public String getAddress() {
		return _id + "@" + _domain;
	}

	@Override
	public int compareTo(final Email other) {
		int cmp = _domain.compareTo(other._domain);
		if (cmp == 0) {
			cmp = _id.compareTo(other._id);
		}

		return cmp;
	}

	@Override
	public int hashCode() {
		return hash(_id, _domain);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Email email &&
			Objects.equals(email._id, _id) &&
			Objects.equals(email._domain, _domain);
	}

	@Override
	public String toString() {
		return getAddress();
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Email} object with the given {@code id} and
	 * {@code domain}.
	 *
	 * @param id id half of email address (billgates2004)
	 * @param domain domain half of email address (hotmail.com)
	 * @return a new {@code Email} object with the given values
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static Email of(final String id, final String domain) {
		return new Email(id, domain);
	}

	/**
	 * Create a new {@code Email} from the given {@code address} string.
	 *
	 * @param address the email address string
	 * @return a new {@code Email} object with {@code address}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the given {@code address} is invalid
	 */
	public static Email of(final String address) {
		if (address.length() < 3) {
			throw new IllegalArgumentException(format(
				"Invalid email: '%s'.", address
			));
		}

		final int index = address.indexOf('@');
		if (index == -1 || index == 0 || index == address.length() - 1) {
			throw new IllegalArgumentException(format(
				"Invalid email: '%s'.", address
			));
		}

		return new Email(
			address.substring(0, index),
			address.substring(index + 1)
		);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.EMAIL, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		IO.writeString(getAddress(), out);
	}

	static Email read(final DataInput in) throws IOException {
		return Email.of(IO.readString(in));
	}

	/* *************************************************************************
	 *  XML stream object serialization
	 * ************************************************************************/

	static final XMLWriter<Email> WRITER = XMLWriter.elem("email",
		XMLWriter.attr("id").map(email -> email._id),
		XMLWriter.attr("domain").map(email -> email._domain)
	);

	static final XMLReader<Email> READER = XMLReader.elem(
		v -> Email.of((String)v[0], (String)v[1]),
		"email",
		XMLReader.attr("id"),
		XMLReader.attr("domain")
	);

}
