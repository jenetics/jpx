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

import java.time.Year
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Common environment values.
 */
object Env {
	private val NOW: ZonedDateTime = ZonedDateTime.now()

	private val YEAR: Year = Year.now();

	val COPYRIGHT_YEAR = "2016-${YEAR}"

	private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

	val BUILD_DATE: String = DATE_FORMAT.format(NOW)

	val BUILD_JDK: String = System.getProperty("java.version")

	val BUILD_OS_NAME: String = System.getProperty("os.name")

	val BUILD_OS_ARCH: String = System.getProperty("os.arch")

	val BUILD_OS_VERSION: String = System.getProperty("os.version")

	val BUILD_BY: String = System.getProperty("user.name")

}

/**
 * Information about the library and author.
 */
object JPX {
	const val VERSION = "3.2.0"
	const val ID = "jpx"
	const val NAME = "jpx"
	const val GROUP = "io.jenetics"
	const val AUTHOR = "Franz Wilhelmstötter"
	const val EMAIL = "franz.wilhelmstoetter@gmail.com"
	const val URL = "https://github.com/jenetics/jpx"
}

/**
 * Environment variables for publishing to Maven Central.
 */
object Maven {
	const val SNAPSHOT_URL = "https://oss.sonatype.org/content/repositories/snapshots/"
	const val RELEASE_URL = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"

	const val SCM_URL = "https://github.com/jenetics/jpx"
	const val SCM_CONNECTION = "scm:git:https://github.com/jenetics/jpx.git"
	const val DEVELOPER_CONNECTION = "scm:git:https://github.com/jenetics/jpx.git"
}
