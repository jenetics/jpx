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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package jpx.geom;

import static java.util.Objects.requireNonNull;

import jpx.Length;
import jpx.Point;

/**
 * Helper class for collecting a stream of points to its length.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class LengthCollector {

	private final Geoid _geoid;
	private final DoubleAdder _length = new DoubleAdder();

	private Point _first;
	private Point _start;

	LengthCollector(final Geoid geoid) {
		_geoid = requireNonNull(geoid);
	}

	LengthCollector combine(final LengthCollector other) {
		throw new UnsupportedOperationException();
	}

	void add(final Point point) {
		requireNonNull(point);

		if (_first == null) {
			_first = point;
		}

		final Point end = _start;
		_start = point;

		if (end != null) {
			_length.add(_geoid.distance(end, _start).doubleValue());
		}
	}

	Length pathLength() {
		return Length.ofMeters(_length.doubleValue());
	}

	Length tourLength() {
		if (_start != null && _first != null) {
			_length.add(_geoid.distance(_start, _first).doubleValue());
		}
		return pathLength();
	}
}
