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
package io.jenetics.jpx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ListsTest {

	@Test
	public void equalityCountsDuplicateElements() {
		Assert.assertFalse(Lists.equals(List.of("a", "a"), List.of("a", "b")));
		Assert.assertFalse(Lists.equals(List.of("a", "b"), List.of("a", "a")));
		Assert.assertFalse(Lists.equals(List.of("a", "a", "b"), List.of("a", "b", "b")));
		Assert.assertTrue(Lists.equals(List.of("a", "a", "b"), List.of("b", "a", "a")));
	}

	@Test
	public void equalityCountsNullElements() {
		Assert.assertFalse(Lists.equals(Arrays.asList(null, null), Arrays.asList(null, "a")));
		Assert.assertFalse(Lists.equals(Arrays.asList(null, "a"), Arrays.asList(null, null)));
		Assert.assertTrue(Lists.equals(Arrays.asList(null, "a"), Arrays.asList("a", null)));
	}

	@Test
	public void wayPointEqualityWithDuplicateLinksIsSymmetric() {
		final var a = Link.of("https://example.com/a");
		final var b = Link.of("https://example.com/b");
		final var first = WayPoint.builder().lat(0).lon(0).links(List.of(a, a)).build();
		final var second = WayPoint.builder().lat(0).lon(0).links(List.of(a, b)).build();
		Assert.assertFalse(first.equals(second));
		Assert.assertFalse(second.equals(first));
	}

	static <T> List<T> revert(final List<T> list) {
		final List<T> result = new ArrayList<T>(list.size());
		for (int i = 0, n = list.size(); i < n; ++i) {
			result.add(list.get(n - i - 1));
		}
		return result;
	}

}
