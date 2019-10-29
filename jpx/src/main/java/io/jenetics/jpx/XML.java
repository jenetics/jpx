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
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;
import static javax.xml.transform.OutputKeys.VERSION;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.5
 * @since 1.5
 */
final class XML {

	private static final class TFHolder {
		private static final TFHolder INSTANCE = new TFHolder();

		final TransformerFactory factory;

		private TFHolder() {
			factory = TransformerFactory.newInstance();
		}
	}

	private XML() {
	}


	private static void __copy(final Source source, final Result sink)
		throws XMLStreamException
	{
		try {
			final Transformer transformer = TFHolder.INSTANCE.factory
				.newTransformer();

			transformer.setOutputProperty(OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(VERSION, "1.0");

			transformer.transform(source, sink);
		} catch (TransformerException e) {
			throw new XMLStreamException(e);
		}
	}

	static void copy(final XMLStreamReader source, final Document sink)
		throws XMLStreamException
	{
		__copy(new StAXSource(source), new DOMResult(sink));
	}

	static void copy(final Node source, final XMLStreamWriter sink)
		throws XMLStreamException
	{
		__copy(new DOMSource(source), new StAXResult(sink));
	}

	static void copy(final Node source, final OutputStream sink)
		throws IOException
	{
		try {
			__copy(new DOMSource(source), new StreamResult(sink));
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	private static String toString(final Node source) {
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

			factory.setValidating(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setNamespaceAware(true);

			return factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new XMLStreamException(e);
		}
	}

	static Document parse(final String xml) {
		try {
			final Document doc = builder().newDocument();

			final ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes(UTF_8));
			__copy(new StreamSource(in), new DOMResult(doc));
			return clean(doc);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	static Document clone(final Document doc) {
		if (doc == null) return null;

		try {
			final Transformer transformer = TFHolder.INSTANCE.factory
				.newTransformer();

			final DOMSource source = new DOMSource(doc);
			final DOMResult result = new DOMResult();
			transformer.transform(source,result);
			return (Document)result.getNode();
		} catch (TransformerException e) {
			throw (DOMException)
				new DOMException(DOMException.NOT_SUPPORTED_ERR, e.getMessage())
					.initCause(e);
		}
	}

	static boolean equals(final Node n1, final Node n2) {
		if (n1 == n2) return true;
		if (n1 == null || n2 == null) return false;
		if (!Objects.equals(n1.getNodeValue(), n2.getNodeValue())) return false;
		if (!equals(n1.getAttributes(), n2.getAttributes())) return false;

		final NodeList nl1 = n1.getChildNodes();
		final NodeList nl2 = n2.getChildNodes();
		if (nl1.getLength() != nl2.getLength()) return false;
		for (int i = 0; i < nl1.getLength(); ++i) {
			if (!equals(nl1.item(i), nl2.item(i))) return false;
		}

		return true;
	}

	private static boolean equals(final NamedNodeMap a1, final NamedNodeMap a2) {
		if (a1 == null && a2 == null) return true;
		if (a1 == null || a2 == null) return false;
		if (a1.getLength() != a2.getLength()) return false;

		for (int i = 0; i < a1.getLength(); ++i) {
			final String name = a1.item(i).getNodeName();
			if (!"xmlns".equals(name)) {
				final String v1 = a1.item(i).getNodeValue();
				final String v2 = a2.getNamedItem(a1.item(i).getNodeName()).getNodeValue();

				if (!Objects.equals(v1, v2)) return false;
			}
		}

		return true;
	}

	private static boolean isEmpty(final Document doc) {
		return doc == null ||
			doc.getDocumentElement().getChildNodes().getLength() == 0;
	}

	private static <T extends Node> T clean(final T node) {
		if (node == null) return null;

		node.normalize();
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

	static Document removeNS(final Document doc) {
		if (doc == null) return null;

		final Node root = doc.getDocumentElement();
		final Element newRoot = doc.createElement(root.getNodeName());

		final NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			newRoot.appendChild(children.item(i).cloneNode(true));
		}

		doc.replaceChild(newRoot, root);
		return doc;
	}

	static Document extensions(final Document extensions) {
		final Document doc = XML.clean(extensions);
		return XML.isEmpty(doc) ? null : doc;
	}

	static Document checkExtensions(final Document extensions) {
		if (extensions != null) {
			final Element root = extensions.getDocumentElement();

			if (root == null) {
				throw new IllegalArgumentException(
					"'extensions' has no document element."
				);
			}

			if (!"extensions".equals(root.getNodeName())) {
				throw new IllegalArgumentException(format(
					"Expected 'extensions' root element, but got '%s'.",
					root.getNodeName()
				));
			}

			if (root.getNamespaceURI() != null) {
				final String ns = root.getNamespaceURI();
				if (!ns.isEmpty() &&
					!ns.startsWith("http://www.topografix.com/GPX/1/1") &&
					!ns.startsWith("http://www.topografix.com/GPX/1/0"))
				{
					throw new IllegalArgumentException(format(
						"Invalid document namespace: '%s'.", ns
					));
				}
			}
		}

		return extensions;
	}

}
