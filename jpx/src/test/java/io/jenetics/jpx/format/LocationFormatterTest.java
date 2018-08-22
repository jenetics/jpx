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

import static io.jenetics.jpx.format.LocationFormatter.ISO_HUMAN_LONG;
import static io.jenetics.jpx.format.LocationFormatter.ISO_LAT_LONG;
import static io.jenetics.jpx.format.LocationFormatter.ISO_LAT_MEDIUM;
import static io.jenetics.jpx.format.LocationFormatter.ISO_LAT_SHORT;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Length.Unit;
import io.jenetics.jpx.Longitude;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LocationFormatterTest {

	private static final String BASE_DIR = "jpx/src/test/resources/io/jenetics/jpx/iso6709";

	// -7.287954696138044 07°17'17"S 07°17'S -07.28795
	//@Test
	public void parsing() {
		//LocationFormatter.ofPattern("DD°MM'SS.SSS\"[NS] dd°mm'ss.sss\"[EW]");
	}

	@Test
	public void format() {
		final Latitude latitude = Latitude.ofDegrees(16.44977221);
		final Longitude longitude = Longitude.ofDegrees(45.14937221);

		final LocationFormatter formatter = LocationFormatter.ISO_HUMAN_LONG;

		System.out.println(formatter.format(Location.of(latitude, longitude)));
		System.out.println(formatter.format(Location.of(latitude, longitude, Length.of(234.23, Unit.METER))));
		System.out.println(ISO_LAT_SHORT.format(Latitude.ofDegrees(-6.44977221)));
		System.out.println(ISO_LAT_MEDIUM.format(Latitude.ofDegrees(-6.44977221)));
		System.out.println(ISO_LAT_LONG.format(Latitude.ofDegrees(-6.44977221)));
		System.out.println(ISO_HUMAN_LONG.toPattern());
	}

	//@Test(dataProvider = "latitudes")
	public void latitudesISOHumanLong(final String[] row) {
		final double degrees = Double.parseDouble(row[0]);
		final Latitude latitude = Latitude.ofDegrees(degrees);

		final String format = row[1];
		Assert.assertEquals(
			LocationFormatter.ISO_HUMAN_LAT_LONG.format(Location.of(latitude)),
			format
		);
	}

//	@Test(dataProvider = "latitudes")
//	public void latitudesISOHumanMedium(final String[] row) {
//		final double degrees = Double.parseDouble(row[0]);
//		final Latitude latitude = Latitude.ofDegrees(degrees);
//
//		final String format = row[2];
//		Assert.assertEquals(
//			LocationFormatter.ISO_HUMAN_MEDIUM.format(latitude),
//			format
//		);
//	}
//
//	@Test(dataProvider = "latitudes")
//	public void latitudesISODecimal(final String[] row) {
//		final double degrees = Double.parseDouble(row[0]);
//		final Latitude latitude = Latitude.ofDegrees(degrees);
//
//		final String format = row[3];
//		Assert.assertEquals(
//			LocationFormatter.ISO_DECIMAL.format(latitude),
//			format
//		);
//	}
//
//	@Test(dataProvider = "latitudes")
//	public void latitudesISOLong(final String[] row) {
//		final double degrees = Double.parseDouble(row[0]);
//		final Latitude latitude = Latitude.ofDegrees(degrees);
//
//		final String format = row[4];
//		Assert.assertEquals(
//			LocationFormatter.ISO_LONG.format(latitude),
//			format
//		);
//	}
//
//	@Test(dataProvider = "latitudes")
//	public void latitudesISOMedium(final String[] row) {
//		final double degrees = Double.parseDouble(row[0]);
//		final Latitude latitude = Latitude.ofDegrees(degrees);
//
//		final String format = row[5];
//		Assert.assertEquals(
//			LocationFormatter.ISO_MEDIUM.format(latitude),
//			format
//		);
//	}
//
//	@Test(dataProvider = "latitudes")
//	public void latitudesISOShort(final String[] row) {
//		final double degrees = Double.parseDouble(row[0]);
//		final Latitude latitude = Latitude.ofDegrees(degrees);
//
//		final String format = row[6];
//		Assert.assertEquals(
//			LocationFormatter.ISO_SHORT.format(latitude),
//			format
//		);
//	}

	@DataProvider
	public Iterator<Object[]> latitudes() throws IOException {
		final File file  = new File("" +
			"src/test/resources/io/jenetics/jpx/iso6709",
			"latitudes.csv"
		);
		final List<String> lines = Files.readAllLines(file.toPath());

		return lines.stream()
			.map(line -> line.split("\t"))
			.map(row -> (Object[])row)
			.collect(Collectors.toList())
			.iterator();
	}
}
