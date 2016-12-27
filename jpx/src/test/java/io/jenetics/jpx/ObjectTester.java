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

import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class ObjectTester<T> {

	abstract Supplier<T> factory(final Random random);

	protected List<T> newEqualObjects(final int length) {
		return Stream
			.generate(() -> factory(new Random(5647)).get())
			.limit(length)
			.collect(toList());
	}

	@Test
	public void equals() {
		final List<T> same = newEqualObjects(5);

		final Object that = same.get(0);
		for (int i = 1; i < same.size(); ++i) {
			final Object other = same.get(i);

			Assert.assertEquals(other, other);
			Assert.assertEquals(other, that);
			Assert.assertEquals(that, other);
			Assert.assertEquals(that.hashCode(), other.hashCode());
		}
	}

	@Test
	public void notEquals() {
		for (int i = 0; i < 10; ++i) {
			final Object that = factory(new Random()).get();
			final Object other = factory(new Random()).get();

			if (that.equals(other)) {
				Assert.assertTrue(other.equals(that));
				Assert.assertEquals(that.hashCode(), other.hashCode());
			} else {
				Assert.assertFalse(other.equals(that));
				Assert.assertFalse(that.equals(other));
			}
		}
	}

	@Test
	public void notEqualsNull() {
		final Object that = factory(new Random()).get();
		Assert.assertFalse(that == null);
	}

	@Test
	public void notEqualsStringType() {
		final Object that = factory(new Random()).get();
		Assert.assertFalse(that.equals("__some_string__"));
	}

	@Test
	public void notEqualsClassType() {
		final Object that = factory(new Random()).get();
		Assert.assertFalse(that.equals(Class.class));
	}

	@Test
	public void hashCodeMethod() {
		final List<T> same = newEqualObjects(5);

		final Object that = same.get(0);
		for (int i = 1; i < same.size(); ++i) {
			final Object other = same.get(i);

			Assert.assertEquals(that.hashCode(), other.hashCode());
		}
	}

	@Test
	public void toStringMethod() {
		final List<T> same = newEqualObjects(5);

		final Object that = same.get(0);
		for (int i = 1; i < same.size(); ++i) {
			final Object other = same.get(i);

			Assert.assertEquals(that.toString(), other.toString());
			Assert.assertNotNull(other.toString());
		}
	}

	@Test
	public void objectSerialize() throws Exception {
		final Object object = factory(new Random()).get();

		if (object instanceof Serializable) {
			for (int i = 0; i < 10; ++i) {
				final Serializable serializable =
					(Serializable)factory(new Random()).get();

				test(serializable);
			}
		}
	}

	public static void test(final Object object)
		throws IOException, ClassNotFoundException
	{
		try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
			 ObjectOutputStream oout = new ObjectOutputStream(bout))
		{
			oout.writeObject(object);
			oout.flush();

			final byte[] data = bout.toByteArray();
			try (ByteArrayInputStream in = new ByteArrayInputStream(data);
				 ObjectInputStream oin = new ObjectInputStream(in))
			{
				final Object copy = oin.readObject();
				Assert.assertEquals(copy, object);
			}
		}
	}

}
