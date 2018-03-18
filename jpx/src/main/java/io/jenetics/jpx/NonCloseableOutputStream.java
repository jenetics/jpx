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
import java.io.OutputStream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.3
 * @since 1.3
 */
final class NonCloseableOutputStream extends OutputStream {

	private final OutputStream _output;

	NonCloseableOutputStream(final OutputStream output) {
		_output = requireNonNull(output);
	}

	@Override
	public void write(final int b) throws IOException {
		_output.write(b);
	}

	@Override
	public void write(final byte[] b) throws IOException {
		_output.write(b);
	}

	@Override
	public void write(final byte[] b, final int off, final int len)
		throws IOException
	{
		_output.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		_output.flush();
	}

	@Override
	public void close() {
	}

}
