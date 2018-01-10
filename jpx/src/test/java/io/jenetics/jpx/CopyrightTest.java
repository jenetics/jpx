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

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Year;
import java.util.Random;
import java.util.function.Supplier;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class CopyrightTest extends XMLStreamTestBase<Copyright> {

	@Override
	public Supplier<Copyright> factory(Random random) {
		return () -> nextCopyright(random);
	}

	@Override
	protected Params<Copyright> params(final Random random) {
		return new Params<>(
			() -> nextCopyright(random),
			Copyright.reader(),
			Copyright::write
		);
	}

	private static URI uri(final Random random) {
		try {
			return new URI(format("http://uri.com/%s", random.nextInt(100)));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static Copyright nextCopyright(final Random random) {
		return Copyright.of(
			format("author_%s", random.nextInt(100)),
			random.nextBoolean()
				? Year.of(random.nextInt(1000))
				: null,
			random.nextBoolean()
				? uri(random)
				: null
		);
	}

	@Test
	public void nullURIString() {
		Copyright.of("author", 23, (String)null);
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(Copyright.class).verify();
	}

}
