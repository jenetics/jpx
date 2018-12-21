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
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static javax.xml.stream.XMLStreamConstants.CDATA;
import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.COMMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Document;

import io.jenetics.jpx.XMLReader.Type;

/**
 * Simplifies the usage of the {@link XMLStreamReader}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 1.0
 */
abstract class XMLReader<T> {

	/**
	 * Represents the XML element type.
	 */
	static enum Type {

		/**
		 * Denotes a element reader.
		 */
		ELEM,

		/**
		 * Denotes a element attribute reader.
		 */
		ATTR,

		/**
		 * Denotes a reader of elements of the same type.
		 */
		LIST,

		/**
		 * Denotes a reader of the text of a element.
		 */
		TEXT

	}

	private final String _name;
	private final Type _type;

	/**
	 * Create a new XML reader with the given name and type.
	 *
	 * @param name the element name of the reader
	 * @param type the element type of the reader
	 * @throws NullPointerException if one of the give arguments is {@code null}
	 */
	XMLReader(final String name, final Type type) {
		_name = requireNonNull(name);
		_type = requireNonNull(type);
	}

	/**
	 * Read the given type from the underlying XML stream {@code reader}.
	 *
	 * <pre>{@code
	 * try (AutoCloseableXMLStreamReader xml = XML.reader(in)) {
	 *     // Move XML stream to first element.
	 *     xml.next();
	 *     return reader.read(xml);
	 * }
	 * }</pre>
	 *
	 * @param xml the underlying XML stream {@code reader}
	 * @param lenient lenient read mode
	 * @return the data read from the XML stream, maybe {@code null}
	 * @throws XMLStreamException if an error occurs while reading the value
	 * @throws NullPointerException if the given {@code xml} stream reader is
	 *         {@code null}
	 */
	public abstract T read(final XMLStreamReader xml, final boolean lenient)
		throws XMLStreamException;

	/**
	 * Create a new reader for the new mapped type {@code B}.
	 *
	 * @param mapper the mapper function
	 * @param <B> the target type of the new reader
	 * @return a new reader
	 * @throws NullPointerException if the given {@code mapper} function is
	 *         {@code null}
	 */
	public <B> XMLReader<B> map(final Function<? super T, ? extends B> mapper) {
		requireNonNull(mapper);

		return new XMLReader<B>(_name, _type) {
			@Override
			public B read(final XMLStreamReader xml, final boolean lenient)
				throws XMLStreamException
			{
				try {
					return mapper.apply(XMLReader.this.read(xml, lenient));
				} catch (RuntimeException e) {
					if (!lenient) {
						throw new XMLStreamException(format(
							"Invalid value for '%s'.", _name), e
						);
					} else {
						return null;
					}
				}
			}
		};
	}

	/**
	 * Return the name of the element processed by this reader.
	 *
	 * @return the element name the reader is processing
	 */
	String name() {
		return _name;
	}

	/**
	 * Return the element type of the reader.
	 *
	 * @return the element type of the reader
	 */
	Type type() {
		return _type;
	}

	@Override
	public String toString() {
		return format("Reader[%s, %s]", name(), type());
	}


	/* *************************************************************************
	 * Static reader factory methods.
	 * ************************************************************************/

	/**
	 * Return a {@code Reader} for reading an attribute of an element.
	 * <p>
	 * <b>XML</b>
	 * <pre> {@code <element length="3"/>}</pre>
	 *
	 * <b>Reader definition</b>
	 * <pre>{@code
	 * final Reader<Integer> reader =
	 *     elem(
	 *         v -> (Integer)v[0],
	 *         "element",
	 *         attr("length").map(Integer::parseInt)
	 *     );
	 * }</pre>
	 *
	 * @param name the attribute name
	 * @return an attribute reader
	 * @throws NullPointerException if the given {@code name} is {@code null}
	 */
	public static XMLReader<String> attr(final String name) {
		return new AttrReader(name);
	}

	/**
	 * Return a {@code Reader} for reading the text of an element.
	 * <p>
	 * <b>XML</b>
	 * <pre> {@code <element>1234<element>}</pre>
	 *
	 * <b>Reader definition</b>
	 * <pre>{@code
	 * final Reader<Integer> reader =
	 *     elem(
	 *         v -> (Integer)v[0],
	 *         "element",
	 *         text().map(Integer::parseInt)
	 *     );
	 * }</pre>
	 *
	 * @return an element text reader
	 */
	public static XMLReader<String> text() {
		return new TextReader();
	}

