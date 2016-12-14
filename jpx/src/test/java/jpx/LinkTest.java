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
package jpx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LinkTest {

	@Test
	public void writeXML() throws XMLStreamException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final XMLStreamWriter writer = XMLOutputFactory
			.newInstance()
			.createXMLStreamWriter(out);

		//Link.of("http://jenetics.io").writeTo(out);
		Link.of("http://jenetics.io", "some text", "some type").write(writer);

		final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		final XMLStreamReader reader = XMLInputFactory
			.newFactory()
			.createXMLStreamReader(in);

		reader.next();
		final Link link = Link.read(reader);
		System.out.println(link);
	}

}
