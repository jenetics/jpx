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
package io.jenetics.jpx.jdbc;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.ZonedDateTime;

import io.jenetics.jpx.DGPSStation;
import io.jenetics.jpx.Degrees;
import io.jenetics.jpx.Fix;
import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;
import io.jenetics.jpx.Speed;
import io.jenetics.jpx.UInt;

import io.jenetics.facilejdbc.spi.SqlTypeMapper;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class GpxTypeMapper extends SqlTypeMapper {
	@Override
	public Object convert(final Object value) {
		if (value instanceof Latitude) return ((Latitude)value).doubleValue();
		if (value instanceof Longitude) return ((Longitude)value).doubleValue();
		if (value instanceof Length) return ((Length)value).doubleValue();
		if (value instanceof Speed) return ((Speed)value).doubleValue();
		if (value instanceof Degrees) return ((Degrees)value).doubleValue();
		if (value instanceof Fix) return ((Fix)value).getValue();
		if (value instanceof UInt) return ((UInt)value).getValue();
		if (value instanceof DGPSStation) return ((DGPSStation)value).intValue();
		if (value instanceof ZonedDateTime) return ((ZonedDateTime)value).toOffsetDateTime();
		if (value instanceof Duration) return ((Duration)value).getSeconds();
		if (value instanceof URI) return value.toString();
		if (value instanceof  URL) return value.toString();
		return value;
	}
}
