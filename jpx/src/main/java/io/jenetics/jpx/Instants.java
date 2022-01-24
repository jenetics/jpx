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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.Instant;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.0
 * @since 1.2
 */
final class Instants {

	private Instants() {
	}

	static void write(final Instant time, final DataOutput out)
		throws IOException
	{
		IO.writeLong(time.getEpochSecond(), out);
		IO.writeInt(time.getNano(), out);
	}

	static Instant read(final DataInput in) throws IOException {
		final var seconds = IO.readLong(in);
		final var nanos = IO.readInt(in);
		return Instant.ofEpochSecond(seconds, nanos);
	}

}
