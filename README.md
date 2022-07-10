# JPX

![Build Status](https://github.com/jenetics/jpx/actions/workflows/gradle.yml/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.jenetics/jpx/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22jpx%22)
[![Javadoc](https://www.javadoc.io/badge/io.jenetics/jpx.svg)](http://www.javadoc.io/doc/io.jenetics/jpx)

**JPX** is a Java library for creating, reading and writing [GPS](https://en.wikipedia.org/wiki/Global_Positioning_System) data in [GPX](https://en.wikipedia.org/wiki/GPS_Exchange_Format) format. It is a *full* implementation of version [1.1](http://www.topografix.com/GPX/1/1/) and version [1.0](http://www.topografix.com/gpx_manual.asp) of the GPX format. The data classes are completely immutable and allows a functional programming style. They  are working also nicely with the Java [Stream](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/stream/Stream.html) API. It is also possible to convert the location information into strings which are compatible to the [ISO 6709](http://en.wikipedia.org/wiki/ISO_6709) standard.

Beside the basic functionality of reading and writing GPX files, the library also allows to manipulate the read GPX object in a functional way.


## Dependencies

No external dependencies are needed by the _JPX_ library. It only needs **Java 17** to compile and run.


## Building JPX

For  building the JPX library you have to check out the master branch from Github.

    $ git clone https://github.com/jenetics/jpx.git
    
*Executing the tests:*
    
    $ cd jpx
    $ ./gradlew test

*Building the library:*

    $ ./gradlew jar
    

## Examples

### Creating new GPX object with 3 track-points

```java
final GPX gpx = GPX.builder()
    .addTrack(track -> track
        .addSegment(segment -> segment
        .addPoint(p -> p.lat(48.20100).lon(16.31651).ele(283))
        .addPoint(p -> p.lat(48.20112).lon(16.31639).ele(278))
        .addPoint(p -> p.lat(48.20126).lon(16.31601).ele(274))))
    .build();
```

**Writing GPX object to a file**

```java
GPX.write(gpx, "track.gpx");
```

*GPX output*

```xml
<gpx version="1.1" creator="JPX - https://github.com/jenetics/jpx" xmlns="http://www.topografix.com/GPX/1/1">
    <trk>
        <trkseg>
            <trkpt lat="48.201" lon="16.31651">
                <ele>283</ele>
            </trkpt>
            <trkpt lat="48.20112" lon="16.31639">
                <ele>278</ele>
            </trkpt>
            <trkpt lat="48.20126" lon="16.31601">
                <ele>274</ele>
            </trkpt>
        </trkseg>
    </trk>
</gpx>
```

### Reading GPX object from file

This example writes a given `GPX` object to a file, reads it again and prints the `WayPoint`s of all tracks and all track-segments to the console.

```java
GPX.write(gpx, "track.gpx");
GPX.read("gpx.xml").tracks()
    .flatMap(Track::segments)
    .flatMap(TrackSegment::points)
    .forEach(System.out::println);

```

*Console output*

```bash
$ [lat=48.201, lon=16.31651, ele=283]
$ [lat=48.20112, lon=16.31639, ele=278]
$ [lat=48.20126, lon=16.31601, ele=274]

```

### Reading GPX extensions

The library is also able to read arbitrary GPX _extensions_.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="JPX - Java GPX library" xmlns="http://www.topografix.com/GPX/1/1">
    ...
    <extensions>
        <gpxdata:lap xmlns:gpxdata="http://www.cluetrust.com/XML/GPXDATA/1/0">
            <gpxdata:index>1</gpxdata:index>
            <gpxdata:startPoint lat="51.219983" lon="6.765224"/>
            <gpxdata:endPoint lat="51.220137" lon="6.765098" />
        </gpxdata:lap>
    </extensions>
</gpx>

```

The extensions are available via a `org.w3c.dom.Document` object, with an `extensions` root element.

```java
final Optional<Document> extensions = gpx.getExtensions();
```

### Converting a `GPX` object into an `org.w3c.dom.Document`

```java
final GPX gpx = ...;

final Document doc = XMLProvider.provider()
    .documentBuilderFactory()
    .newDocumentBuilder()
    .newDocument();

// The GPX data are written to the empty `doc` object.
GPX.Writer.DEFAULT.write(gpx, new DOMResult(doc));
```

### Reading GPX 1.0 and writing GPX 1.1

By default, JPX is reading and writing the GPX files in version 1.1. But it is possible to read and write GPX files in version 1.0 as well.

```java
// Reading GPX 1.0 file.
final GPX gpx10 = GPX.reader(GPX.Version.V10).read("track-v10.gpx");

// Changing GPX version to 1.1.
final GPX gpx11 = gpx10.toBuilder()
    .version(GPX.Version.V11)
    .build();

// Writing GPX to file.
GPX.write(gpx11, "track-v11.gpx");
```

### ISO 6709 location strings

With the `LocationFormatter` class it is possible to create ISO 6709 compatible strings.

```java
final Point p = WayPoint.of(...);
final Location loc = Location.of(p);
final LocationFormatter format = LocationFormatter.ISO_HUMAN_LONG;
System.out.println(format.format(loc));
```
The printed location will look like this

    24°59'15.486"N 65°14'03.390"W 65.23m

It is also possible to define your own formatter from a given pattern string,

```java
final LocationFormatter format = 
    LocationFormatter.ofPattern("DD°MMSS dd°mmss");
```
which leads to the following output

    24°5915 65°1403

This string can then also be parsed to a _location_.

```java
final Location location = format.parse("24°5915 65°1403");
```

### Geodetic calculations

#### Distance between two points

```java
final Point start = WayPoint.of(47.2692124, 11.4041024);
final Point end = WayPoint.of(47.3502, 11.70584);
final Length distance = Geoid.WGS84.distance(start, end);
System.out.println(distance);
```

*Console output*

```bash
$ 24528.356073554987 m
```

#### Path length 

Calculate the path length of the first track-segment.

```java
final Length length = gpx.tracks()
    .flatMap(Track::segments)
    .findFirst()
    .map(TrackSegment::points).orElse(Stream.empty())
    .collect(Geoid.WGS84.toPathLength());
```

### GPX manipulation/filtering

#### Filtering

The following example filters empty tracks and track-segments from an existing `GPX` object.
    
```java
final GPX gpx = GPX.read("track.gpx");

// Filtering empty tracks.
final GPX gpx1 = gpx.toBuilder()
    .trackFilter()
        .filter(Track::nonEmpty)
        .build()
    .build();

// Filtering empty track-segments.
final GPX gpx2 = gpx.toBuilder()
    .trackFilter()
        .map(track -> track.toBuilder()
            .filter(TrackSegment::nonEmpty)
            .build())
        .build()
    .build();

// Filtering empty tracks and track-segments.
final GPX gpx3 = gpx.toBuilder()
    .trackFilter()
        .map(track -> track.toBuilder()
            .filter(TrackSegment::nonEmpty)
            .build())
        .filter(Track::nonEmpty)
        .build()
    .build();
```

#### Changing GPX object

*Fixing* the time of all track way-points by adding one hour.

```java
final GPX gpx = GPX.read("track.gpx");

final GPX gpx1 = gpx.toBuilder()
    .trackFilter()
        .map(track -> track.toBuilder()
            .map(segment -> segment.toBuilder()
                .map(wp -> wp.toBuilder()
                    .time(wp.getTime()
                        .map(t -> t.plusHours(1))
                        .orElse(null))
                    .build())
                .build())
            .build())
        .build()
    .build();
```

Doing the same only for the GPX way-points.

```java
final GPX gpx = GPX.read("track.gpx");

final GPX gpx1 = gpx.toBuilder()
    .wayPointFilter()
        .map(wp -> wp.toBuilder()
            .time(wp.getTime()
                .map(t -> t.plusHours(1))
                .orElse(null))
            .build())
        .build()
    .build();
```

### XML configuration

The _JPX_ library uses the XML classes available in the Java [`java.xml`](https://docs.oracle.com/en/java/javase/11/docs/api/java.xml/module-summary.html) module. This API is highly configurable and it is possible to replace the underlying implementation. Especially for Android, using different XML implementation is a necessity. _JPX_ uses three _factory_ classes for reading/writing GPX files:

1. [`XMLInputFactory`](https://docs.oracle.com/en/java/javase/11/docs/api/java.xml/javax/xml/stream/XMLInputFactory.html): This class is needed for reading GPX files.
1. [`XMLOutputFactory`](https://docs.oracle.com/en/java/javase/11/docs/api/java.xml/javax/xml/stream/XMLOutputFactory.html): This class is needed for writing GPX files.
1. [`DocumentBuilderFactory`](https://docs.oracle.com/en/java/javase/11/docs/api/java.xml/javax/xml/parsers/DocumentBuilderFactory.html): This class is used for creating XML-documents for the GPX `extensions` data.

You can change the used classes by implementing and registering a different `XMLProvider` class. The following code show how to change the configuration of the `DocumentBuilderFactory` class.

```java
package org.acme;
final class ValidatingDocumentBuilder extends XMLProvider { 
    @Override
    public DocumentBuilderFactory documentBuilderFactory() { 
        final DocumentBuilderFactory factory = 
            DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        return factory; 
    }
}
```
And don't forget to create a `META-INF/services/io.jenetics.jpx.XMLProvider` file with the following content:

```
org.acme.NonValidatingDocumentBuilder
```

## License

The library is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

    Copyright 2016-2022 Franz Wilhelmstötter

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## Release notes

### [3.0.1](https://github.com/jenetics/jpx/releases/tag/v3.0.1)

#### Bugs

* [#162](https://github.com/jenetics/jpx/issues/162): Elevation serialization for values > 1000m is incompatible with deserialization.

### [3.0.0](https://github.com/jenetics/jpx/releases/tag/v3.0.0)

#### Improvements

* [#125](https://github.com/jenetics/jpx/issues/125): **Breaking change** - Use `Instant` instead of `ZonedDateTime` for `Point.time` property.
* [#148](https://github.com/jenetics/jpx/issues/148): **Breaking change** - Update to Java17.
* [#155](https://github.com/jenetics/jpx/issues/155): Improved `GPX.Reader` and `GPX.Writer` classes.
* [#158](https://github.com/jenetics/jpx/issues/158): Add XML `Document` reader/writer methods.
```java
final GPX gpx = ...;

final Document doc = XMLProvider.provider()
    .documentBuilderFactory()
    .newDocumentBuilder()
    .newDocument();

// The GPX data are written to the empty `doc` object.
GPX.Writer.DEFAULT.write(gpx, new DOMResult(doc));
```

#### Bugs

* [#151](https://github.com/jenetics/jpx/issues/151): `Double`'s being written as exponents in GPX file.
* [#152](https://github.com/jenetics/jpx/issues/152): `LocationFormatter::parse` method is not thread-safe.
