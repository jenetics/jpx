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
package io.jenetics.jpx;

import java.text.ParsePosition;
import java.time.format.DateTimeFormatter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public abstract class LocationFormatter {

	public static final LocationFormatter ISO6709_DECIMAL = null;

	public static final LocationFormatter ISO6709_HUMAN_LONG = null;

	public static final LocationFormatter ISO6709_HUMAN_MEDIUM = null;

	public static final LocationFormatter ISO6709_HUMAN_SHORT = null;

	public static final LocationFormatter ISO6709_LONG = null;

	public static final LocationFormatter ISO6709_MEDIUM = null;

	public static final LocationFormatter ISO6709_SHORT = null;


	protected LocationFormatter() {
		DateTimeFormatter f;
	}

	public abstract String format(final Latitude lat);

	public abstract String format(final Longitude lon);

	public abstract String format(final Latitude lat, final Longitude lon);

	public abstract String format(final Point point);

	public abstract Object parseLatitude(final CharSequence text, final ParsePosition pos);

	public abstract Object parseLongitude(final CharSequence text, final ParsePosition pos);

}
