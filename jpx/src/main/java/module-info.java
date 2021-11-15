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
 *
 * <p>
 * <em><b>Examples:</b></em>
 * <p>
 * <b>Creating a GPX object with one track-segment and 3 track-points</b>
 * <pre>{@code
 * final GPX gpx = GPX.builder()
 *     .addTrack(track -> track
 *         .addSegment(segment -> segment
 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
 *     .build();
 * }</pre>
 *
 * <b>Reading a GPX file</b>
 * <pre>{@code
 * final GPX gpx = GPX.read("track.xml");
 * }</pre>
 *
 * <b>Reading erroneous GPX files</b>
 * <pre>{@code
 * final boolean lenient = true;
 * final GPX gpx = GPX.read("track.xml", lenient);
 * }</pre>
 *
 * This allows to read otherwise invalid GPX files, like
 * <pre>{@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <gpx version="1.1" creator="GPSBabel - http://www.gpsbabel.org" xmlns="http://www.topografix.com/GPX/1/0">
 *     <metadata>
 *         <time>2015-11-13T15:22:42.140Z</time>
 *         <bounds minlat="-37050536.000000000" minlon="-0.000000000" maxlat="48.359161377" maxlon="16.448385239"/>
 *     </metadata>
 *     <trk>
 *         <name>track-1</name>
 *         <desc>Log every 3 sec, 0 m</desc>
 *         <trkseg>
 *             <trkpt></trkpt>
 *             <trkpt lat="48.199352264" lon="16.403341293">
 *                 <ele>4325376.000000</ele>
 *                 <time>2015-10-23T17:07:08Z</time>
 *                 <speed>2.650000</speed>
 *                 <name>TP000001</name>
 *             </trkpt>
 *             <trkpt lat="6.376383781" lon="-0.000000000">
 *                 <ele>147573952589676412928.000000</ele>
 *                 <time>1992-07-19T10:10:58Z</time>
 *                 <speed>464.010010</speed>
 *                 <name>TP000002</name>
 *             </trkpt>
 *             <trkpt lat="-37050536.000000000" lon="0.000475423">
 *                 <ele>0.000000</ele>
 *                 <time>2025-12-17T05:10:27Z</time>
 *                 <speed>56528.671875</speed>
 *                 <name>TP000003</name>
 *             </trkpt>
 *             <trkpt></trkpt>
 *         </trkseg>
 *     </trk>
 * </gpx>
 * }</pre>
 *
 * which is read as
 * <pre>{@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <gpx xmlns="http://www.topografix.com/GPX/1/1" version="1.1" creator="JPX" >
 *     <metadata>
 *         <time>2015-11-13T15:22:42.140Z</time>
 *     </metadata>
 *     <trk>
 *         <name>track-1</name>
 *         <desc>Log every 3 sec, 0 m</desc>
 *         <trkseg>
 *             <trkpt lat="48.199352264" lon="16.403341293">
 *                 <ele>4325376.000000</ele>
 *                 <time>2015-10-23T17:07:08Z</time>
 *                 <speed>2.650000</speed>
 *                 <name>TP000001</name>
 *             </trkpt>
 *             <trkpt lat="6.376383781" lon="-0.000000000">
 *                 <ele>147573952589676412928.000000</ele>
 *                 <time>1992-07-19T10:10:58Z</time>
 *                 <speed>464.010010</speed>
 *                 <name>TP000002</name>
 *             </trkpt>
 *         </trkseg>
 *     </trk>
 * </gpx>
 * }</pre>
 */
module io.jenetics.jpx {
	requires transitive java.xml;

	exports io.jenetics.jpx;
	exports io.jenetics.jpx.format;
	exports io.jenetics.jpx.geom;

	uses io.jenetics.jpx.XMLProvider;
}
