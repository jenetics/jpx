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

import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Collector;

import io.jenetics.jpx.Length;
import io.jenetics.jpx.Length.Unit;
import io.jenetics.jpx.Point;
import io.jenetics.jpx.Speed;

/**
 * Implementation of <em>geodetic</em> functions.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Geoid">Wikipedia: Geoid</a>
 * @see Ellipsoid
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 1.0
 */
public final class Geoid {

	/**
	 * {@link Geoid} using of the <em>World Geodetic System: WGS 84</em>
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/World_Geodetic_System#A_new_World_Geodetic_System:_WGS_84">
	 *     WGS-84</a>
	 */
	public static final Geoid WGS84 = of(Ellipsoid.WGS84);

	/**
	 * {@link Geoid} using the <em>International Earth Rotation and Reference
	 * Systems Service (1989)</em>
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/IERS">IERS-89</a>
	 */
	public static final Geoid IERS_1989 = of(Ellipsoid.IERS_1989);

	/**
	 * {@link Geoid} using the <em>International Earth Rotation and Reference
	 * Systems Service (2003)</em>
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/IERS">IERS-89</a>
	 */
	public static final Geoid IERS_2003 = of(Ellipsoid.IERS_2003);

	/**
	 * {@link Geoid} using the {@link Ellipsoid#DEFAULT} ellipsoid.
	 */
	public static final Geoid DEFAULT = of(Ellipsoid.DEFAULT);

	private final Ellipsoid _ellipsoid;

	// Minor semi-axes of the ellipsoid.
	private final double B;

	private final double AABBBB;

	// Flattening (A - B)/A
	private final double F;

	// The maximal iteration of the 'distance'
	private static final int DISTANCE_ITERATION_MAX = 1000;

	// The epsilon of the result, when to stop iteration.
	private static final double DISTANCE_ITERATION_EPSILON = 1E-12;

	/**
	 * Create a new {@code Geoid} object with the given ellipsoid.
	 *
	 * @param ellipsoid the ellipsoid used by the geoid
	 * @throws NullPointerException if the given {@code ellipsoid} is {@code null}
	 */
	private Geoid(final Ellipsoid ellipsoid) {
		_ellipsoid = requireNonNull(ellipsoid);

		final double a = ellipsoid.A();
		final double aa = a*a;

		B = ellipsoid.B();
		final double bb = B*B;

		AABBBB = (aa - bb)/bb;
		F = 1.0/ellipsoid.F();
	}

	/**
	 * Return the ellipsoid the {@code Geom} object is using.
	 *
	 * @return the ellipsoid the {@code Geom} object is using
	 */
	public Ellipsoid getEllipsoid() {
		return _ellipsoid;
	}

	/**
	 * Calculate the distance between points on an ellipsoidal earth model. This
	 * method will throw an {@link ArithmeticException} if the algorithm doesn't
	 * converge while calculating the distance, which is the case for a point
	 * and its (near) antidote.
	 *
	 * @see <a href="http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf">DIRECT AND
	 *               INVERSE SOLUTIONS OF GEODESICS 0 THE ELLIPSOID
	 *               WITH APPLICATION OF NESTED EQUATIONS</a>
	 * @see <a href="http://www.movable-type.co.uk/scripts/latlong-vincenty.html">
	 *     Vincenty solutions of geodesics on the ellipsoid</a>
	 *
	 * @param start the start point
	 * @param end the end point
	 * @return the distance between {@code start} and {@code end} in meters
	 * @throws NullPointerException if one of the points is {@code null}
	 * @throws ArithmeticException if the algorithm used for calculating the
	 *         distance between {@code start} and {@code end} didn't converge,
	 *         which is the case for a point and its (near) antidote.
	 */
	public Length distance(final Point start, final Point end) {
		final double lat1 = start.getLatitude().toRadians();
		final double lon1 = start.getLongitude().toRadians();
		final double lat2 = end.getLatitude().toRadians();
		final double lon2 = end.getLongitude().toRadians();

		final double omega = lon2 - lon1;

		final double tanphi1 = tan(lat1);
		final double tanU1 = (1.0 - F)*tanphi1;
		final double U1 = atan(tanU1);
		final double sinU1 = sin(U1);
		final double cosU1 = cos(U1);

		final double tanphi2 = tan(lat2);
		final double tanU2 = (1.0 - F)*tanphi2;
		final double U2 = atan(tanU2);
		final double sinU2 = sin(U2);
		final double cosU2 = cos(U2);

		final double sinU1sinU2 = sinU1*sinU2;
		final double cosU1sinU2 = cosU1*sinU2;
		final double sinU1cosU2 = sinU1*cosU2;
		final double cosU1cosU2 = cosU1*cosU2;

		// Eq. 13
		double lambda = omega;

		// Intermediates we'll need to compute distance 's'
		double a;
		double b;
		double sigma;
		double deltasigma;
		double lambda0;

		int iteration = 0;
		do {
			lambda0 = lambda;

			final double sinlambda = sin(lambda);
			final double coslambda = cos(lambda);

			// Eq. 14
			final double sin2sigma =
				(cosU2*sinlambda*cosU2*sinlambda) +
					(cosU1sinU2 - sinU1cosU2*coslambda)*
						(cosU1sinU2 - sinU1cosU2*coslambda);
			final double sinsigma = sqrt(sin2sigma);

			// Eq. 15
			final double cossigma = sinU1sinU2 + (cosU1cosU2*coslambda);

			// Eq. 16
			sigma = atan2(sinsigma, cossigma);

			// Eq. 17 Careful! sin2sigma might be almost 0!
			final double sinalpha = sin2sigma == 0.0
				? 0.0
				: cosU1cosU2*sinlambda/sinsigma;
			final double alpha = asin(sinalpha);
			final double cosalpha = cos(alpha);
			double cos2alpha = cosalpha*cosalpha;

			// Eq. 18 Careful! cos2alpha might be almost 0!
			final double cos2sigmam = cos2alpha == 0.0
				? 0.0
				: cossigma - 2*sinU1sinU2/cos2alpha;
			final double u2 = cos2alpha*AABBBB;

			final double cos2sigmam2 = cos2sigmam*cos2sigmam;

			// Eq. 3
			a = 1.0 + u2/16384*(4096 + u2*(-768 + u2*(320 - 175*u2)));

			// Eq. 4
			b = u2/1024*(256 + u2*(-128 + u2*(74 - 47*u2)));

			// Eq. 6
			deltasigma = b*sinsigma*(cos2sigmam +
				b/4*(cossigma*(-1 + 2 * cos2sigmam2) -
					b/6*cos2sigmam*(-3 + 4*sin2sigma)*(-3 + 4*cos2sigmam2)));

			// Eq. 10
			final double C = F/16*cos2alpha*(4 + F*(4 - 3*cos2alpha));

			// Eq. 11
			lambda = omega + (1 - C)*F*sinalpha*
				(sigma + C*sinsigma*(cos2sigmam +
					C*cossigma*(-1 + 2*cos2sigmam2)));

		} while (iteration++ < DISTANCE_ITERATION_MAX &&
			(abs((lambda - lambda0)/lambda) > DISTANCE_ITERATION_EPSILON));

		if (iteration >= DISTANCE_ITERATION_MAX) {
			throw new ArithmeticException(format(
				"Calculating distance between %s and %s didn't converge.",
				start, end
			));
		}

		// Eq. 19
		final double s = B*a*(sigma - deltasigma);

		return Length.of(s, Unit.METER);
	}

