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

import java.util.Optional;
import java.util.function.Function;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Helper class for simplifying XML stream writing.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.2
 * @since 1.0
 */
@FunctionalInterface
interface XMLWriter<T> {

	/**
	 * Write the data of type {@code T} to the given XML stream writer.
	 *
	 * @param xml the underlying {@code XMLStreamXMLWriter}, where the value is
	 *        written to
	 * @param data the value to write
	 * @throws XMLStreamException if writing the data fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	void write(final XMLStreamWriter xml, final T data)
		throws XMLStreamException;

	/**
	 * Maps this writer to a different base type. Mapping to a different data
	 * type is necessary when you are going to write <em>sub</em>-objects of
	 * your basic data type {@code T}. E.g. the chromosome length or the
	 * {@code min} and {@code max} value of an {@code IntegerChromosome}.
	 *
	 * @param mapper the mapper function
	 * @param <B> the new data type of returned writer
	 * @return a writer with changed type
	 */
	default <B> XMLWriter<B>
	map(final Function<? super B, ? extends T> mapper) {
		return (xml, data) -> {
			if (data != null && data != Optional.empty()) {
				final T value = mapper.apply(data);
				if (value != null && value != Optional.empty()) {
					write(xml, value);
				}
			}
		};
	}

	/* *************************************************************************
	 * *************************************************************************
	 * Static factory methods.
	 * *************************************************************************
	 * ************************************************************************/

	/* *************************************************************************
	 * Creating attribute writer.
	 * ************************************************************************/

	/**
	 * Writes the attribute with the given {@code name} to the current
	 * <em>outer</em> element.
	 *
	 * <pre>{@code
	 * final XMLWriter<String> writer1 = elem("element", attr("attribute"));
	 * }</pre>
	 *
	 * @param name the attribute name
	 * @param <T> the writer base type
	 * @return a new writer instance
	 * @throws NullPointerException if the attribute {@code name} is {@code null}
	 */
	static <T> XMLWriter<T> attr(final String name) {
		requireNonNull(name);

		return (xml, data) -> {
			if (data != null && data != Optional.empty()) {
				xml.writeAttribute(name, data.toString());
			}
		};
	}

//	/**
//	 * Writes the attribute with the given {@code name} and a constant
//	 * {@code value} to the current <em>outer</em> element.
//	 *
//	 * <pre>{@code
//	 * final XMLWriter<MyObject> = elem("element", attr("version", "1.0"));
//	 * }</pre>
//	 *
//	 * @param name the attribute name
//	 * @param value the attribute value
//	 * @param <T> the writer base type
//	 * @return a new writer instance
//	 * @throws NullPointerException if one of the {@code name} is {@code null}
//	 */
//	static <T> XMLWriter<T> attr(
//		final String name,
//		final Object value
//	) {
//		return attr(name).map(data -> value);
//	}


	/* *************************************************************************
	 * Creating element writer.
	 * ************************************************************************/

	static <T> XMLWriter<T> ns(final String namespace) {
		return (xml, data) -> xml.writeDefaultNamespace(namespace);
	}

	/**
	 * Create a new {@code XMLWriter}, which writes a XML element with the given
	 * name and writes the given children into it.
	 *
	 * @param name the root element name
	 * @param children the XML child elements
	 * @param <T> the writer base type
	 * @return a new writer instance
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	@SafeVarargs
	static <T> XMLWriter<T> elem(
		final String name,
		final XMLWriter<? super T>... children
	) {
		requireNonNull(name);
		requireNonNull(children);

		return (xml, data) -> {
			if (data != null && data != Optional.empty()) {
				if (children.length > 0) {
					xml.writeStartElement(name);
					for (XMLWriter<? super T> child : children) {
						child.write(xml, data);
					}
					xml.writeEndElement();
				}
			}
		};
	}

	static XMLWriter<String> elem(final String name) {
		return elem(name, text());
	}


	/**
	 * Create a new text {@code XMLWriter}, which writes the given data as string
	 * to the outer element.
	 *
	 * @param <T> the data type, which is written as string to the outer element
	 * @return a new text writer
	 */
	static <T> XMLWriter<T> text() {
		return (xml, data) -> {
			if (data != null && data != Optional.empty()) {
				xml.writeCharacters(data.toString());
			}
		};
	}

	static <N extends Number> XMLWriter<N> number() {
		return (xml, data) -> {
			if (data != null) {
				xml.writeCharacters(Double.toString(data.doubleValue()));
			}
		};
	}

	static XMLWriter<Document> doc(final String name) {
		requireNonNull(name);

		return (xml, data) -> {
			if (data != null) {
				final Element root = data.getDocumentElement();
				if (!name.equals(root.getLocalName())) {
					data.renameNode(root, data.getNamespaceURI(), name);
				}
				//data.normalizeDocument();

				XML.copy(XML.clean(root), new XMLStreamWriterAdapter(xml) {
					@Override
					public void writeEndDocument() {}
					@Override
					public void writeStartDocument() {}
					@Override
					public void writeStartDocument(String version) {}
					@Override
					public void writeStartDocument(String encoding, String version) {}
				});
			}
		};
	}

//	/**
//	 * Creates a new {@code XMLWriter}, which writes the given {@code children} as
//	 * sub-elements, defined by the given {@code childXMLWriter}.
//	 *
//	 * @param name the enclosing element name used for each data value
//	 * @param writer the sub-element writer
//	 * @param <T> the writer base type
//	 * @return a new writer instance
//	 * @throws NullPointerException if one of the arguments is {@code null}
//	 */
//	static <T> XMLWriter<Iterable<T>> elems(
//		final String name,
//		final XMLWriter<? super T> writer
//	) {
//		requireNonNull(name);
//		requireNonNull(writer);
//
//		return (xml, data) -> {
//			if (data != null) {
//				for (T value : data) {
//					if (value != null && value != Optional.empty()) {
//						xml.writeStartElement(name);
//						writer.write(xml, value);
//						xml.writeEndElement();
//					}
//				}
//			}
//		};
//	}

	/**
	 * Creates a new {@code XMLWriter}, which writes the given {@code children} as
	 * sub-elements, defined by the given {@code childXMLWriter}.
	 *
	 * @param writer the sub-element writer
	 * @param <T> the writer base type
	 * @return a new writer instance
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	static <T> XMLWriter<Iterable<T>> elems(final XMLWriter<? super T> writer) {
		requireNonNull(writer);

		return (xml, data) -> {
			if (data != null) {
				for (T value : data) {
					if (value != null && value != Optional.empty()) {
						writer.write(xml, value);
					}
				}
			}
		};
	}

//	/**
//	 * Adds a XML prolog element written by the given {@code writer}. The default
//	 * values for encoding and version is set to "UTF-8" and "1.0", respectively.
//	 *
//	 * <pre> {@code
//	 * <?xml version="1.0" encoding="UTF-8"?>
//	 * }</pre>
//	 *
//	 * @param writer the root element writer
//	 * @param <T> the writer data type
//	 * @return a new writer instance
//	 */
//	public static <T> XMLWriter<T> doc(final XMLWriter<? super T> writer) {
//		return (xml, data) -> {
//			xml.writeStartDocument("UTF-8", "1.0");
//			writer.write(xml, data);
//			xml.writeEndDocument();
//		};
//	}

}
