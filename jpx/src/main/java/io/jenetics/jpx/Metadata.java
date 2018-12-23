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

import static java.time.ZoneOffset.UTC;
import static io.jenetics.jpx.Lists.copy;
import static io.jenetics.jpx.Lists.immutable;
import static io.jenetics.jpx.ZonedDateTimeFormat.format;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Information about the GPX file, author, and copyright restrictions goes in
 * the metadata section. Providing rich, meaningful information about your GPX
 * files allows others to search for and use your GPS data.
 * <p>
 * Creating a GPX object with one track-segment and 3 track-points:
 * <pre>{@code
 * final Metadata gpx = Metadata.builder()
 *     .author("Franz Wilhelmstötter")
 *     .addLink(Link.of("http://jenetics.io"))
 *     .build();
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.3
 * @since 1.0
 */
public final class Metadata implements Serializable {

	private static final long serialVersionUID = 2L;

	private final String _name;
	private final String _description;
	private final Person _author;
	private final Copyright _copyright;
	private final List<Link> _links;
	private final ZonedDateTime _time;
	private final String _keywords;
	private final Bounds _bounds;

	/**
	 * Create a new {@code Metadata} object with the given parameters.
	 *
	 * @param name the name of the GPX file
	 * @param description a description of the contents of the GPX file
	 * @param author the person or organization who created the GPX file
	 * @param copyright copyright and license information governing use of the
	 *        file
	 * @param links URLs associated with the location described in the file
	 * @param time the creation date of the file
	 * @param keywords keywords associated with the file. Search engines or
	 *        databases can use this information to classify the data.
	 * @param bounds minimum and maximum coordinates which describe the extent
	 *        of the coordinates in the file
	 */
	private Metadata(
		final String name,
		final String description,
		final Person author,
		final Copyright copyright,
		final List<Link> links,
		final ZonedDateTime time,
		final String keywords,
		final Bounds bounds
	) {
		_name = name;
		_description = description;
		_author = author;
		_copyright = copyright;
		_links = immutable(links);
		_time = time;
		_keywords = keywords;
		_bounds = bounds;
	}

	/**
	 * Return the name of the GPX file.
	 *
	 * @return the name of the GPX file
	 */
	public Optional<String> getName() {
		return Optional.ofNullable(_name);
	}

	/**
	 * Return a description of the contents of the GPX file.
	 *
	 * @return a description of the contents of the GPX file
	 */
	public Optional<String> getDescription() {
		return Optional.ofNullable(_description);
	}

	/**
	 * Return the person or organization who created the GPX file.
	 *
	 * @return the person or organization who created the GPX file
	 */
	public Optional<Person> getAuthor() {
		return Optional.ofNullable(_author);
	}

	/**
	 * Return the copyright and license information governing use of the file.
	 *
	 * @return the copyright and license information governing use of the file
	 */
	public Optional<Copyright> getCopyright() {
		return Optional.ofNullable(_copyright);
	}

	/**
	 * Return the URLs associated with the location described in the file. The
	 * returned list immutable.
	 *
	 * @return the URLs associated with the location described in the file
	 */
	public List<Link> getLinks() {
		return _links;
	}

	/**
	 * Return the creation date of the file.
	 *
	 * @return the creation date of the file
	 */
	public Optional<ZonedDateTime> getTime() {
		return Optional.ofNullable(_time);
	}

	/**
	 * Return the keywords associated with the file. Search engines or databases
	 * can use this information to classify the data.
	 *
	 * @return the keywords associated with the file
	 */
	public Optional<String> getKeywords() {
		return Optional.ofNullable(_keywords);
	}

	/**
	 * Return the minimum and maximum coordinates which describe the extent of
	 * the coordinates in the file.
	 *
	 * @return the minimum and maximum coordinates which describe the extent of
	 *         the coordinates in the file
	 */
	public Optional<Bounds> getBounds() {
		return Optional.ofNullable(_bounds);
	}

	/**
	 * Convert the <em>immutable</em> metadata object into a <em>mutable</em>
	 * builder initialized with the current metadata values.
	 *
	 * @since 1.1
	 *
	 * @return a new metadata builder initialized with the values of {@code this}
	 *         metadata
	 */
	public Builder toBuilder() {
		return builder()
			.name(_name)
			.desc(_description)
			.author(_author)
			.copyright(_copyright)
			.links(_links)
			.time(_time)
			.keywords(_keywords)
			.bounds(_bounds);
	}

	/**
	 * Return {@code true} if all metadata properties are {@code null} or empty.
	 *
	 * @return {@code true} if all metadata properties are {@code null} or empty
	 */
	public boolean isEmpty() {
		return _name == null &&
			_description == null &&
			(_author == null || _author.isEmpty()) &&
			_copyright == null &&
			_links.isEmpty() &&
			_time == null &&
			_keywords == null &&
			_bounds == null;
	}

	/**
	 * Return {@code true} if not all metadata properties are {@code null} or empty.
	 *
	 * @since 1.1
	 *
	 * @return {@code true} if not all metadata properties are {@code null} or empty
	 */
	public boolean nonEmpty() {
		return !isEmpty();
	}

	@Override
	public int hashCode() {
		int hash = 37;
		hash += 17*Objects.hashCode(_name) + 31;
		hash += 17*Objects.hashCode(_description) + 31;
		hash += 17*Objects.hashCode(_author) + 31;
		hash += 17*Objects.hashCode(_copyright) + 31;
		hash += 17*Lists.hashCode(_links) + 31;
		hash += 17*Objects.hashCode(_time) + 31;
		hash += 17*Objects.hashCode(_keywords) + 31;
		hash += 17*Objects.hashCode(_bounds) + 31;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Metadata &&
			Objects.equals(((Metadata)obj)._name, _name) &&
			Objects.equals(((Metadata)obj)._description, _description) &&
			Objects.equals(((Metadata)obj)._author, _author) &&
			Objects.equals(((Metadata)obj)._copyright, _copyright) &&
			Lists.equals(((Metadata)obj)._links, _links) &&
			ZonedDateTimes.equals(((Metadata)obj)._time, _time) &&
			Objects.equals(((Metadata)obj)._keywords, _keywords) &&
			Objects.equals(((Metadata)obj)._bounds, _bounds);
	}

	/**
	 * Builder class for creating immutable {@code Metadata} objects.
	 * <p>
	 * Creating a GPX object with one track-segment and 3 track-points:
	 * <pre>{@code
	 * final Metadata gpx = Metadata.builder()
	 *     .author("Franz Wilhelmstötter")
	 *     .addLink(Link.of("http://jenetics.io"))
	 *     .build();
	 * }</pre>
	 */
	public static final class Builder {
		private String _name;
		private String _description;
		private Person _author;
		private Copyright _copyright;
		private final List<Link> _links = new ArrayList<>();
		private ZonedDateTime _time;
		private String _keywords;
		private Bounds _bounds;

		private Builder() {
		}

		/**
		 * Adds the content of a given {@code Metadata} object.
		 *
		 * @param metadata the metadata content
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder metadata(final Metadata metadata) {
			_name = metadata._name;
			_description = metadata._description;
			_author = metadata._author;
			_copyright = metadata._copyright;
			copy(metadata._links, _links);
			_time = metadata._time;
			_keywords = metadata._keywords;
			_bounds = metadata._bounds;

			return this;
		}

		/**
		 * Set the metadata name.
		 *
		 * @param name the metadata name
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder name(final String name) {
			_name = name;
			return this;
		}

		/**
		 * Return the current name.
		 *
		 * @since 1.3
		 *
		 * @return the current name
		 */
		public Optional<String> name() {
			return Optional.ofNullable(_name);
		}

		/**
		 * Set the metadata description.
		 *
		 * @param description the metadata description
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder desc(final String description) {
			_description = description;
			return this;
		}

		/**
		 * Return the current description.
		 *
		 * @since 1.3
		 *
		 * @return the current description
		 */
		public Optional<String> desc() {
			return Optional.ofNullable(_description);
		}

		/**
		 * Set the metadata author.
		 *
		 * @param author the metadata author
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder author(final Person author) {
			_author = author;
			return this;
		}

		/**
		 * Set the metadata author.
		 *
		 * @param author the metadata author
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder author(final String author) {
			return author != null ? author(Person.of(author)) : null;
		}

		/**
		 * Return the current author.
		 *
		 * @since 1.3
		 *
		 * @return the current author
		 */
		public Optional<Person> author() {
			return Optional.ofNullable(_author);
		}

		/**
		 * Set the copyright info.
		 *
		 * @param copyright the copyright info
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder copyright(final Copyright copyright) {
			_copyright = copyright;
			return this;
		}

		/**
		 * Return the current copyright info.
		 *
		 * @since 1.3
		 *
		 * @return the current copyright info
		 */
		public Optional<Copyright> copyright() {
			return Optional.ofNullable(_copyright);
		}

		/**
		 * Set the metadata links.
		 *
		 * @param links the metadata links
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder links(final List<Link> links) {
			copy(links, _links);
			return this;
		}

		/**
		 * Add the given {@code link} to the metadata
		 *
		 * @param link the link to add to the metadata
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder addLink(final Link link) {
			if (link != null) {
				_links.add(link);
			}
			return this;
		}

		/**
		 * Add the given {@code link} to the metadata
		 *
		 * @param href the link to add to the metadata
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given {@code href} is {@code null}
		 * @throws IllegalArgumentException if the given {@code href} is not a
		 *         valid URL
		 */
		public Builder addLink(final String href) {
			return addLink(Link.of(href));
		}

		/**
		 * Return the current links.
		 *
		 * @since 1.3
		 *
		 * @return the current links
		 */
		public List<Link> links() {
			return new NonNullList<>(_links);
		}

		/**
		 * Set the time of the metadata
		 *
		 * @param time the time of the metadata
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder time(final ZonedDateTime time) {
			_time = time;
			return this;
		}

		/**
		 * Set the time of the metadata.
		 *
		 * @param instant the instant to create the metadata time from
		 * @param zone the time-zone
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder time(final Instant instant, final ZoneId zone) {
			_time = instant != null
				? ZonedDateTime.ofInstant(instant, zone != null ? zone : UTC)
				: null;
			return this;
		}

		/**
		 * Set the time of the metadata.
		 *
		 * @param millis the instant to create the metadata time from
		 * @param zone the time-zone
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder time(final long millis, final ZoneId zone) {
			_time = ZonedDateTime.ofInstant(
				Instant.ofEpochMilli(millis),
				zone != null ? zone : UTC
			);
			return this;
		}

		/**
		 * Set the time of the metadata. The zone is set to UTC.
		 *
		 * @param instant the instant to create the metadata time from
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder time(final Instant instant) {
			return time(instant, null);
		}

		/**
		 * Set the time of the metadata.
		 *
		 * @param millis the instant to create the metadata time
		 *        from
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder time(final long millis) {
			return time(Instant.ofEpochMilli(millis));
		}

		/**
		 * Return the currently set time.
		 *
		 * @since 1.3
		 *
		 * @return the currently set time
		 */
		public Optional<ZonedDateTime> time() {
			return Optional.ofNullable(_time);
		}

		/**
		 * Set the metadata keywords.
		 *
		 * @param keywords the metadata keywords
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder keywords(final String keywords) {
			_keywords = keywords;
			return this;
		}

		/**
		 * Return the current keywords.
		 *
		 * @since 1.3
		 *
		 * @return the current keywords
		 */
		public Optional<String> keywords() {
			return Optional.ofNullable(_keywords);
		}

		/**
		 * Set the GPX bounds.
		 *
		 * @param bounds the GPX bounds
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder bounds(final Bounds bounds) {
			_bounds = bounds;
			return this;
		}

		/**
		 * Return the current bounds.
		 *
		 * @since 1.3
		 *
		 * @return the current bounds
		 */
		public Optional<Bounds> bounds() {
			return Optional.ofNullable(_bounds);
		}

		/**
		 * Create an immutable {@code Metadata} object from the current builder
		 * state.
		 *
		 * @return an immutable {@code Metadata} object from the current builder
		 *         state
		 */
		public Metadata build() {
			return new Metadata(
				_name,
				_description,
				_author,
				_copyright,
				_links,
				_time,
				_keywords,
				_bounds
			);
		}
	}

	/**
	 * Return a new {@code Metadata} builder.
	 *
	 * @return a new {@code Metadata} builder
	 */
	public static Builder builder() {
		return new Builder();
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Metadata} object with the given parameters.
	 *
	 * @param name the name of the GPX file
	 * @param description a description of the contents of the GPX file
	 * @param author the person or organization who created the GPX file
	 * @param copyright copyright and license information governing use of the
	 *        file
	 * @param links URLs associated with the location described in the file
	 * @param time the creation date of the file
	 * @param keywords keywords associated with the file. Search engines or
	 *        databases can use this information to classify the data.
	 * @param bounds minimum and maximum coordinates which describe the extent
	 *        of the coordinates in the file
	 * @return a new {@code Metadata} object with the given parameters
	 * @throws NullPointerException if the given {@code links} sequence is
	 *        {@code null}
	 */
	public static Metadata of(
		final String name,
		final String description,
		final Person author,
		final Copyright copyright,
		final List<Link> links,
		final ZonedDateTime time,
		final String keywords,
		final Bounds bounds
	) {
		return new Metadata(
			name,
			description,
			author == null || author.isEmpty() ? null : author,
			copyright,
			links,
			time,
			keywords,
			bounds
		);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.METADATA, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		IO.writeNullableString(_name, out);
		IO.writeNullableString(_description, out);
		IO.writeNullable(_author, Person::write, out);
		IO.writeNullable(_copyright, Copyright::write, out);
		IO.writes(_links, Link::write, out);
		IO.writeNullable(_time, ZonedDateTimes::write, out);
		IO.writeNullableString(_keywords, out);
		IO.writeNullable(_bounds, Bounds::write, out);
	}

	static Metadata read(final DataInput in) throws IOException {
		return new Metadata(
			IO.readNullableString(in),
			IO.readNullableString(in),
			IO.readNullable(Person::read, in),
			IO.readNullable(Copyright::read, in),
			IO.reads(Link::read, in),
			IO.readNullable(ZonedDateTimes::read, in),
			IO.readNullableString(in),
			IO.readNullable(Bounds::read, in)
		);
	}


	/* *************************************************************************
	 *  XML stream object serialization
	 * ************************************************************************/

	static final XMLWriter<Metadata> WRITER = XMLWriter.elem("metadata",
		XMLWriter.elem("name").map(md -> md._name),
		XMLWriter.elem("desc").map(md -> md._description),
		Person.writer("author").map(md -> md._author),
		Copyright.WRITER.map(md -> md._copyright),
		XMLWriter.elems(Link.WRITER).map(md -> md._links),
		XMLWriter.elem("time").map(md -> format(md._time)),
		XMLWriter.elem("keywords").map(md -> md._keywords),
		Bounds.WRITER.map(md -> md._bounds)
	);

	@SuppressWarnings("unchecked")
	static final XMLReader<Metadata> READER = XMLReader.elem(
		v -> Metadata.of(
			(String)v[0],
			(String)v[1],
			(Person)v[2],
			(Copyright)v[3],
			(List<Link>)v[4],
			(ZonedDateTime)v[5],
			(String)v[6],
			(Bounds)v[7]
		),
		"metadata",
		XMLReader.elem("name"),
		XMLReader.elem("desc"),
		Person.reader("author"),
		Copyright.READER,
		XMLReader.elems(Link.READER),
		XMLReader.elem("time").map(ZonedDateTimeFormat::parse),
		XMLReader.elem("keywords"),
		Bounds.READER,
		XMLReader.ignore("extensions")
	);

}
