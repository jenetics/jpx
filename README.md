# JPX - Java GPX library

*First version will be released soon*

This is a Java library for creating, reading and writing GPS data in [GPX](http://www.topografix.com/GPX) format. It implements version [1.1](http://www.topografix.com/GPX/1/1/) of the GPX format.

## Examples

**Creating new GPX object with 3 track-points**

    final GPX gpx = GPX.builder()
        .addTrack(track -> track
            .addSegment(segment -> segment
                .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
                .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
                .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
        .build();

**Writing GPX object to a file**

    // Writing "pretty" GPX file.
    GPX.write(gpx, "    ", "gpx.xml");

*GPX output*

    <?xml version="1.0" encoding="UTF-8"?>
    <gpx xmlns="http://www.topografix.com/GPX/1/1" version="1.1" creator="JPX - Java GPX library">
        <trk>
            <trkseg>
                <trkpt lat="48.2081743" lon="16.3738189">
                    <ele>160.0</ele>
                </trkpt>
                <trkpt lat="48.2081743" lon="16.3738189">
                    <ele>161.0</ele>
                </trkpt>
                <trkpt lat="48.2081743" lon="16.3738189">
                    <ele>162.0</ele>
                </trkpt>
            </trkseg>
        </trk>
    </gpx>

**Reading GPX object from file**

This example writes a given `GPX` object to a file, reads it again and prints the `WayPoint`s of all tracks and all track-segments to the console.

    GPX.write(gpx, "gpx.xml");
    GPX.read("gpx.xml".tracks()
        .flatMap(Track::segments)
        .flatMap(TrackSegment::points)
        .forEach(System.out::println);

*Console output*

    [lat=48.2081743, lon=48.2081743]
    [lat=48.2081743, lon=48.2081743]
    [lat=48.2081743, lon=48.2081743]

