package io.jenetics.jpx.tool;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import io.jenetics.jpx.Length;
import io.jenetics.jpx.Speed;
import io.jenetics.jpx.WayPoint;

final class PointFilter implements Predicate<WayPoint> {

	public static Predicate<WayPoint>
	time(final ZonedDateTime min, final ZonedDateTime max) {
		return predicate(min, max, WayPoint::getTime);
	}

	public static Predicate<WayPoint>
	elevation(final Length min, final Length max) {
		return predicate(min, max, WayPoint::getElevation);
	}

	public static Predicate<WayPoint> speed(final Speed min, final Speed max) {
		return predicate(min, max, WayPoint::getSpeed);
	}

	private static <C extends Comparable<? super C>> Predicate<WayPoint>
	predicate(final C min, final C max, final Function<WayPoint, Optional<C>> f) {
		return wp -> {
			final Optional<C> value = f.apply(wp);
			return value
				.map(v -> min.compareTo(v) <= 0 && max.compareTo(v) >= 0)
				.orElse(true);
		};
	}

	static final Predicate<WayPoint> FAULTY_POINTS =
	time(
		ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
		ZonedDateTime.of(2100, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()))
	.and(
		speed(
			Speed.of(0, Speed.Unit.KILOMETERS_PER_HOUR),
			Speed.of(300, Speed.Unit.KILOMETERS_PER_HOUR)))
	.and(
		elevation(
			Length.of(0, Length.Unit.METER),
			Length.of(10000, Length.Unit.METER)));

	/*
	static final Predicate<WayPoint> FAULTY_POINTS = new PointFilter(
		ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
		ZonedDateTime.of(2100, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
			6000,
			300
		);
	 */

	private final ZonedDateTime _minTime;
	private final ZonedDateTime _maxTime;
	private final double _maxElevation;
	private final double _maxSpeed;

	PointFilter(
		final ZonedDateTime minTime,
		final ZonedDateTime maxTime,
		final double maxElevation,
		final double maxSpeed
	) {
		_minTime = requireNonNull(minTime);
		_maxTime = requireNonNull(maxTime);
		_maxElevation = maxElevation;
		_maxSpeed = maxSpeed;
	}

	@Override
	public boolean test(final WayPoint wp) {
		final double speed = wp.getSpeed()
			.map(Speed::doubleValue)
			.orElse(0.0);

		final double ele = wp.getElevation()
			.map(Length::doubleValue)
			.orElse(0.0);

		final ZonedDateTime time = wp.getTime()
			.orElse(ZonedDateTime.of(LocalDateTime.MAX, ZoneId.systemDefault()));

		return speed >= 0 && speed < _maxSpeed &&
			ele > 0 && ele < _maxElevation &&
			time.isAfter(_minTime) &&
			time.isBefore(_maxTime);
	}

}
