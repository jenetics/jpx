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

import static java.lang.String.format;

import nl.jqno.equalsverifier.EqualsVerifier;

import java.io.IOException;
import java.util.Random;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class EmailTest extends XMLStreamTestBase<Email> {

	@Override
	public Supplier<Email> factory(Random random) {
		return () -> nextEmail(random);
	}

	@Override
	protected Params<Email> params(final Random random) {
		return new Params<>(
			() -> nextEmail(random),
			Email.reader(),
			Email::write
		);
	}

	public static Email nextEmail(final Random random) {
		return Email.of(
			format("id_%s", random.nextInt(100)),
			format("domain_%s", random.nextInt(100))
		);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fromEmptyAddress() {
		Email.of("");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fromShortAddress1() {
		Email.of("@");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fromShortAddress2() {
		Email.of("a@");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fromShortAddress3() {
		Email.of("@b");
	}

	@Test
	public void fromAddress() {
		Assert.assertEquals(
			Email.of("a@b"),
			Email.of("a", "b")
		);
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(Email.class).verify();
	}

	@Test
	public void serialize() throws IOException, ClassNotFoundException {
		final Object object = nextEmail(new Random());
		Serialization.test(object);
	}

}
