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

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ListsTest {

	@Test
	public void isImmutable() {
		Assert.assertTrue(Lists.isImmutable(unmodifiableList(new ArrayList<String>())));
		Assert.assertTrue(Lists.isImmutable(unmodifiableList(new LinkedList<>())));
		Assert.assertTrue(Lists.isImmutable(emptyList()));
	}

	static <T> List<T> revert(final List<T> list) {
		final List<T> result = new ArrayList<T>(list.size());
		for (int i = 0, n = list.size(); i < n; ++i) {
			result.add(list.get(n - i - 1));
		}
		return result;
	}

}
