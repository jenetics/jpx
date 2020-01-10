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

import org.testng.annotations.Test;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class XMLProviderTest {

	public static final String MESSAGE = "provider does not support documentBuilderFactory";

	public static class TestXMLProvider extends XMLProviderImpl {
		public TestXMLProvider() {
			super();
		}
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


}
