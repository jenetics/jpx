/*
 * Java Genetic Algorithm Library (@__identifier__@).
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ZonedDateTimesTest {

	private static final int MIN_OFFSET = ZoneOffset.MIN.getTotalSeconds();
	private static final int MAX_OFFSET = ZoneOffset.MAX.getTotalSeconds();

	private static ZonedDateTime nextZonedDataTime(final Random random) {
		final int seconds = Math.abs(random.nextInt());
		final ZoneOffset offset = ZoneOffset.ofTotalSeconds(
			random.nextInt(MAX_OFFSET - MIN_OFFSET) + MIN_OFFSET
		);

		return ZonedDateTime.ofInstant(Instant.ofEpochSecond(seconds), offset);
	}

	@Test
	public void readWriteZonedDateTime() throws IOException {
		final Random random = new Random();

		for (int i = 0; i < 1000; ++i) {
			final ZonedDateTime zdt = nextZonedDataTime(random);

			final ByteArrayOutputStream bout = new ByteArrayOutputStream();
			final DataOutputStream dout = new DataOutputStream(bout);

			ZonedDateTimes.write(zdt, dout);

			final ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			final DataInputStream din = new DataInputStream(bin);
			final ZonedDateTime read = ZonedDateTimes.read(din);
			Assert.assertEquals(read, zdt);
		}
	}

}
