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
package io.jenetics.jpx.geom;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

/**
 * Represents an earth ellipsoid, which is a mathematical figure approximating
 * the shape of the Earth, used as a reference frame for computations in
 * geodesy, astronomy and the geosciences. Various different ellipsoids have
 * been used as approximations.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Earth_ellipsoid">Earth ellipsoid</a>
 * @see Geoid
 *
 * @param name the name of the earth ellipsoid model
 * @param A the equatorial radius, in meter
 * @param B the polar radius, in meter
 * @param F the inverse flattening
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 1.0
 */
public record Ellipsoid(String name, double A, double B, double F)
	implements Serializable
{

	/**
	 * The ellipsoid of the <em>World Geodetic System: WGS 84</em>
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/World_Geodetic_System#A_new_World_Geodetic_System:_WGS_84">
	 *     WGS-84</a>
	 */
	public static final Ellipsoid WGS84 = new Ellipsoid(
		"WGS-84",
		6_378_137,
		6_356_752.314245,
		298.257223563
	);

	/**
	 * The ellipsoid of the <em>International Earth Rotation and Reference
	 * Systems Service (1989)</em>
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/IERS">IERS-89</a>
	 */
	public static final Ellipsoid IERS_1989 = new Ellipsoid(
		"IERS-1989",
		6_378_136,
		6_356_751.302,
		298.257
	);

	/**
	 * The ellipsoid of the <em>International Earth Rotation and Reference
	 * Systems Service (2003)</em>
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/IERS">IERS-89</a>
	 */
	public static final Ellipsoid IERS_2003 = new Ellipsoid(
		"IERS-2003",
		6_378_136.6,
		6_356_751.9,
		298.25642
	);

	/**
	 * The default ellipsoid: WGSC-84
	 */
	public static final Ellipsoid DEFAULT = WGS84;

	/**
	 * Create a new earth ellipsoid with the given parameters.
	 *
	 * @param name the name of the earth ellipsoid model
	 * @param A the equatorial radius, in meter
	 * @param B the polar radius, in meter
	 * @param F the inverse flattening
	 * @throws NullPointerException if the given {@code name} is {@code null}
	 */
	public Ellipsoid {
		requireNonNull(name);
	}

}
