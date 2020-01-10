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

public class XMLProviderImpl extends XMLProvider {

	public XMLProviderImpl() {
		super();
	}

	public XMLInputFactory xmlInputFactory() {
		return XMLInputFactory.newInstance();
	}

	public XMLOutputFactory xmlOutputFactory() {
		return XMLOutputFactory.newInstance();
	}

	public DocumentBuilderFactory documentBuilderFactory() {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setNamespaceAware(true);
		return factory;
	}
}
