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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class XMLTest {

	@Test(dataProvider = "extensionNamespaces")
	public void copyExtensionNamespaces(final String child, final String namespace)
		throws Exception
	{
		final String xml = """
			<extensions xmlns="http://www.topografix.com/GPX/1/1"
			    xmlns:e="urn:extension" xmlns:a="urn:attribute">%s</extensions>
			""".formatted(child);
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
		factory.setNamespaceAware(true);
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document source = builder.parse(new InputSource(new StringReader(xml)));
		final StringWriter output = new StringWriter();
		final var writer = new XMLStreamWriterAdapter(
			XMLOutputFactory.newDefaultFactory().createXMLStreamWriter(output)
		) {
			@Override
			public void setPrefix(final String prefix, final String uri)
				throws XMLStreamException
			{
				if ("xmlns".equals(prefix)) {
					throw new XMLStreamException("Reserved xmlns prefix");
				}
				super.setPrefix(prefix, uri);
			}
		};
		XML.copy(source.getDocumentElement(), writer);
		writer.flush();

		final Document result = builder.parse(new InputSource(new StringReader(output.toString())));
		Assert.assertEquals(result.getDocumentElement().getNamespaceURI(),
			"http://www.topografix.com/GPX/1/1");
		final var messages = result.getElementsByTagNameNS(namespace, "message");
		Assert.assertEquals(messages.getLength(), 1);
		final Element message = (Element)messages.item(0);
		Assert.assertEquals(message.getAttributeNS("urn:attribute", "code"), "7");
		Assert.assertEquals(message.getAttributeNS(null, "code"), "plain");
		Assert.assertEquals(message.getTextContent(), "text");
	}

	@DataProvider
	public Object[][] extensionNamespaces() {
		return new Object[][] {
			{"<e:message a:code=\"7\" code=\"plain\">text</e:message>", "urn:extension"},
			{"<message xmlns=\"urn:default\" a:code=\"7\" code=\"plain\">text</message>", "urn:default"},
			{"<message xmlns=\"\" a:code=\"7\" code=\"plain\">text</message>", null}
		};
	}

	@Test
	public void checkExtensionsNull() {
		Assert.assertNull(XML.checkExtensions(null));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void checkExtensionsMissingRootElement() throws ParserConfigurationException {
		final DocumentBuilderFactory dbf = XMLProvider.provider().documentBuilderFactory();
		final DocumentBuilder db = dbf.newDocumentBuilder();
		final Document doc = db.newDocument();

		XML.checkExtensions(doc);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void checkExtensionsWrongRootElement() throws ParserConfigurationException {
		final DocumentBuilderFactory dbf = XMLProvider.provider().documentBuilderFactory();
		final DocumentBuilder db = dbf.newDocumentBuilder();
		final Document doc = db.newDocument();
		final Element root = doc.createElement("ext");
		doc.appendChild(root);

		XML.checkExtensions(doc);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void checkExtensionsWrongRootElementNS() throws ParserConfigurationException {
		final DocumentBuilderFactory dbf = XMLProvider.provider().documentBuilderFactory();
		final DocumentBuilder db = dbf.newDocumentBuilder();
		final Document doc = db.newDocument();
		final Element root = doc.createElementNS(
			"http://www.topografix.com/GPX/1/f1",
			"extensions"
		);
		doc.appendChild(root);

		XML.checkExtensions(doc);
	}

}
