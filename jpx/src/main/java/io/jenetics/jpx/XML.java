/*
 * Java Genetic Algorithm Library (@__identifier__@).
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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class XML {
	private XML() {
	}

	static void copy(final XMLStreamReader source, final XMLStreamWriter sink)
		throws XMLStreamException
	{
		copy(new StAXSource(source), new StAXResult(sink));
	}

	private static void copy(final Source source, final Result sink)
		throws XMLStreamException
	{
		try {
			final Transformer transformer = TransformerFactory
				.newInstance()
				.newTransformer();

			transformer.transform(source, sink);
		} catch (TransformerException e) {
			throw new XMLStreamException(e);
		}
	}

	static void copy(final XMLStreamReader source, final Document sink)
		throws XMLStreamException
	{
		copy(new StAXSource(source), new DOMResult(sink));
	}

	static void copy(final Node source, final XMLStreamWriter sink)
		throws XMLStreamException
	{
		copy(new DOMSource(source), new StAXResult(sink));
	}

	static void copy(final Node source, final OutputStream sink)
		throws IOException
	{
		try {
			copy(new DOMSource(source), new StreamResult(sink));
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	static String toString(final Node source) {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			copy(source, out);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return out.toString();
	}

	static DocumentBuilder builder()
		throws XMLStreamException
	{
		try {
			final DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();

			//factory.setValidating(true);
			//factory.setIgnoringElementContentWhitespace(true);

			return factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new XMLStreamException(e);
		}
	}

	static Document parse(final String xml) {
		try {
			final Document doc = builder().newDocument();

			final ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes(UTF_8));
			copy(new StreamSource(in), new DOMResult(doc));
			return clean(doc);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	static <T extends Node> T clean(final T node) {
		final List<Node> remove = new ArrayList<>();
		clean(node, remove);
		for (Node n : remove) {
			if (n.getParentNode() != null) {
				n.getParentNode().removeChild(n);
			}
		}

		return node;
	}

	private static void clean(final Node node, final List<Node> remove) {
		if (node.getNodeType() == Node.TEXT_NODE
			&& isEmpty(node.getTextContent()))
		{
			remove.add(node);
		}

		final NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			clean(list.item(i), remove);
		}
	}

	private static boolean isEmpty(final String text) {
		if (text == null) return true;
		if (text.isEmpty()) return true;
		for (int i = 0; i < text.length(); ++i) {
			if (!Character.isWhitespace(text.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	static boolean equals(final Node n1, final Node n2) {
		if (n1 == n2) return true;
		if (n1 != null && n2 != null) {
			return n1.isEqualNode(n2);
		}
		return false;
	}

	static int hashCode(final Node node) {

		return 1;
	}

}
