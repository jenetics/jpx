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
import static io.jenetics.jpx.Parsers.toURI;
import static io.jenetics.jpx.Parsers.toYear;
import static io.jenetics.jpx.XMLReader.attr;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Year;
import java.util.Objects;
import java.util.Optional;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Information about the copyright holder and any license governing use of this
 * file. By linking to an appropriate license, you may place your data into the
 * public domain or grant additional usage rights.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.0
 * @since 1.0
 */
public final class Copyright implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String _author;
	private final Year _year;
	private final URI _license;

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @param year year of copyright.
	 * @param license link to external file containing license text.
	 * @throws NullPointerException if the {@code author} is {@code null}
	 */
	private Copyright(final String author, final Year year, final URI license) {
		_author = requireNonNull(author);
		_year = year;
		_license = license;
	}

	/**
	 * Return the copyright holder.
	 *
	 * @return the copyright holder
	 */
	public String getAuthor() {
		return _author;
	}

	/**
	 * Return the year of copyright.
	 *
	 * @return the year of copyright
	 */
	public Optional<Year> getYear() {
		return Optional.ofNullable(_year);
	}

	/**
	 * Return the link to external file containing license text.
	 *
	 * @return link to external file containing license text
	 */
	public Optional<URI> getLicense() {
		return Optional.ofNullable(_license);
	}

	@Override
	public int hashCode() {
		int hash = 31;
		hash += 17*Objects.hashCode(_author) + 37;
		hash += 17*Objects.hashCode(_year) + 37;
		hash += 17*Objects.hashCode(_license) + 37;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Copyright &&
			Objects.equals(((Copyright) obj)._author, _author) &&
			Objects.equals(((Copyright) obj)._year, _year) &&
			Objects.equals(((Copyright) obj)._license, _license);
	}

	@Override
	public String toString() {
		return _author + getYear().map(y -> " (c) " + y).orElse("");
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @param year year of copyright.
	 * @param license link to external file containing license text.
	 * @return a new {@code Copyright} object with the given data
	 * @throws NullPointerException if the {@code author} is {@code null}
	 */
	public static Copyright of(
		final String author,
		final Year year,
		final URI license
	) {
		return new Copyright(author, year, license);
	}

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @param year year of copyright.
	 * @param license link to external file containing license text.
	 * @return a new {@code Copyright} object with the given data
	 * @throws NullPointerException if the {@code author} is {@code null}
	 */
	public static Copyright of(
		final String author,
		final int year,
		final URI license
	) {
		return new Copyright(author, Year.of(year), license);
	}

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @param year year of copyright.
	 * @param license link to external file containing license text.
	 * @return a new {@code Copyright} object with the given data
	 * @throws NullPointerException if the {@code author} is {@code null}
	 * @throws IllegalArgumentException if the given {@code license} is not a
	 *         valid {@code URI} object
	 */
	public static Copyright of(
		final String author,
		final int year,
		final String license
	) {
		final URI uri;
		try {
			uri = license != null ? new URI(license) : null;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}

		return new Copyright(author, Year.of(year), uri);
	}

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @param year year of copyright.
	 * @return a new {@code Copyright} object with the given data
	 * @throws NullPointerException if the {@code author} is {@code null}
	 */
	public static Copyright of(final String author, final Year year) {
		return new Copyright(author, year, null);
	}

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @param year year of copyright.
	 * @return a new {@code Copyright} object with the given data
	 * @throws NullPointerException if the {@code author} is {@code null}
	 */
	public static Copyright of(final String author, final int year) {
		return new Copyright(author, Year.of(year), null);
	}

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @param license link to external file containing license text.
	 * @return a new {@code Copyright} object with the given data
	 * @throws NullPointerException if the {@code author} is {@code null}
	 */
	public static Copyright of(final String author, final URI license) {
		return new Copyright(author, null, license);
	}

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @return a new {@code Copyright} object with the given data
	 * @throws NullPointerException if the {@code author} is {@code null}
	 */
	public static Copyright of(final String author) {
		return new Copyright(author, null, null);
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	static final class Ser implements Externalizable {
		private static final long serialVersionUID = 1L;

		private Copyright _object;

		public Ser() {
		}

		private Ser(final Copyright object) {
			_object = object;
		}

		private Object readResolve() {
			return _object;
		}

		@Override
		public void writeExternal(final ObjectOutput out) throws IOException {
			_object.write(out);
		}

		@Override
		public void readExternal(final ObjectInput in) throws IOException {
			_object = Copyright.read(in);
		}
	}

	private Object writeReplace() {
		return new Ser(this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		IO.writeString(_author, out);
		out.writeBoolean(_year != null);
		if (_year != null) IO.writeInt(_year.getValue(), out);
		IO.writeNullableString(_license != null ? _license.toString() : null, out);
	}

	static Copyright read(final DataInput in) throws IOException {
		final String author = IO.readString(in);
		final Year year = in.readBoolean() ? Year.of(IO.readInt(in)) : null;
		final String license = IO.readNullableString(in);
		try {
			return new Copyright(
				author,
				year,
				license != null ? new URI(license) : null
			);
		} catch (URISyntaxException e) {
			throw (InvalidObjectException)
				new InvalidObjectException(e.getMessage()).initCause(e);
		}
	}

	/* *************************************************************************
	 *  XML stream object serialization
	 * ************************************************************************/

	/**
	 * Writes this {@code Link} object to the given XML stream {@code writer}.
	 *
	 * @param writer the XML data sink
	 * @throws XMLStreamException if an error occurs
	 */
	void write(final XMLStreamWriter writer) throws XMLStreamException {
		final XMLWriter xml = new XMLWriter(writer);

		xml.write("copyright",
			xml.attr("author", _author),
			xml.elem("year", _year),
			xml.elem("license", _license)
		);
	}

	static XMLReader<Copyright> reader() {
		final XML.Function<Object[], Copyright> creator = a -> Copyright.of(
			Parsers.toString(a[0]),
			toYear(a[1], "Copyright.year"),
			toURI(a[2], "Copyright.license")
		);

		return XMLReader.of(creator, "copyright",
			attr("author"),
			XMLReader.of("year"),
			XMLReader.of("license")
		);
	}

}
