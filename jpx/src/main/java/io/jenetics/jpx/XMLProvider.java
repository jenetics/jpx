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
 *    Antoine Vianey (https://github.com/avianey)
 */
package io.jenetics.jpx;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * A {@link ServiceLoader} for managing XML factories used by the library.
 * Custom implementation should be referenced in a META-INF/services/io.jenetics.jpx.XMLProvider file
 *
 * @see XMLProviderImpl
 */
public abstract class XMLProvider {

	private static XMLProvider INSTANCE;
	private static final Object lock = new Object();

	protected XMLProvider() {
	}

	/**
	 * Returns {@link XMLInputFactory} to be used for reading files.
	 *
	 * @return the xml input factory
	 */
	public abstract XMLInputFactory xmlInputFactory();

	/**
	 * Returns {@link XMLOutputFactory} to be used for writing files.
	 *
	 * @return the xml output factory
	 */
	public abstract XMLOutputFactory xmlOutputFactory();

	/**
	 * Returns the {@link DocumentBuilderFactory} used for handling extensions documents
	 *
	 * @return the document builder factory
	 */
	public abstract DocumentBuilderFactory documentBuilderFactory();

	public static XMLProvider provider() {
		if (INSTANCE == null) {
			synchronized (lock) {
				if (INSTANCE == null) {
					loadInstance();
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * Clear current spi to allow hot reloading a new one...
	 */
	protected static void clear() {
		if (INSTANCE != null) {
			synchronized (lock) {
				if (INSTANCE != null) {
					INSTANCE = null;
				}
			}
		}
	}

	private static void loadInstance() {
		ServiceLoader<XMLProvider> loader = ServiceLoader.load(XMLProvider.class);
		Iterator<XMLProvider> providers = loader.iterator();
		if (providers.hasNext()) {
			INSTANCE = providers.next();
		} else {
			INSTANCE = new XMLProviderImpl();
		}
	}

}
