## Release notes

### [3.2.1](https://github.com/jenetics/jpx/releases/tag/v3.2.1)

#### Improvements

* [#186](https://github.com/jenetics/jpx/issues/186): LENIENT mode allows GPX tags without creator attributes.

### [3.2.0](https://github.com/jenetics/jpx/releases/tag/v3.2.0)

#### Improvements

* [#183](https://github.com/jenetics/jpx/issues/183): Update Gradle to 8.11 and improve build scripts.
* [#181](https://github.com/jenetics/jpx/pull/181): Update code examples in README.

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

### [2.2.0](https://github.com/jenetics/jpx/releases/tag/v2.2.0)

#### Improvements

* [#72](https://github.com/jenetics/jpx/issues/72): Parsing of [ISO 6709](https://en.wikipedia.org/wiki/ISO_6709) location strings (thanks to [bunkenburg](https://github.com/bunkenburg)). This also contains fixes in the ISO 6709 location formatter.

### [2.1.0](https://github.com/jenetics/jpx/releases/tag/v2.1.0)

#### Improvements

* [#128](https://github.com/jenetics/jpx/issues/128): Added Java Module System support ([Adito5393](https://github.com/Adito5393)).
* [#132](https://github.com/jenetics/jpx/issues/132): Convert Gradle build scripts from Groovy to Kotlin.
* [#134](https://github.com/jenetics/jpx/issues/134): Make distance calculation more stable.

#### Bugs

* [#129](https://github.com/jenetics/jpx/issues/129): Fixed spelling of build script name ([marcusfey](https://github.com/marcusfey)).

### [2.0.0](https://github.com/jenetics/jpx/releases/tag/v2.0.0)

#### Improvements

* [#68](https://github.com/jenetics/jpx/issues/68): Remove deprecated methods.
* [#113](https://github.com/jenetics/jpx/issues/113): Upgrade to Java 11.

### [1.7.0](https://github.com/jenetics/jpx/releases/tag/v1.7.0)

#### Improvements

* [#116](https://github.com/jenetics/jpx/issues/116): Create `XMLProvider` SPI, which allows to change the used XML implementation. (Implemented by [avianey](https://github.com/avianey).)

### [1.6.1](https://github.com/jenetics/jpx/releases/tag/v1.6.1)

#### Bugs

* [#105](https://github.com/jenetics/jpx/issues/105): Location dependent formatting in `LocationFormatter`. (Fixed by [Segelzwerg](https://github.com/Segelzwerg).)
* [#108](https://github.com/jenetics/jpx/issues/108): Make library compileable with Java 13.
* [#110](https://github.com/jenetics/jpx/issues/110): Fix `Bounds.toBounds` collector. Wrong results for only _negative_ points.

### [1.6.0](https://github.com/jenetics/jpx/releases/tag/v1.6.0)

#### Improvements

* [#87](https://github.com/jenetics/jpx/issues/87): Consistent exception handling. Invalid GPX files, read from file or input stream throwing now an `InvalidObjectException`. (Implemented by [Segelzwerg](https://github.com/Segelzwerg).)
* [#97](https://github.com/jenetics/jpx/issues/97): Implement `Bounds.toBounds()` collector. This collector finds the bounds of a given `Point` stream.
* [#102](https://github.com/jenetics/jpx/issues/102): Add `Point.getInstant` method.


### [1.5.3](https://github.com/jenetics/jpx/releases/tag/v1.5.3)

#### Bugs

* [#94](https://github.com/jenetics/jpx/issues/94): NPE for empty 'extensions' XML-document.

### [1.5.2](https://github.com/jenetics/jpx/releases/tag/v1.5.2)

#### Bugs

* [#86](https://github.com/jenetics/jpx/issues/86): Fix parsing of GPX `time` fields.

### [1.5.1](https://github.com/jenetics/jpx/releases/tag/v1.5.1)

#### Bugs

* [#82](https://github.com/jenetics/jpx/issues/82): Fix parsing of GPX `extensions`.

### [1.5.0](https://github.com/jenetics/jpx/releases/tag/v1.5.0)

#### Enhancement

* [#59](https://github.com/jenetics/jpx/issues/59): Add GPX `extensions`.

#### Bugs

* [#73](https://github.com/jenetics/jpx/issues/73): Fix alerts found by [LGTM](https://lgtm.com/projects/g/jenetics/jpx/alerts?mode=list).
* [#77](https://github.com/jenetics/jpx/issues/77): Fix handling of XML comments.


### [1.4.0](https://github.com/jenetics/jpx/releases/tag/v1.4.0)

#### Enhancement

* [#65](https://github.com/jenetics/jpx/issues/65): Make it compatible with JSR-173 stax-api 1.0.1.
* [#70](https://github.com/jenetics/jpx/issues/70): ISO 6709 string representation for GPS coordinate

### [1.3.0](https://github.com/jenetics/jpx/releases/tag/v1.3.0)

#### Enhancement

* [#25](https://github.com/jenetics/jpx/issues/25): Read GPX version 1.0 files.
* [#54](https://github.com/jenetics/jpx/issues/54): Create GPX object from XML-string.

#### Bug fixes

* [#57](https://github.com/jenetics/jpx/issues/57): XMLStreamException: Unexpected element <extensions>.


### [1.2.3](https://github.com/jenetics/jpx/releases/tag/v1.2.3)

#### Bug fixes

* [#49](https://github.com/jenetics/jpx/issues/49): Improve thrown exceptions for invalid files and let the _lenient_ read ignore unknown XML tags.
* [#51](https://github.com/jenetics/jpx/issues/51): GPX reader does not handle XML comments correctly.

### [1.2.2](https://github.com/jenetics/jpx/releases/tag/v1.2.2)

#### Bug fixes

* [#40](https://github.com/jenetics/jpx/issues/40): Improve/fix `equals` and `hashCode` methods.
* [#43](https://github.com/jenetics/jpx/issues/43): Improve Java serialization. Make it smaller, faster and more stable [Serialization proxy](https://dzone.com/articles/serialization-proxy-pattern). This change breaks the existing Java serialization.
* [#45](https://github.com/jenetics/jpx/issues/45): Update internal XML reader/writer classes; cleanup of the XML serialization code.

### [1.2.1](https://github.com/jenetics/jpx/releases/tag/v1.2.1)

#### Bug fixes

* [#38](https://github.com/jenetics/jpx/issues/38): Erroneous marshalling of `author` metadata.

### [1.2.0](https://github.com/jenetics/jpx/releases/tag/v1.2.0)

#### Improvements

* [#35](https://github.com/jenetics/jpx/issues/35): Additional length units.

### [1.1.2](https://github.com/jenetics/jpx/releases/tag/v1.1.2)

#### Bug fixes
* [#28](https://github.com/jenetics/jpx/issues/28): Fix 'WayPoint.toString' method.

### [1.1.1](https://github.com/jenetics/jpx/releases/tag/v1.1.1)

#### Improvements
* [#26](https://github.com/jenetics/jpx/issues/26): Define stable module name. `io.jenetics.jpx`.

### [1.1.0](https://github.com/jenetics/jpx/releases/tag/v1.1.0)

#### Bug fixes
* [#20](https://github.com/jenetics/jpx/issues/20): Order of links in `Track`, `Route` and `Metadata` changes object equality.

#### Improvements
* [#3](https://github.com/jenetics/jpx/issues/3): Add methods for doing way-point filtering and manipulation in a functional way.
* [#10](https://github.com/jenetics/jpx/issues/10): Add *lenient* mode for reading GPX files. Reading a GPX file in *lenient* mode simply skips invalid way-points.
* [#17](https://github.com/jenetics/jpx/issues/17): Implement filter method (in `Filters` class) for removing empty GPX elements.
* [#18](https://github.com/jenetics/jpx/issues/18): Improve error handling when creating empty way-points.
* [#22](https://github.com/jenetics/jpx/issues/22): Implement `Filter`s for merging `TrackSegment`s and `Track`s.

### [1.0.1](https://github.com/jenetics/jpx/releases/tag/v1.0.1)

#### Bug fixes
* [#5](https://github.com/jenetics/jpx/issues/5): Fix exception handling for empty XML elements: e.g. `<ele/>`.
* [#15](https://github.com/jenetics/jpx/issues/15): Fix NPE when creating `Copyright` object with `null` license string.

#### Improvements
* [#6](https://github.com/jenetics/jpx/issues/6): Improve error handling for invalid GPX files.
