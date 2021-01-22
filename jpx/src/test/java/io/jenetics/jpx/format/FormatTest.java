package io.jenetics.jpx.format;

import static org.testng.Assert.assertEquals;
import static io.jenetics.jpx.format.LocationFormatter.ofPattern;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class FormatTest extends Fixture {

	@DataProvider public Object[][] data() {
		return new Object[][]{
			// testing D
			{"D", latitude(-90), "-90"},
			{"D", latitude(90), "90"},
			{"DX", latitude(1), "1N"},
			{"DX", latitude(-1), "1S"},
			{"+D", latitude(1), "+1"},
			{"+D", latitude(-1), "-1"},
			{"D.DD", latitude(1.234), "1.23"},
			{"D MM", latitude(0, 54), "0 54"},
			{"D MM SS", latitude(0,59, 24), "0 59 24"},
			{"D", latitude(0.9), "1"},
			{"D", latitude(1), "1"},
			{"D", latitude(12), "12"},
			{"DD", latitude(1), "01"},
			{"DD", latitude(12), "12"},
			{"D", latitude(-1), "-1"},
			{"D", latitude(-12), "-12"},
			{"DD", latitude(-1), "-01"},
			{"DD", latitude(-12), "-12"},
			// testing M
			{"D M.MM", latitude(0.099), "0 5.94"}, // fractional minutes
			{"D M S.SS", latitude(0.099), "0 5 56.40"}, // truncates minutes and shows seconds
			{"D M", latitude(0.099), "0 6"}, // rounds minutes up
			{"D M", latitude(0, 12), "0 12"}, // lax width
			{"D M", latitude(0, 6), "0 6"},
			{"D MM", latitude(0, 6), "0 06"}, // strict width
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
			{"d.dd", longitude(1.234), "1.23"},
			{"d mm", longitude(0, 54), "0 54"},
			{"d mm ss", longitude(0,59, 24), "0 59 24"},
			{"d", longitude(0.9), "1"},
			{"d", longitude(1), "1"},
			{"d", longitude(12), "12"},
			{"d", longitude(123), "123"},
			{"dd", longitude(1), "01"},
			{"dd", longitude(12), "12"},
			{"dd", longitude(123), "123"},
			{"ddd", longitude(1), "001"},
			{"ddd", longitude(12), "012"},
			{"ddd", longitude(123), "123"},
			{"d", longitude(-1), "-1"},
			{"d", longitude(-12), "-12"},
			{"d", longitude(-123), "-123"},
			{"dd", longitude(-1), "-01"}, // bad
			{"dd", longitude(-12), "-12"}, // bad
			{"dd", longitude(-123), "-123"}, // bad
			{"ddd", longitude(-1), "-001"}, // bad
			{"ddd", longitude(-12), "-012"}, // bad
			{"ddd", longitude(-123), "-123"}, //bad
			// testing m
			{"d m.mm", longitude(0.099), "0 5.94"}, // fractional minutes
			{"d m s.ss", longitude(0.099), "0 5 56.40"}, // truncates minutes and shows seconds
			{"d m", longitude(0.099), "0 6"}, // rounds minutes up
			{"d m", longitude(0, 12), "0 12"}, // lax width
			{"d m", longitude(0, 6), "0 6"},
			{"d mm", longitude(0, 6), "0 06"}, // strict width
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
			{"EE", elevation(100), "100"},
			{"EE", elevation(-1), "-01"}, // bad
			{"EE", elevation(-10), "-10"}, // bad
			{"EE", elevation(-100), "-100"}, // bad
			{"E.EE", elevation(0), "0.00"},
			{"E.EE", elevation(0.1), "0.10"},
			{"E.EE", elevation(0.01), "0.01"},
			{"+E.EE'm'", elevation(1.23), "+1.23m"},

			// strange patterns
			{"+[+'A'+]+", latitude(1), "++A++"}
		};
	}

	@Test(dataProvider = "data")
	public void testFormat(
		final String pattern,
		final Location location,
		final String expected
	) {
		f = ofPattern(pattern);
		String actual = f.format(location);
		String message = location.toString();
		assertEquals(actual, expected, pattern + " " + message);
	}

}
