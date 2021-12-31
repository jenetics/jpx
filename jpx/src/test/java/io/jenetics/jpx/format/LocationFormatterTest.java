/*
 * Java GPX Library (@__identifier__@).
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

import static org.testng.Assert.assertEquals;
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

import java.util.Random;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LocationFormatterTest extends Fixture {

	@Test(dataProvider = "formats")
	public void format(
		final LocationFormatter formatter,
		final Location location,
		final String expected
	) {
		final String actual = formatter.format(location);
		assertEquals(actual, expected);
	}

	@DataProvider
	public Object[][] formats() {
		return new Object[][] {
			{ISO_HUMAN_LAT_LONG, latitude(23.987635), "23°59'15.486\"N"},
			{ISO_HUMAN_LAT_LONG, latitude(-65.234275), "65°14'03.390\"S"},
			{ISO_HUMAN_LON_LONG, longitude(23.987635), "23°59'15.486\"E"},
			{ISO_HUMAN_LON_LONG, longitude(-65.234275), "65°14'03.390\"W"},
			{ISO_HUMAN_ELE_LONG, elevation(23.987635), "23.99m"},
			{ISO_HUMAN_ELE_LONG, elevation(-65.234275), "-65.23m"},
			{ISO_HUMAN_LONG, location(23.987635, -65.234275,-65.234275), "23°59'15.486\"N 65°14'03.390\"W -65.23m"},

			{ISO_LAT_SHORT, latitude(23.987635), "+23.99"},
			{ISO_LAT_SHORT, latitude(-65.234275), "-65.23"},
			{ISO_LON_SHORT, longitude(23.987635), "+023.99"},
			{ISO_LON_SHORT, longitude(-65.234275), "-065.23"},
			{ISO_ELE_SHORT, elevation(23.987635), "+24CRS"},
			{ISO_ELE_SHORT, elevation(-65.234275), "-65CRS"},
			{ISO_SHORT, location(23.987635,-65.234275,-65.234275), "+23.99-065.23-65CRS"},

			{ISO_LAT_MEDIUM, latitude(23.987635), "+2359.258"},
			{ISO_LAT_MEDIUM, latitude(-65.234275), "-6514.056"},
			{ISO_LON_MEDIUM, longitude(23.987635), "+02359.258"},
			{ISO_LON_MEDIUM, longitude(-65.234275), "-06514.056"},
			{ISO_ELE_MEDIUM, elevation(23.987635), "+24.0CRS"},
			{ISO_ELE_MEDIUM, elevation(-65.234275), "-65.2CRS"},
			{ISO_MEDIUM, location(23.987635,-65.234275,-65.234275), "+2359.258-06514.056-65.2CRS"},

			{ISO_LAT_LONG, latitude(23.987635), "+235915.49"},
			{ISO_LAT_LONG, latitude(-65.234275), "-651403.39"},
			{ISO_LON_LONG, longitude(23.987635), "+0235915.49"},
			{ISO_LON_LONG, longitude(-65.234275), "-0651403.39"},
			{ISO_ELE_LONG, elevation(23.987635), "+23.99CRS"},
			{ISO_ELE_LONG, elevation(-65.234275), "-65.23CRS"},
			{ISO_LONG, location(23.987635,-65.234275,-65.234275), "+235915.49-0651403.39-65.23CRS"},

			{ ofPattern("DD°MMSS dd°mmss"), location(23.987635,-65.234275,-65.234275), "23°5915 -65°1403"}, // bad
			{ ofPattern("LL[g''g]"), latitude(23.987635), "24g'g"},
			{ ofPattern("+LL[g''g]"), latitude(23.987635), "+24g'g"},
			//{ ofPattern("+L+L[g''g]"), Location.of(Latitude.ofDegrees(23.987635)), "+24+24g'g"}, // double L
			{ ofPattern("+++LL[g''g]"), latitude(23.987635), "+++24g'g"},
			{ ofPattern("+++LL[g''g]++"), latitude(23.987635), "+++24g'g++"}
		};
	}

	@Test(dataProvider = "formatters")
	public void formatAndParse(final LocationFormatter formatter) {
		final Random random = new Random(123);
		final Location location = LocationRandom.nextLocation(random);

		// Must be possible to safely parse previously formatted locations.
		final String formatted = formatter.format(location);
		final Location parsed = formatter.parse(formatted);

		final String formatted2 = formatter.format(parsed);
		Assert.assertEquals(formatted2, formatted);

		final Location parsed2 = formatter.parse(formatted2);
		Assert.assertEquals(parsed2, parsed);
	}

	@DataProvider
	public Object[][] formatters() {
		return new Object[][] {
			{ISO_HUMAN_LAT_LONG},
			{ISO_HUMAN_LAT_LONG},
			{ISO_HUMAN_LON_LONG},
			{ISO_HUMAN_LON_LONG},
			{ISO_HUMAN_ELE_LONG},
			{ISO_HUMAN_ELE_LONG},
			{ISO_HUMAN_LONG},

			{ISO_LAT_SHORT},
			{ISO_LAT_SHORT},
			{ISO_LON_SHORT},
			{ISO_LON_SHORT},
			{ISO_ELE_SHORT},
			{ISO_ELE_SHORT},
			{ISO_SHORT},

			{ISO_LAT_MEDIUM},
			{ISO_LAT_MEDIUM},
			{ISO_LON_MEDIUM},
			{ISO_LON_MEDIUM},
			{ISO_ELE_MEDIUM},
			{ISO_ELE_MEDIUM},
			{ISO_MEDIUM},

			{ISO_LAT_LONG},
			{ISO_LAT_LONG},
			{ISO_LON_LONG},
			{ISO_LON_LONG},
			{ISO_ELE_LONG},
			{ISO_ELE_LONG},
			{ISO_LONG}
		};
	}

	@Test
	public void parallelFormatting() {
		final var location = LocationRandom.nextLocation(new Random(123));
		final var formatter = ISO_HUMAN_LONG;
		final var expected = formatter.format(location);

		IntStream.range(0, 100_000).parallel()
			.mapToObj(i -> formatter.format(location))
			.forEach(result -> assertEquals(result, expected));
	}

	@Test
	public void parallelParsing() {
		final var expected = LocationRandom.nextLocation(new Random(123));
		final var formatter = ISO_HUMAN_LONG;
		final var formatted = formatter.format(expected);

		IntStream.range(0, 100_000).parallel()
			.mapToObj(i -> formatter.parse(formatted))
			.forEach(result -> assertEquals(result, expected));
	}

}
