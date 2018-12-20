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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Nodes {
	private Nodes() {
	}

	static void write(final List<Node> nodes, final DataOutput out)
		throws IOException
	{
		if (nodes.isEmpty()) {
			IO.writeInt(0, out);
			return;
		}

		final Document doc;
		try {
			doc = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.newDocument();
		} catch (ParserConfigurationException e) {
			throw new IOException(e);
		}

		final Element root = doc.createElement("extensions");
		doc.appendChild(root);
		nodes.forEach(node -> {
			//final Node n = node.getOwnerDocument().importNode(node, true);
			//root.appendChild(n);
			final Node n = doc.importNode(node, true);
			root.appendChild(n);
		});
		final DOMSource source = new DOMSource(doc);

		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		final StreamResult result = new StreamResult(bout);

		try {
			TransformerFactory
				.newInstance()
				.newTransformer()
				.transform(source, result);
		} catch (TransformerException e) {
			throw new IOException(e);
		}

		final byte[] data = bout.toByteArray();
		IO.writeInt(data.length, out);
		out.write(data);
	}

	static List<Node> read(final DataInput in) throws IOException {
		final int size = IO.readInt(in);
		System.out.println("SIZE: " + size);
		if (size == 0) {
			return Collections.emptyList();
		}

		final byte[] data = new byte[size];
		in.readFully(data);
		System.out.println(new String(data));

		final ByteArrayInputStream bin = new ByteArrayInputStream(data);

		final Document doc;
		try {
			final DocumentBuilder builder = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder();

			doc = builder.parse(bin);
		} catch (ParserConfigurationException|SAXException e) {
			throw new IOException(e);
		}

		final Element root = doc.getDocumentElement();
		final NodeList children = root.getChildNodes();

		final List<Node> nodes = new ArrayList<>();
		for (int i = 0; i < children.getLength(); ++i) {
			System.out.println(children.item(i));
			nodes.add(children.item(i));
		}

		return nodes;
	}

}
