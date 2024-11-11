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
 * The data classes are completely immutable and allow a functional programming
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
 *             .addPoint(p -> p.lat(48.20100).lon(16.31651).ele(283))
 *             .addPoint(p -> p.lat(48.20112).lon(16.31639).ele(278))
 *             .addPoint(p -> p.lat(48.20126).lon(16.31601).ele(274))))
 *     .build();
 * }</pre>
 *
 * <b>Writing a GPX file</b>
 * <pre>{@code
 * final var indent = new GPX.Writer.Indent("    ");
 * GPX.Writer.of(indent).write(gpx, Path.of("points.gpx"));
 * }</pre>
 *
 * This will produce the following output.
 * <pre>{@code
 * <gpx version="1.1" creator="JPX - https://github.com/jenetics/jpx" xmlns="http://www.topografix.com/GPX/1/1">
 *     <trk>
 *         <trkseg>
 *             <trkpt lat="48.201" lon="16.31651">
 *                 <ele>283</ele>
 *             </trkpt>
 *             <trkpt lat="48.20112" lon="16.31639">
 *                 <ele>278</ele>
 *             </trkpt>
 *             <trkpt lat="48.20126" lon="16.31601">
 *                 <ele>274</ele>
 *             </trkpt>
 *         </trkseg>
 *     </trk>
 * </gpx>
 * }</pre>
 *
 * <b>Reading a GPX file</b>
 * <pre>{@code
 * final GPX gpx = GPX.read(Path.of("points.gpx"));
 * }</pre>
 *
 * <b>Reading erroneous GPX files</b>
 * <pre>{@code
 * final GPX gpx = GPX.Reader
 *     .of(GPX.Reader.Mode.LENIENT)
 *     .read(Path.of("points.gpx"));
 * }</pre>
 *
 * This allows reading otherwise invalid GPX files, like
 * <pre>{@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <gpx version="1.1" creator="GPSBabel - http://www.gpsbabel.org" xmlns="http://www.topografix.com/GPX/1/1">
 *   <metadata>
 *     <time>2019-12-31T21:36:04.134Z</time>
 *     <bounds minlat="48.175186667" minlon="16.299580000" maxlat="48.199555000" maxlon="16.416933333"/>
 *   </metadata>
 *   <trk>
 *     <trkseg>
 *       <trkpt lat="48.184298333" lon="16.299580000">
 *         <ele>0.000</ele>
 *         <time>2011-03-20T09:47:16Z</time>
 *         <geoidheight>43.5</geoidheight>
 *         <fix>2d</fix>
 *         <sat>3</sat>
 *         <hdop>4.200000</hdop>
 *         <vdop>1.000000</vdop>
 *         <pdop>4.300000</pdop>
 *       </trkpt>
 *       <trkpt lat="48.175186667" lon="16.303916667">
 *         <ele>0.000</ele>
 *         <time>2011-03-20T09:51:31Z</time>
 *         <geoidheight>43.5</geoidheight>
 *         <fix>2d</fix>
 *         <sat>3</sat>
 *         <hdop>16.600000</hdop>
 *         <vdop>0.900000</vdop>
 *         <pdop>16.600000</pdop>
 *       </trkpt>
 *     </trkseg>
 *   </trk>
 * </gpx>
 * }</pre>
 *
 * which is read as (if you write it again)
 * <pre>{@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <gpx version="1.1" creator="GPSBabel - http://www.gpsbabel.org" xmlns="http://www.topografix.com/GPX/1/1">
 *     <metadata>
 *         <time>2019-12-31T21:36:04.134Z</time>
 *         <bounds minlat="48.175187" minlon="16.29958" maxlat="48.199555" maxlon="16.416933"></bounds>
 *     </metadata>
 *     <trk>
 *         <trkseg>
 *             <trkpt lat="48.184298" lon="16.29958">
 *                 <ele>0</ele>
 *                 <time>2011-03-20T09:47:16Z</time>
 *                 <geoidheight>43.5</geoidheight>
 *                 <fix>2d</fix>
 *                 <sat>3</sat>
 *                 <hdop>4.2</hdop>
 *                 <vdop>1</vdop>
 *                 <pdop>4.3</pdop>
 *             </trkpt>
 *             <trkpt lat="48.175187" lon="16.303917">
 *                 <ele>0</ele>
 *                 <time>2011-03-20T09:51:31Z</time>
 *                 <geoidheight>43.5</geoidheight>
 *                 <fix>2d</fix>
 *                 <sat>3</sat>
 *                 <hdop>16.6</hdop>
 *                 <vdop>0.9</vdop>
 *                 <pdop>16.6</pdop>
 *             </trkpt>
 *         </trkseg>
 *     </trk>
 * </gpx>
 * }</pre>
 *
 * <b>Converting a GPX object to an XML {@link org.w3c.dom.Document}</b>
 * <pre>{@code
 * final GPX gpx = ...;
 *
 * final Document doc = XMLProvider.provider()
 *     .documentBuilderFactory()
 *     .newDocumentBuilder()
 *     .newDocument();
 *
 * // The GPX data are written to the empty `doc` object.
 * GPX.Writer.DEFAULT.write(gpx, new DOMResult(doc));
 * }</pre>
 */
module io.jenetics.jpx {
	requires transitive java.xml;

	exports io.jenetics.jpx;
	exports io.jenetics.jpx.format;
	exports io.jenetics.jpx.geom;

	uses io.jenetics.jpx.XMLProvider;
}
