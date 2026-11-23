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
 *    Antoine Vianey (github.com/avianey)
 */
package io.jenetics.jpx;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLProviderTest {

	public static final String MESSAGE = "provider does not support documentBuilderFactory";

	static final String SECRET = "jpx-external-entity-content";

	public static class TestXMLProvider extends XMLProvider {
		public DocumentBuilderFactory documentBuilderFactory() {
			throw new UnsupportedOperationException(MESSAGE);
		}
	}

	@Test(
		expectedExceptions = UnsupportedOperationException.class,
		expectedExceptionsMessageRegExp = MESSAGE,
		singleThreaded = true)
	public void checkXMLProviderSpiLoading() {
		final ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
		ClassLoader cl = new ClassLoader(threadClassLoader) {
			@Override
			public Enumeration<URL> getResources(final String name) throws IOException {
				if ("META-INF/services/io.jenetics.jpx.XMLProvider".equals(name)) {
					return new Enumeration<URL>() {

						private URL serviceFile =
							new File("src/test/resources/io/jenetics/jpx/io.jenetics.jpx.XMLProvider")
								.toURI().toURL();

						@Override
						public boolean hasMoreElements() {
							return serviceFile != null;
						}

						@Override
						public URL nextElement() {
							final URL url = serviceFile;
							serviceFile = null;
							return url;
						}

					};
				} else {
					return super.getResources(name);
				}
			}
		};
		XMLProvider.clear();
		Thread.currentThread().setContextClassLoader(cl);
		try {
			XMLProvider provider = XMLProvider.provider();
			provider.documentBuilderFactory();
		} finally {
			XMLProvider.clear();
			Thread.currentThread().setContextClassLoader(threadClassLoader);
		}
	}

	@Test(dataProvider = "externalEntityDocuments")
	public void xmlInputFactoryDoesNotResolveExternalEntities(final String xml) {
		final XMLInputFactory factory = new XMLProvider() {}.xmlInputFactory();

		final var text = new StringBuilder();
		try {
			final XMLStreamReader reader =
				factory.createXMLStreamReader(new StringReader(xml));
			while (reader.hasNext()) {
				if (reader.next() == XMLStreamConstants.CHARACTERS) {
					text.append(reader.getText());
				}
			}
		} catch (XMLStreamException ignore) {
			// Rejecting the document is fine, resolving the entity is not.
		}

		assertThat(text.toString()).doesNotContain(SECRET);
	}

	@Test(dataProvider = "externalEntityDocuments")
	public void documentBuilderFactoryDoesNotResolveExternalEntities(final String xml)
		throws ParserConfigurationException, IOException
	{
		final DocumentBuilder builder = new XMLProvider() {}
			.documentBuilderFactory()
			.newDocumentBuilder();

		String text = "";
		try {
			text = builder.parse(new InputSource(new StringReader(xml)))
				.getDocumentElement()
				.getTextContent();
		} catch (SAXException ignore) {
			// Rejecting the document is fine, resolving the entity is not.
		}

		assertThat(text).doesNotContain(SECRET);
	}

	@DataProvider
	public Object[][] externalEntityDocuments() throws IOException {
		final Path secret = Files.createTempFile("jpx-secret", ".txt");
		secret.toFile().deleteOnExit();
		Files.writeString(secret, SECRET);

		final Path dtd = Files.createTempFile("jpx-external", ".dtd");
		dtd.toFile().deleteOnExit();
		Files.writeString(
			dtd,
			"<!ENTITY secret SYSTEM \"%s\">".formatted(secret.toUri())
		);

		return new Object[][] {
			// External general entity.
			{"""
				<!DOCTYPE gpx [<!ENTITY secret SYSTEM "%s">]>
				<gpx>&secret;</gpx>
				""".formatted(secret.toUri())},
			// External parameter entity in the internal DTD subset.
			{"""
				<!DOCTYPE gpx [<!ENTITY %% dtd SYSTEM "%s"> %%dtd;]>
				<gpx>&secret;</gpx>
				""".formatted(dtd.toUri())},
			// External DTD subset.
			{"""
				<!DOCTYPE gpx SYSTEM "%s">
				<gpx>&secret;</gpx>
				""".formatted(dtd.toUri())}
		};
	}

}
