package io.jenetics.jpx.format;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static io.jenetics.jpx.format.LocationFormatter.*;
import static org.testng.AssertJUnit.assertEquals;

/** Tests about patterns: validations, deprecations, ... */
public class PatternTest extends Fixture {

	@DataProvider public Object[] illegalPatterns(){
		return new String[]{
			"D D", "M M", "S S", "d d", "m m", "s s", "E E", // repetitions
			"+DX", "+dx", // double sign
			"D.D M", "D.D M S", "d.d m", "d.d m s", // fractional degrees
			"M", "M S", "S", "m", "m s", "s", // missing larger field
			"D M.MM S", "d m.mm s" // fractional minutes
		};
	}

	@Test(dataProvider = "illegalPatterns", expectedExceptions = {IllegalArgumentException.class})
	public void testIllegalPatterns(String pattern){ f = ofPattern(pattern); }

	@Test public void deprecatedL(){
		f = ofPattern("+LL.LLL");
		assertEquals("+DD.DDD", f.toPattern() );
	}

	@Test public void deprecatedl(){
		f = ofPattern("+ll.lll");
		assertEquals("+dd.ddd", f.toPattern());
	}

	@Test public void deprecatedH(){
		f = ofPattern("+H.HH");
		assertEquals("+E.EE", f.toPattern());
	}

	@Test(dataProvider = "patterns") public void parse(final String pattern) {
		f = ofPattern(pattern);
		String actual = f.toPattern();
		assertEquals(pattern, actual);
	}

	@DataProvider public Object[][] patterns() {
		final List<String> patterns = Arrays.asList(
			ISO_ELE_LONG.toPattern(),
			ISO_ELE_MEDIUM.toPattern(),
			ISO_ELE_SHORT.toPattern(),
			ISO_HUMAN_ELE_LONG.toPattern(),
			ISO_HUMAN_LAT_LONG.toPattern(),
			ISO_HUMAN_LONG.toPattern(),
			ISO_HUMAN_LON_LONG.toPattern(),
			ISO_LAT_LONG.toPattern(),
			ISO_LAT_MEDIUM.toPattern(),
			ISO_LAT_SHORT.toPattern(),
			ISO_LONG.toPattern(),
			ISO_LON_LONG.toPattern(),
			ISO_LON_MEDIUM.toPattern(),
			ISO_LON_SHORT.toPattern(),
			ISO_MEDIUM.toPattern(),
			ISO_SHORT.toPattern(),
			".DDf",
			"DD[gg]",
			"DD[g''g]"
		);

		return patterns.stream()
			.map(f -> new Object[]{f})
			.toArray(Object[][]::new);
	}

}
