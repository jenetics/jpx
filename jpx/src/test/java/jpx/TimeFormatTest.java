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
import java.util.Optional;
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

	@Test(dataProvider = "formats")
	public void parse(final TimeFormat format) {
		final ZonedDateTime time = ZonedDateTime.now();
		final String string = format.format(time);

		format.parse(string);
	}

	@Test(dataProvider = "validExamples")
	public void parseExample(final String example) {
		final Optional<TimeFormat> format = TimeFormat.findFormatFor(example);
		format.ifPresent(f -> {
			final ZonedDateTime time = f.parse(example);
			System.out.println(example + " -> " + f.format(time));
		});
		if (!format.isPresent()) {
			System.out.println(example + " -> ");
		}
	}

	@DataProvider(name = "formats")
	public Object[][] formats() {
		return Stream.of(TimeFormat.values())
			.map(format -> new Object[]{format})
			.toArray(Object[][]::new);
	}

	@DataProvider(name = "validExamples")
	public Object[][] validExamples() {
		return new Object[][] {
			{"2001-10-26T21:32:52"},
			{"2001-10-26T21:32:52+02:00"},
			{"2001-10-26T19:32:52Z"},
			{"2001-10-26T19:32:52+00:00"},
			{"-2001-10-26T21:32:52"},
			{"2001-10-26T21:32:52.12679"}

			/*
			{"2002-05-30T09:00:00"},
			{"2002-05-30T09:30:10.5"},
			{"2002-05-30T09:30:10.53"},
			{"2002-05-30T09:30:10.536"},
			{"2016-12-19T15:30:50.485+0100"},
			{"2016-12-19T15:30:50.485+01:00[Europe/Vienna]"}
			*/
		};
	}

}
