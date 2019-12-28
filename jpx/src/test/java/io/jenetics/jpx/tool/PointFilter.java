package io.jenetics.jpx.tool;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Predicate;

import io.jenetics.jpx.Length;
import io.jenetics.jpx.Speed;
import io.jenetics.jpx.WayPoint;

final class PointFilter implements Predicate<WayPoint> {

	static final PointFilter FAULTY_POINTS = new PointFilter(
		ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
		ZonedDateTime.of(2100, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
			6000,
			300
		);

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
