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
package jpx.geom;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

/**
 * Represents an earth ellipsoid, which is a mathematical figure approximating
 * the shape of the Earth, used as a reference frame for computations in
 * geodesy, astronomy and the geosciences. Various different ellipsoids have
 * been used as approximations.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Earth_ellipsoid">Earth ellipsoid</a>
 * @see Geom
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Ellipsoid implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * @see <a href="https://en.wikipedia.org/wiki/World_Geodetic_System#A_new_World_Geodetic_System:_WGS_84">
	 *     WGS-84</a>
	 */
	public static final Ellipsoid WGSC_84 = of(
		"WGS-84",
		6_378_137,
		6_356_752.314245,
		1.0/298.257223563
	);

	/**
	 * @see <a href="https://en.wikipedia.org/wiki/IERS">IERS-89</a>
	 */
	public static final Ellipsoid IERS_1989 = of(
		"IERS-1989",
		6_378_136,
		6_356_751.302,
		1.0/298.257
	);

	public static final Ellipsoid IERS_2003 = of(
		"IERS-2003",
		6_378_136.6,
		6_356_751.9,
		1.0/298.25642
	);

	public static final Ellipsoid DEFAULT = WGSC_84;


	private final String _name;
	private final double _a;
	private final double _b;
	private final double _f;

	/**
	 * Create a new earth ellipsoid with the given parameters.
	 *
	 * @param name the name of the earth ellipsoid model
	 * @param a the equatorial radius, in meter
	 * @param b the polar radius, in meter
	 * @param f the inverse flattening
	 * @throws NullPointerException if the given {@code name} is {@code null}
	 */
	private Ellipsoid(
		final String name,
		final double a,
		final double b,
		final double f
	) {
		_name = requireNonNull(name);
		_a = a;
		_b = b;
		_f = f;
	}

	/**
	 * Return the name of the earth ellipsoid model.
	 *
	 * @return the name of the earth ellipsoid model
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Return the equatorial radius, in meter.
	 *
	 * @return the equatorial radius, in meter
	 */
	public double A() {
		return _a;
	}

	/**
	 * Return the polar radius, in meter.
	 *
	 * @return the polar radius, in meter
	 */
	public double B() {
		return _b;
	}

	/**
	 * Return the inverse flattening.
	 *
	 * @return the inverse flattening
	 */
	public double F() {
		return _f;
	}

	/**
	 * Create a new earth ellipsoid with the given parameters.
	 *
	 * @param name the name of the earth ellipsoid model
	 * @param a the equatorial radius, in meter
	 * @param b the polar radius, in meter
	 * @param f the inverse flattening
	 * @return  a new earth ellipsoid with the given parameters
	 */
	public static Ellipsoid of(
		final String name,
		final double a,
		final double b,
		final double f
	) {
		return new Ellipsoid(name, a, b, f);
	}

}
