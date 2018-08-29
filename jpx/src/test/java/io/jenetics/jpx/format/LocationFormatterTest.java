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

import static java.util.Arrays.asList;
import static io.jenetics.jpx.Length.Unit.METER;
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

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LocationFormatterTest {

	@Test(dataProvider = "formats")
	public void format(
		final LocationFormatter formatter,
		final Location location,
		final String format
	) {
		DateTimeFormatter b;
		Assert.assertEquals(formatter.format(location), format);
	}

	@DataProvider
	public Object[][] formats() {
		return new Object[][] {
			{ISO_HUMAN_LAT_LONG, Location.of(Latitude.ofDegrees(23.987635)), "24°59'15.486\"N"},
			{ISO_HUMAN_LAT_LONG, Location.of(Latitude.ofDegrees(-65.234275)), "65°14'03.390\"S"},
			{ISO_HUMAN_LON_LONG, Location.of(Longitude.ofDegrees(23.987635)), "24°59'15.486\"E"},
			{ISO_HUMAN_LON_LONG, Location.of(Longitude.ofDegrees(-65.234275)), "65°14'03.390\"W"},
			{ISO_HUMAN_ELE_LONG, Location.of(Length.of(23.987635, METER)), "23.99m"},
			{ISO_HUMAN_ELE_LONG, Location.of(Length.of(-65.234275, METER)), "-65.23m"},
			{ISO_HUMAN_LONG, Location.of(
				Latitude.ofDegrees(23.987635),
				Longitude.ofDegrees(-65.234275),
				Length.of(-65.234275, METER)), "24°59'15.486\"N 65°14'03.390\"W -65.23m"},

			{ISO_LAT_SHORT, Location.of(Latitude.ofDegrees(23.987635)), "+23.99"},
			{ISO_LAT_SHORT, Location.of(Latitude.ofDegrees(-65.234275)), "-65.23"},
			{ISO_LON_SHORT, Location.of(Longitude.ofDegrees(23.987635)), "+023.99"},
			{ISO_LON_SHORT, Location.of(Longitude.ofDegrees(-65.234275)), "-065.23"},
			{ISO_ELE_SHORT, Location.of(Length.of(23.987635, METER)), "+24CRS"},
			{ISO_ELE_SHORT, Location.of(Length.of(-65.234275, METER)), "-65CRS"},
			{ISO_SHORT, Location.of(
				Latitude.ofDegrees(23.987635),
				Longitude.ofDegrees(-65.234275),
				Length.of(-65.234275, METER)), "+23.99-065.23-65CRS"},

			{ISO_LAT_MEDIUM, Location.of(Latitude.ofDegrees(23.987635)), "+2459.258"},
			{ISO_LAT_MEDIUM, Location.of(Latitude.ofDegrees(-65.234275)), "-6514.056"},
			{ISO_LON_MEDIUM, Location.of(Longitude.ofDegrees(23.987635)), "+02459.258"},
			{ISO_LON_MEDIUM, Location.of(Longitude.ofDegrees(-65.234275)), "-06514.056"},
			{ISO_ELE_MEDIUM, Location.of(Length.of(23.987635, METER)), "+24.0CRS"},
			{ISO_ELE_MEDIUM, Location.of(Length.of(-65.234275, METER)), "-65.2CRS"},
			{ISO_MEDIUM, Location.of(
				Latitude.ofDegrees(23.987635),
				Longitude.ofDegrees(-65.234275),
				Length.of(-65.234275, METER)), "+2459.258-06514.056-65.2CRS"},

			{ISO_LAT_LONG, Location.of(Latitude.ofDegrees(23.987635)), "+245915.49"},
			{ISO_LAT_LONG, Location.of(Latitude.ofDegrees(-65.234275)), "-651403.39"},
			{ISO_LON_LONG, Location.of(Longitude.ofDegrees(23.987635)), "+0245915.49"},
			{ISO_LON_LONG, Location.of(Longitude.ofDegrees(-65.234275)), "-0651403.39"},
			{ISO_ELE_LONG, Location.of(Length.of(23.987635, METER)), "+23.99CRS"},
			{ISO_ELE_LONG, Location.of(Length.of(-65.234275, METER)), "-65.23CRS"},
			{ISO_LONG, Location.of(
				Latitude.ofDegrees(23.987635),
				Longitude.ofDegrees(-65.234275),
				Length.of(-65.234275, METER)), "+245915.49-0651403.39-65.23CRS"}
		};
	}

	@Test
	public void pattern() {
		System.out.println(ISO_LONG.toPattern());
	}

	@Test(dataProvider = "tokens")
	public void tokenize(final String pattern, final List<String> tokens) {
		final List<String> t = LocationFormatter.Builder.tokenize(pattern);
		Assert.assertEquals(t, tokens, String.format("%s != %s", t, tokens));
	}

	@DataProvider
	public Object[][] tokens() {
		return new Object[][] {
			{"LL", asList("LL")},
			{".LL", asList(".LL")},
			{"LL''", asList("LL", "'", "'")},
			{"LL'''", asList("LL", "'", "'", "'")},
			{"LL.LLL", asList("LL.LLL")},
			{"LL,LLL", asList("LL,LLL")},
			{"LLDD", asList("LL", "DD")},
			{"LL.LDD", asList("LL.L", "DD")},
			{"LL.LDD.DDD", asList("LL.L", "DD.DDD")},
			{"LL.L123DD.DDD", asList("LL.L", "123", "DD.DDD")},
			{"LL.L123DD.DDD4567", asList("LL.L", "123", "DD.DDD", "4567")},
			{"+LL.LDD.DDD", asList("+", "LL.L", "DD.DDD")},
			{"+LL.LDD.DDDx", asList("+", "LL.L", "DD.DDD", "x")},
			{"+LL.LDD.DDD''x", asList("+", "LL.L", "DD.DDD", "'", "'", "x")},
			{"+LL.LDD.DDD'x'", asList("+", "LL.L", "DD.DDD", "'", "x", "'")},
			{"+LL.LDD.DDD[x]ss", asList("+", "LL.L", "DD.DDD", "[", "x", "]", "ss")},
			{"+LL.LDD.DDD[x]'ss", asList("+", "LL.L", "DD.DDD", "[", "x", "]", "'", "ss")},
			{"+DD.DD[SSS]'XXX'sss.smm", asList("+", "DD.DD", "[", "SSS", "]", "'", "XXX", "'", "sss.s", "mm")},
			{"+DD.DD[SSS]'XXXsss.smm", asList("+", "DD.DD", "[", "SSS", "]", "'", "XXXsss.smm")}
		};
	}

	@Test(dataProvider = "patterns")
	public void parse(final String pattern) {
		Assert.assertEquals(LocationFormatter.ofPattern(pattern).toPattern(), pattern);
	}

	@DataProvider
	public Object[][] patterns() {
		final List<LocationFormatter> formatters = Arrays.asList(
			ISO_ELE_LONG,
			ISO_ELE_MEDIUM,
			ISO_ELE_SHORT,
			ISO_HUMAN_ELE_LONG,
			ISO_HUMAN_LAT_LONG,
			ISO_HUMAN_LONG,
			ISO_HUMAN_LON_LONG,
			ISO_LAT_LONG,
			ISO_LAT_MEDIUM,
			ISO_LAT_SHORT,
			ISO_LONG,
			ISO_LON_LONG,
			ISO_LON_MEDIUM,
			ISO_LON_SHORT,
			ISO_MEDIUM,
			ISO_SHORT
		);

		return formatters.stream()
			.map(f -> new Object[]{f.toPattern()})
			.toArray(Object[][]::new);
	}

}
