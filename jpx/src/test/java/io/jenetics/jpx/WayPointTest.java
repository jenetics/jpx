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
package jpx;

import static java.lang.String.format;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
@Test
public class WayPointTest extends XMLStreamTestBase<WayPoint> {

	@Override
	protected Params<WayPoint> params(final Random random) {
		return new Params<>(
			() -> nextWayPoint(random),
			WayPoint.reader("wpt"),
			(p, writer) -> p.write("wpt", writer)
		);
	}

	public static WayPoint nextWayPoint(final Random random) {
		return WayPoint.builder()
			.ele(random.nextBoolean() ? Length.ofMeters(random.nextInt(1000)) : null)
			.speed(random.nextBoolean() ? Speed.of(random.nextDouble()*100) : null)
			.time(random.nextBoolean() ? ZonedDateTime.now() : null)
			.magvar(random.nextBoolean() ? Degrees.ofDegrees(random.nextDouble()*10) : null)
			.geoidheight(random.nextBoolean() ? Length.ofMeters(random.nextInt(1000)) : null)
			.name(random.nextBoolean() ? format("name_%s", random.nextInt(100)) : null)
			.cmt(random.nextBoolean() ? format("comment_%s", random.nextInt(100)) : null)
			.desc(random.nextBoolean() ? format("description_%s", random.nextInt(100)) : null)
			.src(random.nextBoolean() ? format("source_%s", random.nextInt(100)) : null)
			.links(LinkTest.nextLinks(random))
			.sym(random.nextBoolean() ? format("symbol_%s", random.nextInt(100)) : null)
			.type(random.nextBoolean() ? format("type_%s", random.nextInt(100)) : null)
			.fix(random.nextBoolean() ? Fix.values()[random.nextInt(Fix.values().length)] : null)
			.sat(random.nextBoolean() ? UInt.of(random.nextInt(100)) : null)
			.hdop(random.nextBoolean() ? random.nextDouble() + 2: null)
			.vdop(random.nextBoolean() ? random.nextDouble() + 2: null)
			.pdop(random.nextBoolean() ? random.nextDouble() + 2: null)
			.ageofdgpsdata(random.nextBoolean() ? Duration.ofSeconds(random.nextInt(1000)) : null)
			.dgpsid(random.nextBoolean() ? DGPSStation.of(random.nextInt(100)) : null)
			.build(48 + random.nextDouble()*2, 16 + random.nextDouble()*2);
	}

	public static List<WayPoint> nextWayPoints(final Random random) {
		final List<WayPoint> points = new ArrayList<>();
		for (int i = 0, n = random.nextInt(20); i < n; ++i) {
			points.add(nextWayPoint(random));
		}
		return points;
	}

}
