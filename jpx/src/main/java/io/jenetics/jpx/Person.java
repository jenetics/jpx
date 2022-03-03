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

import static java.util.Objects.hash;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * A person or organization.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.2
 * @since 1.0
 */
public final class Person implements Serializable {

	@Serial
	private static final long serialVersionUID = 2L;

	private final String _name;
	private final Email _email;
	private final Link _link;

	/**
	 * Create a new {@code Person} object with the given parameters.
	 *
	 * @param name name of person or organization
	 * @param email the person's email address
	 * @param link link to Web site or other external information about person
	 */
	private Person(final String name, final Email email, final Link link) {
		_name = name;
		_email = email;
		_link = link;
	}

	/**
	 * Return the name of the person or organization.
	 *
	 * @return the name of the person or organization
	 */
	public Optional<String> getName() {
		return Optional.ofNullable(_name);
	}

	/**
	 * Return the email address.
	 *
	 * @return the email address
	 */
	public Optional<Email> getEmail() {
		return Optional.ofNullable(_email);
	}

	/**
	 * Return the link to Web site or other external information about person.
	 *
	 * @return the link to Web site or other external information about person
	 */
	public Optional<Link> getLink() {
		return Optional.ofNullable(_link);
	}

	/**
	 * Return {@code true} if all person properties are {@code null}.
	 *
	 * @return {@code true} if all person properties are {@code null}
	 */
	public boolean isEmpty() {
		return _name == null &&
			_email == null &&
			_link == null;
	}

	/**
	 * Return {@code true} if not all person properties are {@code null}.
	 *
	 * @since 1.1
	 *
	 * @return {@code true} if not all person properties are {@code null}
	 */
	public boolean nonEmpty() {
		return !isEmpty();
	}

	@Override
	public int hashCode() {
		return hash(_name, _email, _link);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Person person &&
			Objects.equals(person._name, _name) &&
			Objects.equals(person._email, _email) &&
			Objects.equals(person._link, _link);
	}

	@Override
	public String toString() {
		return Objects.toString(_name);
	}

	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Person} object with the given parameters.
	 *
	 * @param name name of person or organization
	 * @param email the person's email address
	 * @param link link to Web site or other external information about person
	 * @return a new {@code Person} object with the given parameters
	 */
	public static Person of(final String name, final Email email, final Link link) {
		return new Person(name, email, link);
	}

	/**
	 * Create a new {@code Person} object with the given parameters.
	 *
	 * @param name name of person or organization
	 * @param email the person's email address
	 * @return a new {@code Person} object with the given parameters
	 */
	public static Person of(final String name, final Email email) {
		return new Person(name, email, null);
	}

	/**
	 * Create a new {@code Person} object with the given parameters.
	 *
	 * @param name name of person or organization
	 * @return a new {@code Person} object with the given parameters
	 */
	public static Person of(final String name) {
		return new Person(name, null, null);
	}

	/**
	 * Create a new <i>empty</i> {@code Person}.
	 *
	 * @return a new <i>empty</i> {@code Person}
	 */
	public static Person of() {
		return new Person(null, null, null);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.PERSON, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		IO.writeNullableString(_name, out);
		IO.writeNullable(_email, Email::write, out);
		IO.writeNullable(_link, Link::write, out);
	}

	static Person read(final DataInput in) throws IOException {
		return new Person(
			IO.readNullableString(in),
			IO.readNullable(Email::read, in),
			IO.readNullable(Link::read, in)
		);
	}


	/* *************************************************************************
	 *  XML stream object serialization
	 * ************************************************************************/

	static XMLWriter<Person> writer(final String name) {
		return XMLWriter.elem(name,
			XMLWriter.elem("name").map(person -> person._name),
			Email.WRITER.map(person -> person._email),
			Link.WRITER.map(person -> person._link)
		);
	}

	static XMLReader<Person> reader(final String name) {
		return XMLReader.elem(
			v -> Person.of((String)v[0], (Email)v[1], (Link)v[2]),
			name,
			XMLReader.elem("name"),
			Email.READER,
			Link.READER
		);
	}

}
