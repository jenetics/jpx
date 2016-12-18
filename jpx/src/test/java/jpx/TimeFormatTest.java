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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package jpx;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class TimeFormatTest {

	@Test(dataProvider = "formats")
	public void format(final TimeFormat format) {
		final ZonedDateTime time = ZonedDateTime.now();
		System.out.println(format.format(time));
	}

	//@Test(dataProvider = "formats")
	public void parse(final TimeFormat format) {
		final ZonedDateTime time = ZonedDateTime.now();
		final String string = format.format(time);

		format.parse(string);
	}

	@DataProvider(name = "formats")
	public Object[][] formats() {
		return Stream.of(TimeFormat.values())
			.map(format -> new Object[]{format})
			.toArray(Object[][]::new);
	}

}
