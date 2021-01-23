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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.jpx.Latitude;

public class ParseTest extends Fixture {

	@DataProvider
	public Object[][] data() {
		return new Object[][]{
			// testing D
			{"D", latitude(-90), "-90"},
			{"D", latitude(90), "90"},
			{"DX", latitude(1), "1N"},
			{"DX", latitude(-1), "1S"},
			{"+D", latitude(1), "+1"},
			{"+D", latitude(-1), "-1"},
			{"D.DD", latitude(1.23), "1.23"},
			{"D MM", latitude(0.9), "0 54"},
			{"D MM SS", latitude(0.99), "0 59 24"},
			{"D", latitude(1), "1"},
			{"D", latitude(12), "12"}, // lax width
			{"DD", latitude(1), "01"}, // strict width
			{"DD", latitude(12), "12"},
			{"D", latitude(-1), "-1"}, // lax width
			{"D", latitude(-12), "-12"}, // lax width
			//{"DD", latitude(-1), "-01"}, // not used all input
			//{"DD", latitude(-12), "-12"}, // not used all input
			{"+DD", latitude(-1), "-01"},
			{"+DD", latitude(-12), "-12"},
			// testing M
			{"D M", latitude(0, 6), "0 6"},
			{"D M.MM", latitude(0, 5, 42), "0 5.70"}, // fractional minutes

			{"+DDM.MMM", latitude(-18, 36, 0), "-1836.000"}, // fractional minutes
			{"+DDM.MMM", Location.of(Latitude.ofDegrees(-18.6)), "-1836.000"}, // fractional minutes

			{"D M S.SS", latitude(0, 5, 42), "0 5 42.00"}, // minutes and seconds
			{"D M", latitude(0,6), "0 6"},
			{"D M", latitude(0, 12), "0 12"}, // lax width
			{"D MM", latitude(0,6), "0 06"}, // strict width
			{"D MM", latitude(0,12), "0 12"}, // strict width
			// testing S
			{"D M S", latitude(0,0,1), "0 0 1"},
			{"D M S", latitude(0,0, 10), "0 0 10"}, // lax width
			{"D M SS", latitude(0,0,1), "0 0 01"}, // strict width
			{"D M SS", latitude(0,0, 10), "0 0 10"}, // strict width
			{"D M S.SS", latitude(0,0,0.01), "0 0 0.01"}, // fractional seconds

			// testing d
			{"d", longitude(-180), "-180"},
			{"d", longitude(180), "180"},
			{"dx", longitude(1), "1E"},
			{"dx", longitude(-1), "1W"},
			{"+d", longitude(1), "+1"},
			{"+d", longitude(-1), "-1"},
			{"d.dd", longitude(1.23), "1.23"},
			{"d mm", longitude(0.9), "0 54"},
			{"d mm ss", longitude(0.99), "0 59 24"},
			{"d", longitude(1), "1"},
			{"d", longitude(12), "12"}, // lax width
			{"d", longitude(123), "123"}, // lax width
			{"dd", longitude(1), "01"}, // strict width
			{"dd", longitude(12), "12"},
			//{"dd", longitude(123), "123"}, // not used all input
			{"ddd", longitude(1), "001"},
			{"ddd", longitude(12), "012"},
			{"ddd", longitude(123), "123"},
			{"d", longitude(-1), "-1"}, // lax width
			{"d", longitude(-12), "-12"}, // lax width
			{"d", longitude(-123), "-123"}, // lax width
			//{"dd", longitude(-1), "-01"}, // not used all input
			//{"dd", longitude(-12), "-12"}, // not used all input
			//{"dd", longitude(-123), "-123"}, // not used all input
			{"+dd", longitude(-1), "-01"},
			{"+dd", longitude(-12), "-12"},
			//{"+dd", longitude(-123), "-123"}, // not used all input
			//{"ddd", longitude(-1), "-001"}, // not used all input
			//{"ddd", longitude(-12), "-012"}, // not used all input
			//{"ddd", longitude(-123), "-123"}, // not used all input
			{"+ddd", longitude(-1), "-001"},
			{"+ddd", longitude(-12), "-012"},
			{"+ddd", longitude(-123), "-123"},
			// testing m
			{"d m", longitude(0, 6), "0 6"},
			{"d m.mm", longitude(0, 5, 42), "0 5.70"}, // fractional minutes
			{"d m s.ss", longitude(0, 5, 42), "0 5 42.00"}, // minutes and seconds
			{"d m", longitude(0,6), "0 6"},
			{"d m", longitude(0, 12), "0 12"}, // lax width
			{"d mm", longitude(0,6), "0 06"}, // strict width
			{"d mm", longitude(0,12), "0 12"}, // strict width
			// testing s
			{"d m s", longitude(0,0,1), "0 0 1"},
			{"d m s", longitude(0,0, 10), "0 0 10"}, // lax width
			{"d m ss", longitude(0,0,1), "0 0 01"}, // strict width
			{"d m ss", longitude(0,0, 10), "0 0 10"}, // strict width
			{"d m s.ss", longitude(0,0,0.01), "0 0 0.01"}, // fractional seconds

			// testing E
			{"E", elevation(0), "0"},
			{"+E", elevation(0), "+0"},
			{"E", elevation(1), "1"},
			{"+E", elevation(1), "+1"},
			{"E", elevation(-1), "-1"},
			{"E", elevation(10), "10"},
			{"E", elevation(100), "100"},
			{"EE", elevation(1), "01"},
			{"EE", elevation(10), "10"},
			//{"EE", elevation(100), "100"}, // not used all input
			//{"EE", elevation(-1), "-01"}, // not used all input
			//{"EE", elevation(-10), "-10"}, // not used all input
			//{"EE", elevation(-100), "-100"}, // not used all input
			{"E.EE", elevation(0), "0.00"},
			{"E.EE", elevation(0.1), "0.10"},
			{"E.EE", elevation(0.01), "0.01"},
			{"+E.EE'm'", elevation(1.23), "+1.23m"},

			// strange patterns
			{"+[+'A'+]+", Location.of(null, null), "++A++"}
		};
	}

	@Test(dataProvider = "data")
	public void testParse(String pattern, Location expected, String in) {
		_formatter = ofPattern(pattern);
		Location actual = _formatter.parse(in);
		assertEquals(actual, expected, "[" + pattern + "] " + in);
	}

}
