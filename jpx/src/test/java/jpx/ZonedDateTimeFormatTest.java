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

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class ZonedDateTimeFormatTest {

	@Test(dataProvider = "validExamples")
	public void parseExample(final String example) {
		final ZonedDateTimeFormat format = ZonedDateTimeFormat
			.findFormat(example)
			.orElseThrow(NoSuchElementException::new);

		final ZonedDateTime zdt = format.formatParse(example);
		final String zdts = ZonedDateTimeFormat.format(zdt);
		Assert.assertEquals(
			ZonedDateTimeFormat
				.parseOptional(zdts)
				.map(ZonedDateTime::toInstant)
				.orElse(Instant.MIN),
			zdt.toInstant()
		);
	}

	@DataProvider(name = "validExamples")
	public Object[][] validExamples() {
		return new Object[][] {
			{"2001-10-26T21:32:52"},
			{"2001-10-26T21:32:52.1"},
			{"2001-10-26T21:32:52.12"},
			{"2001-10-26T21:32:52.123"},
			{"2001-10-26T21:32:52.1234"},
			{"2001-10-26T21:32:52.12345"},
			{"2001-10-26T21:32:52.1234567"},
			{"2001-10-26T21:32:52.12345678"},
			{"2001-10-26T21:32:52.123456789"},

			{"2001-10-26T21:32:52Z"},
			{"2001-10-26T21:32:52.1Z"},
			{"2001-10-26T21:32:52.12Z"},
			{"2001-10-26T21:32:52.123Z"},
			{"2001-10-26T21:32:52.1234Z"},
			{"2001-10-26T21:32:52.12345Z"},
			{"2001-10-26T21:32:52.1234567Z"},
			{"2001-10-26T21:32:52.12345678Z"},
			{"2001-10-26T21:32:52.123456789Z"},

			{"2001-10-26T19:32:52-01:00"},
			{"2001-10-26T19:32:52.1-01:00"},
			{"2001-10-26T19:32:52.12-01:00"},
			{"2001-10-26T19:32:52.123-01:00"},
			{"2001-10-26T19:32:52.1234-01:00"},
			{"2001-10-26T19:32:52.12345-01:00"},
			{"2001-10-26T19:32:52.123456-01:00"},
			{"2001-10-26T19:32:52.1234567-01:00"},
			{"2001-10-26T19:32:52.12345678-01:00"},
			{"2001-10-26T19:32:52.123456789-01:00"},

			{"2001-10-26T19:32:52+00:00"},
			{"2001-10-26T19:32:52.1+00:00"},
			{"2001-10-26T19:32:52.12+00:00"},
			{"2001-10-26T19:32:52.123+00:00"},
			{"2001-10-26T19:32:52.1234+00:00"},
			{"2001-10-26T19:32:52.12345+00:00"},
			{"2001-10-26T19:32:52.123456+00:00"},
			{"2001-10-26T19:32:52.1234567+00:00"},
			{"2001-10-26T19:32:52.12345678+00:00"},
			{"2001-10-26T19:32:52.123456789+00:00"},

			{"2001-10-26T19:32:52+05:00"},
			{"2001-10-26T19:32:52.1+05:00"},
			{"2001-10-26T19:32:52.12+05:00"},
			{"2001-10-26T19:32:52.123+05:00"},
			{"2001-10-26T19:32:52.1234+05:00"},
			{"2001-10-26T19:32:52.12345+05:00"},
			{"2001-10-26T19:32:52.123456+05:00"},
			{"2001-10-26T19:32:52.1234567+05:00"},
			{"2001-10-26T19:32:52.12345678+05:00"},
			{"2001-10-26T19:32:52.123456789+05:00"}
		};
	}

}
