/*
 * Java GPX Library (@__identifier__@).
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
 */

module io.jenetics.jpx.jdbc {
	requires transitive io.jenetics.jpx;
	requires transitive io.jenetics.facilejdbc;

	exports io.jenetics.jpx.jdbc;

	provides io.jenetics.facilejdbc.spi.SqlTypeMapper
		with io.jenetics.jpx.jdbc.GpxTypeMapper;
}
