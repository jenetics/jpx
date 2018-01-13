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
import static java.util.Objects.requireNonNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a link to an external resource (Web page, digital photo, video
 * clip, etc) with additional information.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 1.0
 */
public final class Link implements Serializable {

	private static final long serialVersionUID = 1L;

	private final URI _href;
	private final String _text;
	private final String _type;

	/**
	 * Create a new {@code Link} object with the given parameters.
	 *
	 * @param href the hyperlink (mandatory)
	 * @param text the text of the hyperlink (optional)
	 * @param type the mime type of the content, e.g. {@code image/jpeg}
	 *        (optional)
	 * @throws NullPointerException if the given {@code href} is {@code null}
	 */
	private Link(final URI href, final String text, final String type) {
		_href = requireNonNull(href);
		_text = text;
		_type = type;
	}

	/**
	 * Return the hyperlink.
	 *
	 * @return the hyperlink
	 */
	public URI getHref() {
		return _href;
	}

	/**
	 * Return the hyperlink text.
	 *
	 * @return the hyperlink text
	 */
	public Optional<String> getText() {
		return Optional.ofNullable(_text);
	}

	/**
	 * Return the mime type of the hyperlink
	 *
	 * @return the mime type
	 */
	public Optional<String> getType() {
		return Optional.ofNullable(_type);
	}

	@Override
	public int hashCode() {
		int hash = 37;
		hash += 17*Objects.hashCode(_href) + 31;
		hash += 17*Objects.hashCode(_text) + 31;
		hash += 17*Objects.hashCode(_type) + 31;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Link &&
			Objects.equals(((Link)obj)._href, _href) &&
			Objects.equals(((Link)obj)._text, _text) &&
			Objects.equals(((Link)obj)._type, _type);
	}

	@Override
	public String toString() {
		return format("Link[%s, text=%s, type=%s]", _href, _text, _type);
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Link} object with the given parameters.
	 *
	 * @param href the hyperlink (mandatory)
	 * @param text the text of the hyperlink (optional)
	 * @param type the mime type of the content, e.g. {@code image/jpeg}
	 *        (optional)
	 * @return a new {@code Link} object with the given parameters
	 * @throws NullPointerException if the given {@code href} is {@code null}
	 */
	public static Link of(final URI href, final String text, final String type) {
		return new Link(href, text, type);
	}

	/**
	 * Create a new {@code Link} object with the given parameters.
	 *
	 * @param href the hyperlink (mandatory)
	 * @param text the text of the hyperlink (optional)
	 * @param type the mime type of the content, e.g. {@code image/jpeg}
	 *        (optional)
	 * @return a new {@code Link} object with the given parameters
	 * @throws NullPointerException if the given {@code href} is {@code null}
	 * @throws IllegalArgumentException if the given {@code href} is not a valid
	 *         URL
	 */
	public static Link of(final String href, final String text, final String type) {
		try {
			return new Link(new URI(requireNonNull(href)), text, type);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Create a new {@code Link} object with the given {@code href}.
	 *
	 * @param href the hyperlink (mandatory)
	 * @return a new {@code Link} object with the given {@code href}
	 * @throws NullPointerException if the given {@code href} is {@code null}
	 */
	public static Link of(final URI href) {
		return new Link(href, null, null);
	}

	/**
	 * Create a new {@code Link} object with the given {@code href}.
	 *
	 * @param href the hyperlink (mandatory)
	 * @return a new {@code Link} object with the given {@code href}
	 * @throws NullPointerException if the given {@code href} is {@code null}
	 * @throws IllegalArgumentException if the given {@code href} is not a valid
	 *         URL
	 */
	public static Link of(final String href) {
		try {
			return new Link(new URI(requireNonNull(href)), null, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.LINK, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		IO.writeString(_href.toString(), out);
		IO.writeNullableString(_text, out);
		IO.writeNullableString(_type, out);
	}

	static Link read(final DataInput in) throws IOException {
		try {
			return new Link(
				new URI(IO.readString(in)),
				IO.readNullableString(in),
				IO.readNullableString(in)
			);
		} catch (URISyntaxException e) {
			throw (InvalidObjectException)
				new InvalidObjectException(e.getMessage()).initCause(e);
		}
	}

	/* *************************************************************************
	 *  XML stream object serialization
	 * ************************************************************************/

	static final XMLWriter<Link> WRITER = XMLWriter.elem("link",
		XMLWriter.attr("href").map(link -> link._href),
		XMLWriter.elem("text").map(link -> link._text),
		XMLWriter.elem("type").map(link -> link._type)
	);

	static final XMLReader<Link> READER = XMLReader.elem(
		v -> Link.of(
			(URI)v[0],
			(String)v[1],
			(String)v[2]
		),
		"link",
		XMLReader.attr("href").map(Format::parseURI),
		XMLReader.elem("text"),
		XMLReader.elem("type")
	);

}
