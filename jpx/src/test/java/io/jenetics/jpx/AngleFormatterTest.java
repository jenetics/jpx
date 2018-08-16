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
package io.jenetics.jpx;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class AngleFormatterTest {

	// -7.287954696138044 07°17'17"S 07°17'S -07.28795
	// +88.918540267041150	88°55'07
	@Test
	public void format() {
		//final double d = -7.287954696138044;
		final double d = 88.918540267041150;

		System.out.println(AngleFormatter.ofDegrees("#00").format(d));
		System.out.println(AngleFormatter.ofMinutes("#00").format(d));
		System.out.println(AngleFormatter.ofSeconds("#00.000").format(d));
	}

}
