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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.0
 * @since 3.0
 */
final class NonCloseableInputStream extends InputStream {

	private final InputStream _input;

	NonCloseableInputStream(final InputStream input) {
		_input = requireNonNull(input);
	}

	@Override
	public int read() throws IOException {
		return _input.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return _input.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return _input.read(b, off, len);
	}

	@Override
	public byte[] readAllBytes() throws IOException {
		return _input.readAllBytes();
	}

	@Override
	public byte[] readNBytes(int len) throws IOException {
		return _input.readNBytes(len);
	}

	@Override
	public int readNBytes(byte[] b, int off, int len) throws IOException {
		return _input.readNBytes(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return _input.skip(n);
	}

	@Override
	public void skipNBytes(long n) throws IOException {
		_input.skipNBytes(n);
	}

	@Override
	public int available() throws IOException {
		return _input.available();
	}

	@Override
	public void close() {
	}

	@Override
	public void mark(int readlimit) {
		_input.mark(readlimit);
	}

	@Override
	public void reset() throws IOException {
		_input.reset();
	}

	@Override
	public boolean markSupported() {
		return _input.markSupported();
	}

	@Override
	public long transferTo(OutputStream out) throws IOException {
		return _input.transferTo(out);
	}

}
