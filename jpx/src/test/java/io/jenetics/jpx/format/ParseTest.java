/*
 * Java GPX Library (@__identifier__@).
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
 */
package io.jenetics.jpx.format;

import static org.testng.Assert.assertEquals;
import static io.jenetics.jpx.format.LocationFormatter.ofPattern;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.jpx.Latitude;

public class ParseTest extends Fixture {

	@Test(dataProvider = "data")
	public void testParse(
		final String pattern,
		final Location expected,
		final String in,
		final boolean valid
	) {
		_formatter = ofPattern(pattern);

		if (valid) {
			Location actual = _formatter.parse(in);
			assertEquals(actual, expected, "[" + pattern + "] " + in);
		} else {
			Assert.assertThrows(
				ParseException.class,
				() -> _formatter.parse(in)
			);
		}
	}

	@DataProvider
	public Object[][] data() {
		return new Object[][]{
			// testing D
			{"D", latitude(-90), "-90", true},
			{"D", latitude(90), "90", true},
			{"DX", latitude(1), "1N", true},
			{"DX", latitude(-1), "1S", true},
			{"+D", latitude(1), "+1", true},
			{"+D", latitude(-1), "-1", true},
			{"D.DD", latitude(1.23), "1.23", true},
			{"D MM", latitude(0.9), "0 54", true},
			{"D MM SS", latitude(0.99), "0 59 24", true},
			{"D", latitude(1), "1", true},
			{"D", latitude(12), "12", true}, // lax width
			{"DD", latitude(1), "01", true}, // strict width
			{"DD", latitude(12), "12", true},
			{"D", latitude(-1), "-1", true}, // lax width
			{"D", latitude(-12), "-12", true}, // lax width
			{"DD", latitude(-1), "-01", false}, // not used all input
			{"DD", latitude(-12), "-12", false}, // not used all input
			{"+DD", latitude(-1), "-01", true},
			{"+DD", latitude(-12), "-12", true},
			// testing M
			{"D M", latitude(0, 6), "0 6", true},
			{"D M.MM", latitude(0, 5, 42), "0 5.70", true}, // fractional minutes

			{"+DDM.MMM", latitude(-18, 36, 0), "-1836.000", true}, // fractional minutes
			{"+DDM.MMM", Location.of(Latitude.ofDegrees(-18.6)), "-1836.000", true}, // fractional minutes

			{"D M S.SS", latitude(0, 5, 42), "0 5 42.00", true}, // minutes and seconds
			{"D M", latitude(0,6), "0 6", true},
			{"D M", latitude(0, 12), "0 12", true}, // lax width
			{"D MM", latitude(0,6), "0 06", true}, // strict width
			{"D MM", latitude(0,12), "0 12", true}, // strict width
			// testing S
			{"D M S", latitude(0,0,1), "0 0 1", true},
			{"D M S", latitude(0,0, 10), "0 0 10", true}, // lax width
			{"D M SS", latitude(0,0,1), "0 0 01", true}, // strict width
			{"D M SS", latitude(0,0, 10), "0 0 10", true}, // strict width
			{"D M S.SS", latitude(0,0,0.01), "0 0 0.01", true}, // fractional seconds

			// testing d
			{"d", longitude(-180), "-180", true},
			{"d", longitude(180), "180", true},
			{"dx", longitude(1), "1E", true},
			{"dx", longitude(-1), "1W", true},
			{"+d", longitude(1), "+1", true},
			{"+d", longitude(-1), "-1", true},
			{"d.dd", longitude(1.23), "1.23", true},
			{"d mm", longitude(0.9), "0 54", true},
			{"d mm ss", longitude(0.99), "0 59 24", true},
			{"d", longitude(1), "1", true},
			{"d", longitude(12), "12", true}, // lax width
			{"d", longitude(123), "123", true}, // lax width
			{"dd", longitude(1), "01", true}, // strict width
			{"dd", longitude(12), "12", true},
			{"dd", longitude(123), "123", false}, // not used all input
			{"ddd", longitude(1), "001", true},
			{"ddd", longitude(12), "012", true},
			{"ddd", longitude(123), "123", true},
			{"d", longitude(-1), "-1", true}, // lax width
			{"d", longitude(-12), "-12", true}, // lax width
			{"d", longitude(-123), "-123", true}, // lax width
			{"dd", longitude(-1), "-01", false}, // not used all input
			{"dd", longitude(-12), "-12", false}, // not used all input
			{"dd", longitude(-123), "-123", false}, // not used all input
			{"+dd", longitude(-1), "-01", true},
			{"+dd", longitude(-12), "-12", true},
			{"+dd", longitude(-123), "-123", false}, // not used all input
			{"ddd", longitude(-1), "-001", false}, // not used all input
			{"ddd", longitude(-12), "-012", false}, // not used all input
			{"ddd", longitude(-123), "-123", false}, // not used all input
			{"+ddd", longitude(-1), "-001", true},
			{"+ddd", longitude(-12), "-012", true},
			{"+ddd", longitude(-123), "-123", true},
			// testing m
			{"d m", longitude(0, 6), "0 6", true},
			{"d m.mm", longitude(0, 5, 42), "0 5.70", true}, // fractional minutes
			{"d m s.ss", longitude(0, 5, 42), "0 5 42.00", true}, // minutes and seconds
			{"d m", longitude(0,6), "0 6", true},
			{"d m", longitude(0, 12), "0 12", true}, // lax width
			{"d mm", longitude(0,6), "0 06", true}, // strict width
			{"d mm", longitude(0,12), "0 12", true}, // strict width
			// testing s
			{"d m s", longitude(0,0,1), "0 0 1", true},
			{"d m s", longitude(0,0, 10), "0 0 10", true}, // lax width
			{"d m ss", longitude(0,0,1), "0 0 01", true}, // strict width
			{"d m ss", longitude(0,0, 10), "0 0 10", true}, // strict width
			{"d m s.ss", longitude(0,0,0.01), "0 0 0.01", true}, // fractional seconds

			// testing E
			{"E", elevation(0), "0", true},
			{"+E", elevation(0), "+0", true},
			{"E", elevation(1), "1", true},
			{"+E", elevation(1), "+1", true},
			{"E", elevation(-1), "-1", true},
			{"E", elevation(10), "10", true},
			{"E", elevation(100), "100", true},
			{"EE", elevation(1), "01", true},
			{"EE", elevation(10), "10", true},
			{"EE", elevation(100), "100", false}, // not used all input
			{"EE", elevation(-1), "-01", false}, // not used all input
			{"EE", elevation(-10), "-10", false}, // not used all input
			{"EE", elevation(-100), "-100", false}, // not used all input
			{"E.EE", elevation(0), "0.00", true},
			{"E.EE", elevation(0.1), "0.10", true},
			{"E.EE", elevation(0.01), "0.01", true},
			{"+E.EE'm'", elevation(1.23), "+1.23m", true},

			// strange patterns
			{"+[+'A'+]+", Location.of(null, null), "++A++", true}
		};
	}

}
