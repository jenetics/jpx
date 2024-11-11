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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
package io.jenetics.gradle.dsl

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

/**
 * Gets the module name of the project, as configured in the build file.
 */
var Project.moduleName: String
	get() = if (this.isModule) this.extra.get("moduleName").toString()
			else this.name
	set(value) = this.extra.set("moduleName", value)

/**
 * Checks if the project is configured as a module.
 */
val Project.isModule: Boolean
	get() = this.extra.has("moduleName")
