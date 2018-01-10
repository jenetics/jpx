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

import java.io.IOException;
import java.util.Random;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.jpx.Length.Unit;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class LengthTest extends ObjectTester<Length> {

	@Override
	Supplier<Length> factory(final Random random) {
		return () -> Length.of(random.nextInt(12)*random.nextDouble(),Unit.METER);
	}

	@Test
	public void ofMeters() {
		Assert.assertEquals(
			Length.of(123, Unit.METER),
			Length.of(0.123, Unit.KILOMETER)
		);

		Assert.assertEquals(
			Length.of(123, Unit.METER).to(Unit.METER),
			Length.of(0.123, Unit.KILOMETER).to(Unit.METER)
		);
	}

	@Test
	public void ofKiloMeters() {
		Assert.assertEquals(
			Length.of(0.123, Unit.KILOMETER),
			Length.of(123, Unit.METER)
		);

		Assert.assertEquals(
			Length.of(0.123, Unit.KILOMETER).to(Unit.KILOMETER),
			Length.of(123, Unit.METER).to(Unit.KILOMETER)
		);
	}

	@Test(dataProvider = "toConversions")
	public void toMeter(final Unit unit, final double value) {
		final Length length = Length.of(1, unit);
		Assert.assertEquals(value, length.to(Unit.METER));
	}

	@DataProvider(name = "toConversions")
	public Object[][] toConversions() {
		return new Object[][] {
			{Unit.METER, 1},
			{Unit.KILOMETER, 1000},
			{Unit.INCH, 0.0254},
			{Unit.MILE, 1_609.344},
			{Unit.YARD, 0.9144}
		};
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(Length.class).verify();
	}

	@Test
	public void serialize() throws IOException, ClassNotFoundException {
		final Random random = new Random();
		final Object object = Length.of(random.nextInt(12)*random.nextDouble(),Unit.METER);
		Serialization.test(object);
	}

}
