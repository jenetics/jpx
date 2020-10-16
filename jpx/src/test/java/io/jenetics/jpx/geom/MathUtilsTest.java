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
package io.jenetics.jpx.geom;

import static java.lang.Math.nextDown;
import static java.lang.Math.nextUp;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MathUtilsTest {

	@Test(dataProvider = "equalValues")
	public void equals(
		final double x,
		final double y,
		final int ulps,
		final boolean expected
	) {
		Assert.assertEquals(MathUtils.equal(x, y, ulps), expected);
	}

	@DataProvider
	public Object[][] equalValues() {
		return new Object[][] {
			{123.123, 123.123, 1, true},
			{123.123, nextUp(123.123), 1, true},
			{123.123, nextUp(nextUp(123.123)), 1, false},
			{123.123, nextDown(123.123), 1, true},
			{123.123, nextDown(nextDown(123.123)), 1, false},

			{-123.123, -123.123, 1, true},
			{-123.123, nextUp(-123.123), 1, true},
			{-123.123, nextUp(nextUp(-123.123)), 1, false},
			{-123.123, nextDown(-123.123), 1, true},
			{-123.123, nextDown(nextDown(-123.123)), 1, false},

			{0.0, 0.0, 1, true},
			{0.0, nextUp(0.0), 1, true},
			{0.0, nextUp(nextUp(0.0)), 1, false},
			{0.0, nextDown(0.0), 1, true},
			{0.0, nextDown(nextDown(0.0)), 1, false},

			{1.0, 1.0, 1, true},
			{1.0, nextUp(1.0), 1, true},
			{1.0, nextUp(nextUp(1.0)), 1, false},
			{1.0, nextDown(1.0), 1, true},
			{1.0, nextDown(nextDown(1.0)), 1, false},

			{1.0, -1.0, 1, false},
		};
	}

}
