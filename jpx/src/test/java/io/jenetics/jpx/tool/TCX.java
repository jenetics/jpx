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
package io.jenetics.jpx.tool;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;
import static javax.xml.transform.OutputKeys.VERSION;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TCX {

	private static final DateTimeFormatter DATE_TIME_FORMATTER =
		DateTimeFormatter.ofPattern("YYYYMMdd'T'HHmm");

	public static void main(final String[] args) throws Exception {
		final var factory = DocumentBuilderFactory.newInstance();
		final var builder = factory.newDocumentBuilder();

		final var path = Path.of("/home/fwilhelm/Downloads/sunibiker_2016.04.06_export.tcx");
		final var out = Path.of("/home/fwilhelm/Downloads/out");
		final var doc = parse(Files.readString(path));

		final var activities = doc.getElementsByTagName("Activity");

		for (int i = 0; i < activities.getLength(); ++i) {
			final var result = builder.newDocument();
			final var root = result.createElement("TrainingCenterDatabase");
			root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			root.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
			root.setAttribute("xmlns", "http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2");
			result.appendChild(root);

			var node = activities.item(i);
			node = node.getParentNode().removeChild(node);
			result.adoptNode(node);

			final var sport = node.getAttributes().getNamedItem("Sport").getNodeValue();
			final var start = startTime(node);

			final var acts = result.createElement("Activities");
			acts.appendChild(node);
			root.appendChild(acts);

			final var file = Path.of(
				out.toString(),
				format("%s-%s.tcx", DATE_TIME_FORMATTER.format(start), sport)
			);
			System.out.println(file);
			try (var os = Files.newOutputStream(file)) {
				write(result, os);
			}
		}
	}

	private static void write(final Node xml, final OutputStream out)
		throws TransformerFactoryConfigurationError, TransformerException
	{
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");

		tf.transform(new DOMSource(xml), new StreamResult(out));
	}

	private static LocalDateTime startTime(final Node activity) {
		final var nodes = activity.getChildNodes();
		for (int i = 0; i < nodes.getLength(); ++i) {
			final var node = nodes.item(i);
			if ("Lap".equals(node.getLocalName())) {
				return LocalDateTime.parse(node.getAttributes().getNamedItem("StartTime").getNodeValue());
			}
		}
		return null;
	}

	private static Document parse(final String xml) {
		try {
			final Document doc = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.newDocument();

			final ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes(UTF_8));
			__copy(new StreamSource(in), new DOMResult(doc));
			return doc;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static void __copy(final Source source, final Result sink)
		throws XMLStreamException
	{
		try {
			final Transformer transformer = TransformerFactory
				.newInstance()
				.newTransformer();

			transformer.setOutputProperty(OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(VERSION, "1.0");

			transformer.transform(source, sink);
		} catch (TransformerException e) {
			throw new XMLStreamException(e);
		}
	}

}
