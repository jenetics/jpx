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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.testng.Assert;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class Serialization {

	private Serialization() {
	}

	public static void test(final Object object)
		throws IOException, ClassNotFoundException
	{
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try (ObjectOutputStream oout = new ObjectOutputStream(bout)) {
			oout.writeObject(object);
		}

		final byte[] data = bout.toByteArray();
		//System.out.println("Length: " + data.length);
		final ByteArrayInputStream bin = new ByteArrayInputStream(data);
		try (ObjectInputStream oin = new ObjectInputStream(bin)) {
			final Object obj = oin.readObject();
			Assert.assertEquals(obj, object);
		}
	}

	static byte[] toBytes(final Object... objects) throws IOException {
		int existing = 0;
		for (int i = 0; i < objects.length; ++i) {
			if (objects[i] != null) {
				existing |= 1 << i;
			}
		}

		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try (ObjectOutputStream oout = new ObjectOutputStream(bout)) {
			oout.writeInt(objects.length);
			oout.writeInt(existing);
			for (Object object : objects) {
				if (object != null) {
					oout.writeObject(object);
				}
			}
		}

		return bout.toByteArray();
	}

	static Object[] fromBytes(final byte[] data) throws IOException, ClassNotFoundException {
		final ByteArrayInputStream bin = new ByteArrayInputStream(data);
		try (ObjectInputStream oin = new ObjectInputStream(bin)) {
			final int length = oin.readInt();
			final int existing = oin.readInt();

			final Object[] objects = new Object[length];
			for (int i = 0; i < length; ++i) {
				final boolean exists = (existing & 1 << i) != 0;
				if (exists) {
					objects[i] = oin.readObject();
				}
			}

			return objects;
		}
	}

}