	/**
	 * Return a {@code Reader} for reading an object of type {@code T} from the
	 * XML element with the given {@code name}.
	 *
	 * <p>
	 * <b>XML</b>
	 * <pre> {@code <property name="size">1234<property>}</pre>
	 *
	 * <b>Reader definition</b>
	 * <pre>{@code
	 * final XMLReader<Property> reader =
	 *     elem(
	 *         v -> {
	 *             final String name = (String)v[0];
	 *             final Integer value = (Integer)v[1];
	 *             return Property.of(name, value);
	 *         },
	 *         "property",
	 *         attr("name"),
	 *         text().map(Integer::parseInt)
	 *     );
	 * }</pre>
	 *
	 * @param generator the generator function, which build the result object
	 *        from the given parameter array
	 * @param name the name of the root (sub-tree) element
	 * @param children the child element reader, which creates the values
	 *        forwarded to the {@code generator} function
	 * @param <T> the reader result type
	 * @return a node reader
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 * @throws IllegalArgumentException if the given child readers contains more
	 *         than one <em>text</em> reader
	 */
	public static <T> XMLReader<T> elem(
		final Function<Object[], T> generator,
		final String name,
		final XMLReader<?>... children
	) {
		requireNonNull(name);
		requireNonNull(generator);
		Stream.of(requireNonNull(children)).forEach(Objects::requireNonNull);

		return new ElemReader<>(name, generator, asList(children), Type.ELEM);
	}

	/**
	 * Return a {@code Reader} which reads the value from the child elements of
	 * the given parent element {@code name}.
	 * <p>
	 * <b>XML</b>
	 * <pre> {@code <min><property name="size">1234<property></min>}</pre>
	 *
	 * <b>Reader definition</b>
	 * <pre>{@code
	 * final XMLReader<Property> reader =
	 *     elem("min",
	 *         elem(
	 *             v -> {
	 *                 final String name = (String)v[0];
	 *                 final Integer value = (Integer)v[1];
	 *                 return Property.of(name, value);
	 *             },
	 *             "property",
	 *             attr("name"),
	 *             text().map(Integer::parseInt)
	 *         )
	 *     );
	 * }</pre>
	 *
	 * @param name the parent element name
	 * @param reader the child elements reader
	 * @param <T> the result type
	 * @return a node reader
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static <T> XMLReader<T> elem(
		final String name,
		final XMLReader<? extends T> reader
	) {
		requireNonNull(name);
		requireNonNull(reader);

		return elem(
			v -> {
				@SuppressWarnings("unchecked")
				T value = v.length > 0 ? (T)v[0] : null;
				return value;
			},
			name,
			reader
		);
	}

	public static XMLReader<String> elem(final String name) {
		return elem(name, text());
	}

	public static XMLReader<Object> ignore(final String name) {
		return new IgnoreReader(name);
	}

	/**
	 * Return a {@code XMLReader} which collects the elements, read by the given
	 * child {@code reader}, and returns it as list of these elements.
	 * <p>
	 * <b>XML</b>
	 * <pre> {@code
	 * <properties length="3">
	 *     <property>-1878762439</property>
	 *     <property>-957346595</property>
	 *     <property>-88668137</property>
	 * </properties>
	 * }</pre>
	 *
	 * <b>Reader definition</b>
	 * <pre>{@code
	 * XMLReader<List<Integer>> reader =
	 *     elem(
	 *         v -> (List<Integer>)v[0],
	 *         "properties",
	 *         elems(elem("property", text().map(Integer::parseInt)))
	 *     );
	 * }</pre>
	 *
	 * @param reader the child element reader
	 * @param <T> the element type
	 * @return a list reader
	 */
	public static <T> XMLReader<List<T>> elems(final XMLReader<? extends T> reader) {
		return new ListReader<T>(reader);
	}

	public static XMLReader<Document> doc(final String name) {
		return new DocReader(name);
	}
}


/* *****************************************************************************
 * XML reader implementations.
 * ****************************************************************************/

/**
 * Reader implementation for reading the attribute of the current node.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.2
 * @since 1.2
 */
final class AttrReader extends XMLReader<String> {

	AttrReader(final String name) {
		super(name, Type.ATTR);
	}

