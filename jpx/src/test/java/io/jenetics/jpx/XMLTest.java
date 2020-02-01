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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class XMLTest {

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
