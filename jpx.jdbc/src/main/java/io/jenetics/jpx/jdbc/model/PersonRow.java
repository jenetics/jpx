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
package io.jenetics.jpx.jdbc.model;

import io.jenetics.jpx.Email;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.jdbc.Stored;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Person {
	private final String _name;
	private final Email _email;
	private final Stored<Link> _link;

	/**
	 * Create a new {@code Person} object with the given parameters.
	 *
	 * @param name name of person or organization
	 * @param email the person's email address
	 * @param link link to Web site or other external information about person
	 */
	private Person(final String name, final Email email, final Stored<Link> link) {
		_name = name;
		_email = email;
		_link = link;
	}
}