	@Override
	public String read(final XMLStreamReader xml, final boolean lenient)
		throws XMLStreamException
	{
		xml.require(START_ELEMENT, null, null);
		return xml.getAttributeValue(null, name());
	}

}

/**
 * Reader implementation for reading the text of the current node.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.2
 * @since 1.2
 */
final class TextReader extends XMLReader<String> {

	TextReader() {
		super("", Type.TEXT);
	}

	@Override
	public String read(final XMLStreamReader xml, final boolean lenient)
		throws XMLStreamException
	{
		final StringBuilder out = new StringBuilder();

		int type = xml.getEventType();
		do {
			out.append(xml.getText());
		} while (xml.hasNext() && (type = xml.next()) == CHARACTERS || type == CDATA);


		return out.toString();
	}
}

/**
 * Reader implementation for reading list of elements.
 *
 * @param <T> the element type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.2
 * @since 1.2
 */
final class ListReader<T> extends XMLReader<List<T>> {

	private final XMLReader<? extends T> _adoptee;

	ListReader(final XMLReader<? extends T> adoptee) {
		super(adoptee.name(), Type.LIST);
		_adoptee = adoptee;
	}

	@Override
	public List<T> read(final XMLStreamReader xml, final boolean lenient)
		throws XMLStreamException
	{
		xml.require(START_ELEMENT, null, name());
		final T element = _adoptee.read(xml, lenient);
		return element != null
			? Collections.singletonList(element)
			: emptyList();
	}

	XMLReader<? extends T> reader() {
		return _adoptee;
	}

}

/**
 * This reader implementation ignores the content of the element with the given
 * name.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.2
 * @since 1.2
 */
final class IgnoreReader extends XMLReader<Object> {

	private final XMLReader<Object> _reader;

	IgnoreReader(final String name) {
		super(name, Type.ELEM);
		_reader = new ElemReader<>(name, o -> o, emptyList(), Type.ELEM);
	}

	@Override
	public Object read(final XMLStreamReader xml, final boolean lenient)
		throws XMLStreamException
	{
		return _reader.read(xml, true);
	}
}

/**
 * This reader implementation reads the XML nodes from a given base node.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class DocReader extends XMLReader<Document> {

	DocReader(final String name) {
		super(name, Type.ELEM);
	}

	@Override
	public Document read(final XMLStreamReader xml, final boolean lenient)
		throws XMLStreamException
	{
		Document doc = null;
		try {
			doc = XML.builder().newDocument();
			XML.copy(xml, doc);
			XML.clean(doc);
		} catch (XMLStreamException e) {
			if (!lenient) {
				throw e;
			}
		}

		return doc;
	}

}

/**
 * The main XML element reader implementation.
 *
 * @param <T> the reader data type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.2
 * @since 1.2
 */
final class ElemReader<T> extends XMLReader<T> {

	// Given parameters.
	private final Function<Object[], T> _creator;
	private final List<XMLReader<?>> _children;

	// Derived parameters.
	private final Map<String, Integer> _readerIndexMapping = new HashMap<>();
	private final int[] _attrReaderIndexes;
	private final int[] _textReaderIndex;

	ElemReader(
		final String name,
		final Function<Object[], T> creator,
		final List<XMLReader<?>> children,
		final Type type
	) {
		super(name, type);

		_creator = requireNonNull(creator);
		_children = requireNonNull(children);

		for (int i = 0; i < _children.size(); ++i) {
			_readerIndexMapping.put(_children.get(i).name(), i);
		}
		_attrReaderIndexes = IntStream.range(0, _children.size())
			.filter(i -> _children.get(i).type() == Type.ATTR)
			.toArray();
		_textReaderIndex = IntStream.range(0, _children.size())
			.filter(i -> _children.get(i).type() == Type.TEXT)
			.toArray();

		if (_textReaderIndex.length > 1) {
			throw new IllegalArgumentException(
				"Found more than one TEXT reader."
			);
		}
	}

