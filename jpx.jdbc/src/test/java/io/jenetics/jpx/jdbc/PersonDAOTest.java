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
package io.jenetics.jpx.jdbc;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import io.jenetics.jpx.Person;
import io.jenetics.jpx.PersonTest;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class PersonDAOTest {

	private final DB db = H2DB.newTestInstance();

	private final List<Person> persons = PersonTest.nextPersons(new Random(123));

	//@BeforeSuite
	public void setup() throws IOException, SQLException {
		final String[] queries = IO.
			toSQLText(getClass().getResourceAsStream("/model-mysql.sql"))
			.split(";");

		db.transaction(conn -> {
			for (String query : queries) {
				try (Statement stmt = conn.createStatement()) {
					stmt.execute(query);
				}
			}
		});
	}

	//@Test
	public void insert() throws SQLException {
		final List<Stored<Person>> stored = db.transaction(conn -> {
			return PersonDAO.of(conn).insert(persons);
		});

		stored.forEach(System.out::println);
	}

}