	/**
	 * Return a collector which calculates the length of the (open) path which
	 * is defined by the {@code Point} stream.
	 *
	 * <pre>{@code
	 * final Length length = gpx.tracks()
	 *     .flatMap(Track::segments)
	 *     .flatMap(TrackSegment::points)
	 *     .collect(Geoid.WGSC_84.toPathLength());
	 * }</pre>
	 *
	 * <b>The returned {@code Collector} doesn't work for <em>parallel</em>
	 * stream. Using it for a <em>parallel</em> point stream will throw an
	 * {@link UnsupportedOperationException} at runtime.</b>
	 *
	 * @see #toTourLength()
	 *
	 * @return a new path length collector
	 */
	public Collector<Point, ?, Length> toPathLength() {
		return Collector.of(
			() -> new LengthCollector(this),
			LengthCollector::add,
			LengthCollector::combine,
			LengthCollector::pathLength
		);
	}

	/**
	 * Return a collector which calculates the length of the (closed) tour which
	 * is defined by the {@code Point} stream. The <em>tour</em> length
	 * additionally adds the distance of the last point back to the first point.
	 *
	 * <pre>{@code
	 * final Length length = gpx.tracks()
	 *     .flatMap(Track::segments)
	 *     .flatMap(TrackSegment::points)
	 *     .collect(Geoid.WGSC_84.toTourLength());
	 * }</pre>
	 *
	 * <b>The returned {@code Collector} doesn't work for <em>parallel</em>
	 * stream. Using it for a <em>parallel</em> point stream will throw an
	 * {@link UnsupportedOperationException} at runtime.</b>
	 *
	 * @see #toPathLength()
	 *
	 * @return a new path length collector
	 */
	public Collector<Point, ?, Length> toTourLength() {
		return Collector.of(
			() -> new LengthCollector(this),
			LengthCollector::add,
			LengthCollector::combine,
			LengthCollector::tourLength
		);
	}

	/**
	 * Calculating the speed of point {@code p1} with the given predecessor point
	 * {@code p0}. {@link Optional#empty()} is returned if the time property of
	 * one of the given points is <em>empty</em>.
	 *
	 * @since !__version__!
	 *
	 * @param p0 the start point
	 * @param p1 the destination
	 * @return the calculated (average) speed between the two given points
	 * @throws NullPointerException if one of the points is {@code null}
	 */
	public Optional<Speed> speed(final Point p0, final Point p1) {
		return duration(p0, p1)
			.map(sec -> Speed.of(
				distance(p0, p1).doubleValue()/sec,
				Speed.Unit.METERS_PER_SECOND)
			);
	}

	private static Optional<Double> duration(final Point p0, final Point p1) {
		return p0.getTime().flatMap(t0 ->
			p1.getTime().map(t1 ->
				minus(t0, t1)/1_000.0
			)
		);
	}

	private static long minus(final ZonedDateTime t1, final ZonedDateTime t2) {
		final long i1 = t1.toInstant().toEpochMilli();
		final long i2 = t2.toInstant().toEpochMilli();
		return i2 - i1;
	}

	/* *************************************************************************
	 * Factory methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Geoid} object with the given ellipsoid.
	 *
	 * @param ellipsoid the ellipsoid used by the geoid
	 * @return a new {@code Geoid} object with the given ellipsoid
	 * @throws NullPointerException if the given {@code ellipsoid} is {@code null}
	 */
	public static Geoid of(final Ellipsoid ellipsoid) {
		return new Geoid(ellipsoid);
	}

}