	@Override
	public T read(final XMLStreamReader xml, final boolean lenient)
		throws XMLStreamException
	{
		xml.require(START_ELEMENT, null, name());

		final List<ReaderResult> results = _children.stream()
			.map(ReaderResult::of)
			.collect(Collectors.toList());

		final ReaderResult text = _textReaderIndex.length == 1
			? results.get(_textReaderIndex[0])
			: null;

		for (int i = 0; i < _attrReaderIndexes.length; ++i) {
			final ReaderResult result = results.get(_attrReaderIndexes[i]);
			try {
				result.put(result.reader().read(xml, lenient));
			} catch (IllegalArgumentException|NullPointerException e) {
				if (!lenient) throw e;
			}
		}

		if (xml.hasNext()) {
			xml.next();

			boolean hasNext = false;
			do {
				switch (xml.getEventType()) {
					case COMMENT:
						if (xml.hasNext()) {
							xml.next();
						}
						break;
					case START_ELEMENT:
						Integer index = _readerIndexMapping
							.get(xml.getLocalName());

						if (index == null && !lenient) {
							throw new XMLStreamException(format(
								"Unexpected element <%s>.",
								xml.getLocalName()
							));
						}

						final ReaderResult result = index != null
							? results.get(index)
							: ReaderResult.of(elem(xml.getLocalName()));

						if (result != null) {
							throwUnexpectedElement(xml, lenient, result);
							if (xml.hasNext()) {
								hasNext = true;
								xml.next();
							} else {
								hasNext = false;
							}
						}

						break;
					case CHARACTERS:
					case CDATA:
						if (text != null) {
							throwUnexpectedElement(xml, lenient, text);
						} else {
							xml.next();
						}
						hasNext = true;

						break;
					case END_ELEMENT:
						if (name().equals(xml.getLocalName())) {
							try {
								return _creator.apply(
									results.stream()
										.map(ReaderResult::value)
										.toArray()
								);
							} catch (IllegalArgumentException|NullPointerException e) {
								if (!lenient) {
									throw new XMLStreamException(format(
										"Invalid value for '%s'.", name()), e
									);
								} else {
									return null;
								}
							}
						}
				}

			} while (hasNext);
		}

		throw new XMLStreamException(format(
			"Premature end of file while reading '%s'.", name()
		));
	}

	private void throwUnexpectedElement(
		final XMLStreamReader xml,
		final boolean lenient,
		final ReaderResult text
	)
		throws XMLStreamException
	{
		try {
			text.put(text.reader().read(xml, lenient));
		} catch (IllegalArgumentException|NullPointerException e) {
			if (!lenient) {
				final XMLStreamException exp = new XMLStreamException(format(
					"Unexpected element <%s>.",
					xml.getLocalName()
				));
				exp.addSuppressed(e);
				throw exp;
			}
		}
	}
}

/**
 * Helper interface for storing the XML reader (intermediate) results.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.2
 * @since 1.2
 */
interface ReaderResult {

	/**
	 * Return the underlying XML reader, which reads the result.
	 *
	 * @return return the underlying XML reader
	 */
	XMLReader<?> reader();

	/**
	 * Put the given {@code value} to the reader result.
	 *
	 * @param value the reader result
	 */
	void put(final Object value);

	/**
	 * Return the current reader result value.
	 *
	 * @return the current reader result value
	 */
	Object value();

	/**
	 * Create a reader result for the given XML reader
	 *
	 * @param reader the XML reader
	 * @return a reader result for the given reader
	 */
	static ReaderResult of(final XMLReader<?> reader) {
		return reader.type() == Type.LIST
			? new ListResult(reader)
			: new ValueResult(reader);
	}

}

/**
 * Result object for values read from XML elements.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.2
 * @since 1.2
 */
final class ValueResult implements ReaderResult {

	private final XMLReader<?> _reader;
	private Object _value;

	ValueResult(final XMLReader<?> reader) {
		_reader = reader;
	}

	@Override
	public void put(final Object value) {
		_value = value;
	}

	@Override
	public XMLReader<?> reader() {
		return _reader;
	}


	@Override
	public Object value() {
		return _value;
	}

}

/**
 * Result object for list values read from XML elements.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.2
 * @since 1.2
 */
final class ListResult implements ReaderResult {

	private final XMLReader<?> _reader;
	private final List<Object> _value = new ArrayList<>();

	ListResult(final XMLReader<?> reader) {
		_reader = reader;
	}

	@Override
	public void put(final Object value) {
		if (value instanceof List<?>) {
			_value.addAll((List<?>)value);
		} else {
			_value.add(value);
		}
	}

	@Override
	public XMLReader<?> reader() {
		return _reader;
	}

	@Override
	public List<Object> value() {
		return _value;
	}

}
