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

import java.util.Random;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.jpx.Speed.Unit;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
@Test
public class SpeedTest extends ObjectTester<Speed> {

	@Override
	Supplier<Speed> factory(final Random random) {
		return () -> Speed.of(random.nextDouble(), Unit.METERS_PER_SECOND);
	}

	@Test
	public void units() {
		Assert.assertEquals(
			0.2777777777777778,
			Unit.METERS_PER_SECOND.convert(1, Unit.KILOMETERS_PER_HOUR)
		);

		Assert.assertEquals(
			0.5144444444444445,
			Unit.METERS_PER_SECOND.convert(1, Unit.KNOTS)
		);

		Assert.assertEquals(
			331.3,
			Unit.METERS_PER_SECOND.convert(1, Unit.MACH)
		);

		Assert.assertEquals(
			0.44704,
			Unit.METERS_PER_SECOND.convert(1, Unit.MILES_PER_HOUR)
		);
	}

	@Test(dataProvider = "toConversions")
	public void toMetersPerSecond(final Speed.Unit unit, final double value) {
		final Speed speed = Speed.of(1, unit);
		Assert.assertEquals(value, speed.to(Unit.METERS_PER_SECOND));
	}

	@DataProvider(name = "toConversions")
	public Object[][] toConversions() {
		return new Object[][] {
			{Unit.METERS_PER_SECOND, 1},
			{Unit.KILOMETERS_PER_HOUR, 0.2777777777777778},
			{Unit.KNOTS, 0.5144444444444445},
			{Unit.MILES_PER_HOUR, 0.44704},
			{Unit.MACH, 331.3}
		};
	}

}
