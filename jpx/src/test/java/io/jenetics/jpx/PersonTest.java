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

import static java.lang.String.format;

import nl.jqno.equalsverifier.EqualsVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.testng.annotations.Test;

import io.jenetics.jpx.GPX.Version;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class PersonTest extends XMLStreamTestBase<Person> {

	@Override
	public Supplier<Person> factory(Random random) {
		return () -> nextPerson(random);
	}

	@Override
	protected Params<Person> params(final Version version, final Random random) {
		return new Params<>(
			() -> nextPerson(random),
			Person.reader("author"),
			Person.writer("author")
		);
	}

	public static List<Person> nextPersons(final Random random) {
		final List<Person> persons = new ArrayList<>();
		for (int i = 0, n = random.nextInt(20); i < n; ++i) {
			persons.add(PersonTest.nextPerson(random));
		}

		return persons;
	}

	public static Person nextPerson(final Random random) {
		return Person.of(
			random.nextBoolean()
				? format("name_%s", random.nextInt(100))
				: null,
			random.nextBoolean()
				? EmailTest.nextEmail(random)
				: null,
			random.nextBoolean()
				? LinkTest.nextLink(random)
				: null
		);
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(Person.class).verify();
	}

}
