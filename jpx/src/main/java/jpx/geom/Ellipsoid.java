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

import java.io.Serializable;

/**
 *
 * @see <a href="https://en.wikipedia.org/wiki/Earth_ellipsoid">Earth ellipsoid</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Ellipsoid implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final Ellipsoid WGSC84 = new Ellipsoid(
		6378137,
		6356752.314245,
		1.0/298.257223563
	);

	private final double _a;
	private final double _b;
	private final double _f;

	private Ellipsoid(final double a, final double b, final double f) {
		_a = a;
		_b = b;
		_f = f;
	}

	public double A() {
		return _a;
	}

	public double B() {
		return _b;
	}

	public double F() {
		return _f;
	}

	public static Ellipsoid of(final double a, final double b, final double f) {
		return new Ellipsoid(a, b, f);
	}

}
