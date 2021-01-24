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

/**
 * <em>JPX</em> is a library for creating, reading and writing
 * <a href="https://en.wikipedia.org/wiki/Global_Positioning_System">GPS</a>
 * data in <a href="https://en.wikipedia.org/wiki/GPS_Exchange_Format">GPX</a>
 * format. It is a <em>full</em> implementation of version
 * <a href="http://www.topografix.com/GPX/1/1/">1.1</a> and version
 * <a href="http://www.topografix.com/gpx_manual.as">1.0</a> of the GPX format.
 * The data classes are completely immutable and allows a functional programming
 * style. It is also possible to convert the location information into strings
 * which are compatible to the <a href="http://en.wikipedia.org/wiki/ISO_6709">
 * ISO 6709</a> standard.
 */
module io.jenetics.jpx {
	requires transitive java.xml;

	exports io.jenetics.jpx;
	exports io.jenetics.jpx.format;
	exports io.jenetics.jpx.geom;

	uses io.jenetics.jpx.XMLProvider;
}
