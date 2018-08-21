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
package io.jenetics.jpx.format;

import java.util.Random;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LocationFieldFormatTest {

	//@Test
	public void format() {
		final Random random = new Random(123);

		final Location loc = Location.of(
			LocationRandom.nextLatitude(random),
			null,
			null
		);

		final LocationFieldFormat df = LocationFieldFormat.ofPattern("DD");
		final LocationFieldFormat mf = LocationFieldFormat.ofPattern("MM");
		final LocationFieldFormat sf = LocationFieldFormat.ofPattern("SS.SSS");

		System.out.println(loc.latitude());
		System.out.println(df.format(loc) + "°" + mf.format(loc) + "'" + sf.format(loc) + "\"");
	}

}
