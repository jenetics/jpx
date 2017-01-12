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
package io.jenetics.jpx.jdbc;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.jpx.Email;
import io.jenetics.jpx.Person;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class DiffTest {


	@Test
	public void missing() {
		final Map<String, Person> existing =
			Stream.of(
				Person.of("name_1", Email.of("name", "gmail.com")),
				Person.of("name_2", Email.of("name", "gmail.com")))
			.collect(toMap(p -> p.getName().orElse(null), a -> a, (a, b) -> b));

		final Map<String, Person> actual =
			Stream.of(
				Person.of("name_1", Email.of("name", "gmail.com")),
				Person.of("name_2", Email.of("name", "gmail.com")),
				Person.of("name_3", Email.of("name", "gmail.com")))
			.collect(toMap(p -> p.getName().orElse(null), a -> a, (a, b) -> b));

		final Diff<String, Person, Person> diff = Diff.of(existing, actual);

		final List<Person> missing = diff.missing();
		Assert.assertEquals(
			missing.get(0),
			Person.of("name_3", Email.of("name", "gmail.com"))
		);
	}

}
