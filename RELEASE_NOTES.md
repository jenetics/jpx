## Release notes

### [1.2.1](https://github.com/jenetics/jpx/releases/tag/v1.2.1)

* [#38](https://github.com/jenetics/jpx/issues/38): Errornous marshalling of `author` metadata.

### [1.2.0](https://github.com/jenetics/jpx/releases/tag/v1.2.0)

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
