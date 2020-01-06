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

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class IOTest {

	@Test
	public void readWriteInt() throws IOException {
		final Random random = new Random();
		for (int i = 0; i < 1000; ++i) {
			final int value = random.nextInt();

			final ByteArrayOutputStream bout = new ByteArrayOutputStream(5);
			final DataOutputStream dout = new DataOutputStream(bout);
			IO.writeInt(value, dout);
			dout.flush();

			final ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			final DataInputStream din = new DataInputStream(bin);
			final int read = IO.readInt(din);

			Assert.assertEquals(read, value);
		}
	}

	@Test
	public void readWriteLong() throws IOException {
		final Random random = new Random();
		for (int i = 0; i < 1000; ++i) {
			final long value = random.nextLong();

			final ByteArrayOutputStream bout = new ByteArrayOutputStream(9);
			final DataOutputStream dout = new DataOutputStream(bout);
			IO.writeLong(value, dout);
			dout.flush();

			final ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			final DataInputStream din = new DataInputStream(bin);
			final long read = IO.readLong(din);

			Assert.assertEquals(read, value);
		}
	}

	@Test
	public void readWriteDoc() throws IOException {
		final Document value = GPXTest.doc();

		final ByteArrayOutputStream bout = new ByteArrayOutputStream(9);
		final DataOutputStream dout = new DataOutputStream(bout);
		IO.write(value, dout);
		dout.flush();

		final ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		final DataInputStream din = new DataInputStream(bin);
		final Document read = IO.readDoc(din);

		Assert.assertTrue(XML.equals(value, read));
	}

}
