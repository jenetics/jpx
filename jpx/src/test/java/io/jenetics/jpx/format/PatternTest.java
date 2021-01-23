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

import static org.testng.AssertJUnit.assertEquals;
import static io.jenetics.jpx.format.LocationFormatter.ISO_ELE_LONG;
import static io.jenetics.jpx.format.LocationFormatter.ISO_ELE_MEDIUM;
import static io.jenetics.jpx.format.LocationFormatter.ISO_ELE_SHORT;
import static io.jenetics.jpx.format.LocationFormatter.ISO_HUMAN_ELE_LONG;
import static io.jenetics.jpx.format.LocationFormatter.ISO_HUMAN_LAT_LONG;
import static io.jenetics.jpx.format.LocationFormatter.ISO_HUMAN_LONG;
import static io.jenetics.jpx.format.LocationFormatter.ISO_HUMAN_LON_LONG;
import static io.jenetics.jpx.format.LocationFormatter.ISO_LAT_LONG;
import static io.jenetics.jpx.format.LocationFormatter.ISO_LAT_MEDIUM;
import static io.jenetics.jpx.format.LocationFormatter.ISO_LAT_SHORT;
import static io.jenetics.jpx.format.LocationFormatter.ISO_LONG;
import static io.jenetics.jpx.format.LocationFormatter.ISO_LON_LONG;
import static io.jenetics.jpx.format.LocationFormatter.ISO_LON_MEDIUM;
import static io.jenetics.jpx.format.LocationFormatter.ISO_LON_SHORT;
import static io.jenetics.jpx.format.LocationFormatter.ISO_MEDIUM;
import static io.jenetics.jpx.format.LocationFormatter.ISO_SHORT;
import static io.jenetics.jpx.format.LocationFormatter.ofPattern;

import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests about patterns: validations, deprecations, ...
 */
public class PatternTest extends Fixture {

	@DataProvider
	public Object[] illegalPatterns() {
		return new String[]{
			"D D", "M M", "S S", "d d", "m m", "s s", "E E", // repetitions
			"+DX", "+dx", // double sign
			"D.D M", "D.D M S", "d.d m", "d.d m s", // fractional degrees
			"M", "M S", "S", "m", "m s", "s", // missing larger field
			"D M.MM S", "d m.mm s" // fractional minutes
		};
	}

	@Test(dataProvider = "illegalPatterns", expectedExceptions = {IllegalArgumentException.class})
	public void testIllegalPatterns(String pattern) {
		f = ofPattern(pattern);
	}

	@Test
	public void deprecatedL() {
		f = ofPattern("+LL.LLL");
		assertEquals("+DD.DDD", f.toPattern());
	}

	@Test
	public void deprecatedl() {
		f = ofPattern("+ll.lll");
		assertEquals("+dd.ddd", f.toPattern());
	}

	@Test
	public void deprecatedH() {
		f = ofPattern("+H.HH");
		assertEquals("+E.EE", f.toPattern());
	}

	@Test(dataProvider = "patterns")
	public void parse(final String pattern) {
		f = ofPattern(pattern);
		String actual = f.toPattern();
		assertEquals(pattern, actual);
	}

	@DataProvider
	public Object[][] patterns() {
		final List<String> patterns = List.of(
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
