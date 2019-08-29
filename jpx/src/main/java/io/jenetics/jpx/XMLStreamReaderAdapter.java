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

import static java.util.Objects.requireNonNull;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.3
 * @since 1.3
 */
class XMLStreamReaderAdapter implements XMLStreamReader, AutoCloseable {
	private final XMLStreamReader _reader;

	private boolean _consumed;

	XMLStreamReaderAdapter(final XMLStreamReader reader) {
		_reader = requireNonNull(reader);
	}

	void consumed() {
		_consumed = true;
	}

	boolean safeNext() throws XMLStreamException {
		final boolean result = hasNext();
		try {
			if (result && !_consumed) {
				next();
			}
		} finally {
			_consumed = false;
		}
		return result;
	}

	@Override
	public Object getProperty(final String name) {
		return _reader.getProperty(name);
	}

	@Override
	public int next() throws XMLStreamException {
		return _reader.next();
	}

	@Override
	public void require(
		final int type,
		final String namespaceURI,
		final String localName
	)
		throws XMLStreamException {
		_reader.require(type, namespaceURI, localName);
	}

	@Override
	public String getElementText() throws XMLStreamException {
		return _reader.getElementText();
	}

	@Override
	public int nextTag() throws XMLStreamException {
		return _reader.nextTag();
	}

	@Override
	public boolean hasNext() throws XMLStreamException {
		return _reader.hasNext();
	}

	@Override
	public void close() throws XMLStreamException {
		_reader.close();
	}

	@Override
	public String getNamespaceURI(final String prefix) {
		return _reader.getNamespaceURI(prefix);
	}

	@Override
	public boolean isStartElement() {
		return _reader.isStartElement();
	}

	@Override
	public boolean isEndElement() {
		return _reader.isEndElement();
	}

	@Override
	public boolean isCharacters() {
		return _reader.isCharacters();
	}

	@Override
	public boolean isWhiteSpace() {
		return _reader.isWhiteSpace();
	}

	@Override
	public String getAttributeValue(
		final String namespaceURI,
		final String localName
	) {
		return _reader.getAttributeValue(namespaceURI, localName);
	}

	@Override
	public int getAttributeCount() {
		return _reader.getAttributeCount();
	}

	@Override
	public QName getAttributeName(final int index) {
		return _reader.getAttributeName(index);
	}

	@Override
	public String getAttributeNamespace(final int index) {
		return _reader.getAttributeNamespace(index);
	}

	@Override
	public String getAttributeLocalName(final int index) {
		return _reader.getAttributeLocalName(index);
	}

	@Override
	public String getAttributePrefix(final int index) {
		return _reader.getAttributePrefix(index);
	}

	@Override
	public String getAttributeType(final int index) {
		return _reader.getAttributeType(index);
	}

	@Override
	public String getAttributeValue(final int index) {
		return _reader.getAttributeValue(index);
	}

	@Override
	public boolean isAttributeSpecified(final int index) {
		return _reader.isAttributeSpecified(index);
	}

	@Override
	public int getNamespaceCount() {
		return _reader.getNamespaceCount();
	}

	@Override
	public String getNamespacePrefix(final int index) {
		return _reader.getNamespacePrefix(index);
	}

	@Override
	public String getNamespaceURI(final int index) {
		return _reader.getNamespaceURI(index);
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return _reader.getNamespaceContext();
	}

	@Override
	public int getEventType() {
		return _reader.getEventType();
	}

	@Override
	public String getText() {
		return _reader.getText();
	}

	@Override
	public char[] getTextCharacters() {
		return _reader.getTextCharacters();
	}

	@Override
	public int getTextCharacters(
		final int sourceStart,
		final char[] target,
		final int targetStart,
		final int length
	)
		throws XMLStreamException
	{
		return _reader.getTextCharacters(sourceStart, target, targetStart, length);
	}

	@Override
	public int getTextStart() {
		return _reader.getTextStart();
	}

	@Override
	public int getTextLength() {
		return _reader.getTextLength();
	}

	@Override
	public String getEncoding() {
		return _reader.getEncoding();
	}

	@Override
	public boolean hasText() {
		return _reader.hasText();
	}

	@Override
	public Location getLocation() {
		return _reader.getLocation();
	}

	@Override
	public QName getName() {
		return _reader.getName();
	}

	@Override
	public String getLocalName() {
		return _reader.getLocalName();
	}

	@Override
	public boolean hasName() {
		return _reader.hasName();
	}

	@Override
	public String getNamespaceURI() {
		return _reader.getNamespaceURI();
	}

	@Override
	public String getPrefix() {
		return _reader.getPrefix();
	}

	@Override
	public String getVersion() {
		return _reader.getVersion();
	}

	@Override
	public boolean isStandalone() {
		return _reader.isStandalone();
	}

	@Override
	public boolean standaloneSet() {
		return _reader.standaloneSet();
	}

	@Override
	public String getCharacterEncodingScheme() {
		return _reader.getCharacterEncodingScheme();
	}

	@Override
	public String getPITarget() {
		return _reader.getPITarget();
	}

	@Override
	public String getPIData() {
		return _reader.getPIData();
	}
}
