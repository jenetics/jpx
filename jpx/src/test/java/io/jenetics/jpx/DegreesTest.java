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

import nl.jqno.equalsverifier.EqualsVerifier;

import java.util.Random;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class DegreesTest extends ObjectTester<Degrees> {

	@Override
	Supplier<Degrees> factory(final Random random) {
		return () -> Degrees.ofDegrees(random.nextDouble());
	}

	@Test
	public void ofRadians() {
		Assert.assertEquals(
			Degrees.ofRadians(3),
			Degrees.ofDegrees(Math.toDegrees(3))
		);

		Assert.assertEquals(
			Degrees.ofRadians(3).toRadians(),
			Degrees.ofDegrees(Math.toDegrees(3)).toRadians()
		);
	}

	@Test
	public void ofDegrees() {
		Assert.assertEquals(
			Degrees.ofDegrees(3),
			Degrees.ofRadians(Math.toRadians(3))
		);

		Assert.assertEquals(
			Degrees.ofDegrees(3).toDegrees(),
			Degrees.ofRadians(Math.toRadians(3)).toDegrees()
		);
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(Degrees.class).verify();
	}

}
