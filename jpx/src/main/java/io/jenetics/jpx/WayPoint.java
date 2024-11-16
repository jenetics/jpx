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
package io.jenetics.jpx;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static io.jenetics.jpx.Format.toDurationString;
import static io.jenetics.jpx.Format.toIntString;
import static io.jenetics.jpx.Length.Unit.METER;
import static io.jenetics.jpx.Lists.copyOf;
import static io.jenetics.jpx.Lists.copyTo;
import static io.jenetics.jpx.Speed.Unit.METERS_PER_SECOND;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.w3c.dom.Document;

import io.jenetics.jpx.GPX.Version;

/**
 * A {@code WayPoint} represents a way-point, point of interest, or named
 * feature on a map.
 * <p>
 * Creating a {@code WayPoint}:
 * <pre>{@code
 * final WayPoint point = WayPoint.builder()
 *     .lat(48.2081743).lon(16.3738189).ele(160)
 *     .build();
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.0
 * @since 1.0
 */
public final class WayPoint implements Point, Serializable {

	@Serial
	private static final long serialVersionUID = 2L;

	private final Latitude _latitude;
	private final Longitude _longitude;

	private final Length _elevation;
	private final Speed _speed;
	private final Instant _time;
	private final Degrees _magneticVariation;
	private final Length _geoidHeight;
	private final String _name;
	private final String _comment;
	private final String _description;
	private final String _source;
	private final List<Link> _links;
	private final String _symbol;
	private final String _type;
	private final Fix _fix;
	private final UInt _sat;
	private final Double _hdop;
	private final Double _vdop;
	private final Double _pdop;
	private final Duration _ageOfGPSData;
	private final DGPSStation _dgpsID;
	private final Degrees _course;
	private final Document _extensions;

	/**
	 * Create a new way-point with the given parameter.
	 *
	 * @param latitude the latitude of the point, WGS84 datum (mandatory)
	 * @param longitude the longitude of the point, WGS84 datum (mandatory)
	 * @param elevation the elevation (in meters) of the point (optional)
	 * @param speed the current GPS speed (optional)
	 * @param time creation/modification timestamp for element. Conforms to ISO
	 *        8601 specification for date/time representation. Fractional seconds
	 *        are allowed for millisecond timing in tracklogs. (optional)
	 * @param magneticVariation the magnetic variation at the point (optional)
	 * @param geoidHeight height (in meters) of geoid (mean sea level) above
	 *        WGS84 earth ellipsoid. As defined in NMEA GGA message. (optional)
	 * @param name the GPS name of the way-point. This field will be transferred
	 *        to and from the GPS. GPX does not place restrictions on the length
	 *        of this field or the characters contained in it. It is up to the
	 *        receiving application to validate the field before sending it to
	 *        the GPS. (optional)
	 * @param comment GPS way-point comment. Sent to GPS as comment (optional)
	 * @param description a text description of the element. Holds additional
	 *        information about the element intended for the user, not the GPS.
	 *        (optional)
	 * @param source source of data. Included to give user some idea of
	 *        reliability and accuracy of data. "Garmin eTrex", "USGS quad
	 *        Boston North", e.g. (optional)
	 * @param links links to additional information about the way-point. May be
	 *        empty, but not {@code null}.
	 * @param symbol text of GPS symbol name. For interchange with other
	 *        programs, use the exact spelling of the symbol as displayed on the
	 *        GPS. If the GPS abbreviates words, spell them out. (optional)
	 * @param type type (classification) of the way-point (optional)
	 * @param fix type of GPX fix (optional)
	 * @param sat number of satellites used to calculate the GPX fix (optional)
	 * @param hdop horizontal dilution of precision (optional)
	 * @param vdop vertical dilution of precision (optional)
	 * @param pdop position dilution of precision. (optional)
	 * @param ageOfGPSData number of seconds since last DGPS update (optional)
	 * @param dgpsID ID of DGPS station used in differential correction (optional)
	 * @param course the Instantaneous course at the point
	 * @param extensions the XML extensions document
	 * @throws NullPointerException if the {@code latitude} or {@code longitude}
	 *         is {@code null}
	 */
	private WayPoint(
		final Latitude latitude,
		final Longitude longitude,
		final Length elevation,
		final Speed speed,
		final Instant time,
		final Degrees magneticVariation,
		final Length geoidHeight,
		final String name,
		final String comment,
		final String description,
		final String source,
		final List<Link> links,
		final String symbol,
		final String type,
		final Fix fix,
		final UInt sat,
		final Double hdop,
		final Double vdop,
		final Double pdop,
		final Duration ageOfGPSData,
		final DGPSStation dgpsID,
		final Degrees course,
		final Document extensions
	) {
		_latitude = requireNonNull(latitude);
		_longitude = requireNonNull(longitude);

		_elevation = elevation;
		_speed = speed;
		_time = time;
		_magneticVariation = magneticVariation;
		_geoidHeight = geoidHeight;
		_name = name;
		_comment = comment;
		_description = description;
		_source = source;
		_links = copyOf(links);
		_symbol = symbol;
		_type = type;
		_fix = fix;
		_sat = sat;
		_hdop = hdop;
		_vdop = vdop;
		_pdop = pdop;
		_ageOfGPSData = ageOfGPSData;
		_dgpsID = dgpsID;
		_course = course;
		_extensions = extensions;
	}

	@Override
	public Latitude getLatitude() {
		return _latitude;
	}

	@Override
	public Longitude getLongitude() {
		return _longitude;
	}

	@Override
	public Optional<Length> getElevation() {
		return Optional.ofNullable(_elevation);
	}

	/**
	 * The current GPS speed.
	 *
	 * @return the current GPS speed
	 */
	public Optional<Speed> getSpeed() {
		return Optional.ofNullable(_speed);
	}

	@Override
	public Optional<Instant> getTime() {
		return Optional.ofNullable(_time);
	}

	/**
	 * The magnetic variation at the point.
	 *
	 * @return the magnetic variation at the point
	 */
	public Optional<Degrees> getMagneticVariation() {
		return Optional.ofNullable(_magneticVariation);
	}

	/**
	 * The height (in meters) of geoid (mean sea level) above WGS84 earth
	 * ellipsoid. As defined in NMEA GGA message.
	 *
	 * @return the height (in meters) of geoid (mean sea level) above WGS84
	 *         earth ellipsoid
	 */
	public Optional<Length> getGeoidHeight() {
		return Optional.ofNullable(_geoidHeight);
	}

	/**
	 * The GPS name of the way-point. This field will be transferred to and from
	 * the GPS. GPX does not place restrictions on the length of this field or
	 * the characters contained in it. It is up to the receiving application to
	 * validate the field before sending it to the GPS.
	 *
	 * @return the GPS name of the way-point
	 */
	public Optional<String> getName() {
		return Optional.ofNullable(_name);
	}

	/**
	 * The GPS way-point comment.
	 *
	 * @return the GPS way-point comment
	 */
	public Optional<String> getComment() {
		return Optional.ofNullable(_comment);
	}

	/**
	 * Return a text description of the element. Holds additional information
	 * about the element intended for the user, not the GPS.
	 *
	 * @return a text description of the element
	 */
	public Optional<String> getDescription() {
		return Optional.ofNullable(_description);
	}

	/**
	 * Return the source of data. Included to give user some idea of reliability
	 * and accuracy of data. "Garmin eTrex", "USGS quad Boston North", e.g.
	 *
	 * @return the source of the data
	 */
	public Optional<String> getSource() {
		return Optional.ofNullable(_source);
	}

	/**
	 * Return the links to additional information about the way-point.
	 *
	 * @return the links to additional information about the way-point
	 */
	public List<Link> getLinks() {
		return _links;
	}

	/**
	 * Return the text of GPS symbol name. For interchange with other programs,
	 * use the exact spelling of the symbol as displayed on the GPS. If the GPS
	 * abbreviates words, spell them out.
	 *
	 * @return the text of GPS symbol name
	 */
	public Optional<String> getSymbol() {
		return Optional.ofNullable(_symbol);
	}

	/**
	 * Return the type (classification) of the way-point.
	 *
	 * @return the type (classification) of the way-point
	 */
	public Optional<String> getType() {
		return Optional.ofNullable(_type);
	}

	/**
	 * Return the type of GPX fix.
	 *
	 * @return the type of GPX fix
	 */
	public Optional<Fix> getFix() {
		return Optional.ofNullable(_fix);
	}

	/**
	 * Return the number of satellites used to calculate the GPX fix.
	 *
	 * @return the number of satellites used to calculate the GPX fix
	 */
	public Optional<UInt> getSat() {
		return Optional.ofNullable(_sat);
	}

	/**
	 * Return the horizontal dilution of precision.
	 *
	 * @return the horizontal dilution of precision
	 */
	public Optional<Double> getHdop() {
		return Optional.ofNullable(_hdop);
	}

	/**
	 * Return the vertical dilution of precision.
	 *
	 * @return the vertical dilution of precision
	 */
	public Optional<Double> getVdop() {
		return Optional.ofNullable(_vdop);
	}

	/**
	 * Return the position dilution of precision.
	 *
	 * @return the position dilution of precision
	 */
	public Optional<Double> getPdop() {
		return Optional.ofNullable(_pdop);
	}

	/**
	 * Return the number of seconds since the last DGPS update.
	 *
	 * @return number of seconds since last DGPS update
	 */
	public Optional<Duration> getAgeOfGPSData() {
		return Optional.ofNullable(_ageOfGPSData);
	}

	/**
	 * Return the ID of DGPS station used in differential correction.
	 *
	 * @return the ID of DGPS station used in differential correction
	 */
	public Optional<DGPSStation> getDGPSID() {
		return Optional.ofNullable(_dgpsID);
	}

	/**
	 * Return the instantaneous course at the point. This property is only
	 * available when you read GPX files version 1.0. In version 1.1 this field
	 * is always {@link Optional#empty()}.
	 *
	 * @since 1.3
	 *
	 * @return the instantaneous course at the point
	 */
	public Optional<Degrees> getCourse() {
		return Optional.ofNullable(_course);
	}

	/**
	 * Return the (cloned) extensions document. The root element of the returned
	 * document has the name {@code extensions}.
	 * <pre>{@code
	 * <extensions>
	 *     ...
	 * </extensions>
	 * }</pre>
	 *
	 * @since 1.5
	 *
	 * @return the extensions document
	 * @throws org.w3c.dom.DOMException if the document could not be cloned,
	 *         because of an erroneous XML configuration
	 */
	public Optional<Document> getExtensions() {
		return Optional.ofNullable(_extensions).map(XML::clone);
	}

	/**
	 * Convert the <em>immutable</em> way-point object into a <em>mutable</em>
	 * builder initialized with the current way-point values.
	 *
	 * @since 1.1
	 *
	 * @return a new way-point builder initialized with the values of {@code this}
	 *         way-point
	 */
	public Builder toBuilder() {
		return builder()
			.lat(_latitude)
			.lon(_longitude)
			.ele(_elevation)
			.speed(_speed)
			.time(_time)
			.magvar(_magneticVariation)
			.geoidheight(_geoidHeight)
			.name(_name)
			.cmt(_comment)
			.desc(_description)
			.src(_source)
			.links(_links)
			.sym(_symbol)
			.type(_type)
			.fix(_fix)
			.sat(_sat)
			.hdop(_hdop)
			.vdop(_vdop)
			.pdop(_pdop)
			.ageofdgpsdata(_ageOfGPSData)
			.dgpsid(_dgpsID)
			.course(_course)
			.extensions(_extensions);
	}

	@Override
	public int hashCode() {
		return hash(
			_latitude,
			_longitude,
			_elevation,
			_speed,
			Objects.hashCode(_time),
			_magneticVariation,
			_geoidHeight,
			_name,
			_comment,
			_description,
			_source,
			Lists.hashCode(_links),
			_symbol,
			_type,
			_fix,
			_sat,
			_hdop,
			_vdop,
			_pdop,
			_ageOfGPSData,
			_dgpsID,
			_course
		);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof WayPoint wp &&
			Objects.equals(wp._latitude, _latitude) &&
			Objects.equals(wp._longitude, _longitude) &&
			Objects.equals(wp._elevation, _elevation) &&
			Objects.equals(wp._speed, _speed) &&
			Objects.equals(wp._time, _time) &&
			Objects.equals(wp._magneticVariation, _magneticVariation) &&
			Objects.equals(wp._geoidHeight, _geoidHeight) &&
			Objects.equals(wp._name, _name) &&
			Objects.equals(wp._comment, _comment) &&
			Objects.equals(wp._description, _description) &&
			Objects.equals(wp._source, _source) &&
			Lists.equals(wp._links, _links) &&
			Objects.equals(wp._symbol, _symbol) &&
			Objects.equals(wp._type, _type) &&
			Objects.equals(wp._fix, _fix) &&
			Objects.equals(wp._sat, _sat) &&
			Objects.equals(wp._hdop, _hdop) &&
			Objects.equals(wp._vdop, _vdop) &&
			Objects.equals(wp._pdop, _pdop) &&
			Objects.equals(wp._ageOfGPSData, _ageOfGPSData) &&
			Objects.equals(wp._dgpsID, _dgpsID) &&
			Objects.equals(wp._course, _course);
	}

	@Override
	public String toString() {
		return _elevation != null
			? String.format("[lat=%s, lon=%s, ele=%s]",
				_latitude, _longitude, _elevation)
			: String.format("[lat=%s, lon=%s]",
				_latitude, _longitude);
	}


	/**
	 * Builder for creating a way-point with different parameters.
	 * <p>
	 * Creating a {@code WayPoint}:
	 * <pre>{@code
	 * final WayPoint point = WayPoint.builder()
	 *     .lat(48.2081743).lon(16.3738189).ele(160)
	 *     .build();
	 * }</pre>
	 *
	 * @see  #builder()
	 */
	public static final class Builder {
		private Latitude _latitude;
		private Longitude _longitude;

		private Length _elevation;
		private Speed _speed;
		private Instant _time;
		private Degrees _magneticVariation;
		private Length _geoidHeight;
		private String _name;
		private String _comment;
		private String _description;
		private String _source;
		private final List<Link> _links = new ArrayList<>();
		private String _symbol;
		private String _type;
		private Fix _fix;
		private UInt _sat;
		private Double _hdop;
		private Double _vdop;
		private Double _pdop;
		private Duration _ageOfDGPSData;
		private DGPSStation _dgpsID;
		private Degrees _course;
		private Document _extensions;

		private Builder() {
		}

		/**
		 * Set the latitude value of the way-point.
		 *
		 * @param latitude the new latitude value
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given value is {@code null}
		 */
		public Builder lat(final Latitude latitude) {
			_latitude = requireNonNull(latitude);
			return this;
		}

		/**
		 * Set the latitude value of the way-point.
		 *
		 * @param degrees the new latitude value
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws IllegalArgumentException if the given value is not within the
		 *         range of {@code [-90..90]}
		 */
		public Builder lat(final double degrees) {
			return lat(Latitude.ofDegrees(degrees));
		}

		/**
		 * Return the current latitude value.
		 *
		 * @since 1.1
		 *
		 * @return the current latitude value
		 */
		public Latitude lat() {
			return _latitude;
		}

		/**
		 * Set the longitude value of the way-point.
		 *
		 * @param longitude the new longitude value
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given value is {@code null}
		 */
		public Builder lon(final Longitude longitude) {
			_longitude = requireNonNull(longitude);
			return this;
		}

		/**
		 * Set the longitude value of the way-point.
		 *
		 * @param degrees the new longitude value
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws IllegalArgumentException if the given value is not within the
		 *         range of {@code [-180..180]}
		 */
		public Builder lon(final double degrees) {
			return lon(Longitude.ofDegrees(degrees));
		}

		/**
		 * Return the current longitude value.
		 *
		 * @since 1.1
		 *
		 * @return the current longitude value
		 */
		public Longitude lon() {
			return _longitude;
		}

		/**
		 * Set the elevation  of the point.
		 *
		 * @param elevation the elevation of the point
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder ele(final Length elevation) {
			_elevation = elevation;
			return this;
		}

		/**
		 * Set the elevation (in meters) of the point.
		 *
		 * @param meters the elevation of the point, in meters
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder ele(final double meters) {
			_elevation = Length.of(meters, METER);
			return this;
		}

		/**
		 * Set the elevation of the point.
		 *
		 * @param elevation the elevation of the point
		 * @param unit the length unit
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder ele(final double elevation, final Length.Unit unit) {
			_elevation = Length.of(elevation, unit);
			return this;
		}

		/**
		 * Return the current elevation value.
		 *
		 * @since 1.1
		 *
		 * @return the current elevation value
		 */
		public Optional<Length> ele() {
			return Optional.ofNullable(_elevation);
		}

		/**
		 * Set the current GPS speed.
		 *
		 * @param speed the current GPS speed
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder speed(final Speed speed) {
			_speed = speed;
			return this;
		}

		/**
		 * Set the current GPS speed
		 *
		 * @param speed the current speed value
		 * @param unit the speed unit
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder speed(final double speed, final Speed.Unit unit) {
			return speed(Speed.of(speed, unit));
		}

		/**
		 * Set the current GPS speed.
		 *
		 * @param meterPerSecond the current GPS speed in m/s
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder speed(final double meterPerSecond) {
			_speed = Speed.of(meterPerSecond, METERS_PER_SECOND);
			return this;
		}

		/**
		 * Return the current speed value.
		 *
		 * @since 1.1
		 *
		 * @return the current speed value
		 */
		public Optional<Speed> speed() {
			return Optional.ofNullable(_speed);
		}

		/**
		 * Set the creation/modification timestamp for the point.
		 *
		 * @param instant the instant of the way-point
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder time(final Instant instant) {
			_time = instant;
			return this;
		}

		/**
		 * Set the creation/modification timestamp for the point.
		 *
		 * @param millis the instant of the way-point
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder time(final long millis) {
			_time = Instant.ofEpochMilli(millis);
			return this;
		}

		/**
		 * Return the current time value.
		 *
		 * @return the current time value
		 */
		public Optional<Instant> time() {
			return Optional.ofNullable(_time);
		}

		/**
		 * Set the magnetic variation at the point.
		 *
		 * @param variation the magnetic variation
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder magvar(final Degrees variation) {
			_magneticVariation = variation;
			return this;
		}

		/**
		 * Set the magnetic variation at the point.
		 *
		 * @param degree the magnetic variation
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws IllegalArgumentException if the give value is not within the
		 *         range of {@code [0..360]}
		 */
		public Builder magvar(final double degree) {
			_magneticVariation = Degrees.ofDegrees(degree);
			return this;
		}

		/**
		 * Return the current magnetic variation value.
		 *
		 * @since 1.1
		 *
		 * @return the current magnetic variation value
		 */
		public Optional<Degrees> magvar() {
			return Optional.ofNullable(_magneticVariation);
		}

		/**
		 * Set the height (in meters) of geoid (mean sea level) above WGS84 earth
		 * ellipsoid. As defined in NMEA GGA message.
		 *
		 * @param height the height (in meters) of geoid (mean sea level)
		 *        above WGS84 earth ellipsoid
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder geoidheight(final Length height) {
			_geoidHeight = height;
			return this;
		}

		/**
		 * Set the height (in meters) of geoid (mean sea level) above WGS84 earth
		 * ellipsoid. As defined in NMEA GGA message.
		 *
		 * @param meter the height (in meters) of geoid (mean sea level)
		 *        above WGS84 earth ellipsoid
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder geoidheight(final double meter) {
			_geoidHeight = Length.of(meter, METER);
			return this;
		}

		/**
		 * Set the height of geoid (mean sea level) above WGS84 earth ellipsoid.
		 * As defined in NMEA GGA message.
		 *
		 * @param length the height of geoid (mean sea level) above WGS84 earth
		 *        ellipsoid
		 * @param unit the length unit
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder geoidheight(final double length, Length.Unit unit) {
			_geoidHeight = Length.of(length, unit);
			return this;
		}

		/**
		 * Return the current height of geoid value.
		 *
		 * @since 1.1
		 *
		 * @return the current height of geoid value
		 */
		public Optional<Length> geoidheight() {
			return Optional.ofNullable(_geoidHeight);
		}

		/**
		 * Set the GPS name of the way-point. This field will be transferred to
		 * and from the GPS. GPX does not place restrictions on the length of
		 * this field or the characters contained in it. It is up to the
		 * receiving application to validate the field before sending it to the
		 * GPS.
		 *
		 * @param name the GPS name of the way-point
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder name(final String name) {
			_name = name;
			return this;
		}

		/**
		 * Return the current name value.
		 *
		 * @since 1.1
		 *
		 * @return the current name value
		 */
		public Optional<String> name() {
			return Optional.ofNullable(_name);
		}

		/**
		 * Set the GPS way-point comment.
		 *
		 * @param comment the GPS way-point comment.
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder cmt(final String comment) {
			_comment = comment;
			return this;
		}

		/**
		 * Return the current comment value.
		 *
		 * @since 1.1
		 *
		 * @return the current comment value
		 */
		public Optional<String> cmt() {
			return Optional.ofNullable(_comment);
		}

		/**
		 * Set the GPS way-point description.
		 *
		 * @param description the GPS way-point description.
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder desc(final String description) {
			_description = description;
			return this;
		}

		/**
		 * Return the current description value.
		 *
		 * @since 1.1
		 *
		 * @return the current description value
		 */
		public Optional<String> desc() {
			return Optional.ofNullable(_description);
		}

		/**
		 * Set the GPS way-point source.
		 *
		 * @param source the GPS way-point source.
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder src(final String source) {
			_source = source;
			return this;
		}

		/**
		 * Return the current source value.
		 *
		 * @since 1.1
		 *
		 * @return the current source value
		 */
		public Optional<String> src() {
			return Optional.ofNullable(_source);
		}

		/**
		 * Set the links to additional information about the way-point. The link
		 * list may be {@code null}.
		 *
		 * @param links the links to additional information about the way-point
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if one of the links in the list is
		 *         {@code null}
		 */
		public Builder links(final List<Link> links) {
			copyTo(links, _links);
			return this;
		}

		/**
		 * Set the links to external information about the way-point.
		 *
		 * @param link the links to external information about the way-point.
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder addLink(final Link link) {
			if (link != null) {
				_links.add(link);
			}
			return this;
		}

		/**
		 * Set the links to external information about the way-point.
		 *
		 * @param href the links to external information about the way-point.
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws IllegalArgumentException if the given {@code href} is not a
		 *         valid URL
		 */
		public Builder addLink(final String href) {
			if (href != null) {
				_links.add(Link.of(href));
			}
			return this;
		}

		/**
		 * Return the current links. The returned link list is mutable.
		 *
		 * @since 1.1
		 *
		 * @return the current links
		 */
		public List<Link> links() {
			return new NonNullList<>(_links);
		}

		/**
		 * Set the text of GPS symbol name. For interchange with other programs,
		 * use the exact spelling of the symbol as displayed on the GPS. If the
		 * GPS abbreviates words, spell them out.
		 *
		 * @param symbol the text of GPS symbol name
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder sym(final String symbol) {
			_symbol = symbol;
			return this;
		}

		/**
		 * Return the current symbol value.
		 *
		 * @since 1.1
		 *
		 * @return the current symbol value
		 */
		public Optional<String> sym() {
			return Optional.ofNullable(_symbol);
		}

		/**
		 * Set the type (classification) of the way-point.
		 *
		 * @param type the type (classification) of the way-point
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder type(final String type) {
			_type = type;
			return this;
		}

		/**
		 * Return the current type value.
		 *
		 * @since 1.1
		 *
		 * @return the current type value
		 */
		public Optional<String> type() {
			return Optional.ofNullable(_type);
		}

		/**
		 * Set the type of GPX fix.
		 *
		 * @param fix the type of GPX fix
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder fix(final Fix fix) {
			_fix = fix;
			return this;
		}

		/**
		 * Set the type of GPX fix.
		 *
		 * @param fix the type of GPX fix
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws IllegalArgumentException if the fix value is not one of the
		 *         following values: [none, 2d, 3d, dgps, pps]
		 */
		public Builder fix(final String fix) {
			_fix = Fix.parse(fix);
			return this;
		}

		/**
		 * Return the current GPX fix value.
		 *
		 * @since 1.1
		 *
		 * @return the current GPX fix value
		 */
		public Optional<Fix> fix() {
			return Optional.ofNullable(_fix);
		}

		/**
		 * Set the number of satellites used to calculate the GPX fix.
		 *
		 * @param sat the number of satellites used to calculate the GPX fix
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder sat(final UInt sat) {
			_sat = sat;
			return this;
		}

		/**
		 * Set the number of satellites used to calculate the GPX fix.
		 *
		 * @param sat the number of satellites used to calculate the GPX fix
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws IllegalArgumentException if the given {@code value} is smaller
		 *         than zero
		 */
		public Builder sat(final int sat) {
			_sat = UInt.of(sat);
			return this;
		}

		/**
		 * Return the current number of satelites.
		 *
		 * @since 1.1
		 *
		 * @return the current number of satelites
		 */
		public Optional<UInt> sat() {
			return Optional.ofNullable(_sat);
		}

		/**
		 * Set the horizontal dilution of precision.
		 *
		 * @param hdop the horizontal dilution of precision
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder hdop(final Double hdop) {
			_hdop = hdop;
			return this;
		}

		/**
		 * Return the current horizontal dilution.
		 *
		 * @since 1.1
		 *
		 * @return the current horizontal dilution
		 */
		public Optional<Double> hdop() {
			return Optional.ofNullable(_hdop);
		}

		/**
		 * Set the vertical dilution of precision.
		 *
		 * @param vdop the vertical dilution of precision
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder vdop(final Double vdop) {
			_vdop = vdop;
			return this;
		}

		/**
		 * Return the current vertical dilution.
		 *
		 * @since 1.1
		 *
		 * @return the current vertical dilution
		 */
		public Optional<Double> vdop() {
			return Optional.ofNullable(_vdop);
		}

		/**
		 * Set the position dilution of precision.
		 *
		 * @param pdop the position dilution of precision
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder pdop(final Double pdop) {
			_pdop = pdop;
			return this;
		}

		/**
		 * Return the current position dilution.
		 *
		 * @since 1.1
		 *
		 * @return the current position dilution
		 */
		public Optional<Double> pdop() {
			return Optional.ofNullable(_pdop);
		}

		/**
		 * Set the age since last DGPS update.
		 *
		 * @param age the age since last DGPS update
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder ageofdgpsdata(final Duration age) {
			_ageOfDGPSData = age;
			return this;
		}

		/**
		 * Set the number of seconds since the last DGPS update.
		 *
		 * @param seconds the age since last DGPS update
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder ageofdgpsdata(final double seconds) {
			_ageOfDGPSData = Duration.ofMillis((long)(seconds*1000));
			return this;
		}

		/**
		 * Return the current age since last DGPS update.
		 *
		 * @since 1.1
		 *
		 * @return the current age since last DGPS update
		 */
		public Optional<Duration> ageofdgpsdata() {
			return Optional.ofNullable(_ageOfDGPSData);
		}

		/**
		 * Set the ID of DGPS station used in differential correction.
		 *
		 * @param station the ID of DGPS station used in differential correction
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder dgpsid(final DGPSStation station) {
			_dgpsID = station;
			return this;
		}

		/**
		 * Set the ID of DGPS station used in differential correction.
		 *
		 * @param station the ID of DGPS station used in differential correction
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws IllegalArgumentException if the given station number is not in the
		 *         range of {@code [0..1023]}
		 */
		public Builder dgpsid(final int station) {
			_dgpsID = DGPSStation.of(station);
			return this;
		}

		/**
		 * Return the current the ID of DGPS station used in differential
		 * correction.
		 *
		 * @since 1.1
		 *
		 * @return the current the ID of DGPS station used in differential
		 *         correction
		 */
		public Optional<DGPSStation> dgpsid() {
			return Optional.ofNullable(_dgpsID);
		}

		/**
		 * Set the instantaneous course at the point.
		 *
		 * @since 1.3
		 *
		 * @param course the instantaneous course at the point
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder course(final Degrees course) {
			_course = course;
			return this;
		}

		/**
		 * Set the instantaneous course at the point.
		 *
		 * @since 1.3
		 *
		 * @param courseDegrees the instantaneous course at the point
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws IllegalArgumentException if the give value is not within the
		 *         range of {@code [0..360]}
		 */
		public Builder course(final double courseDegrees) {
			_course = Degrees.ofDegrees(courseDegrees);
			return this;
		}

		/**
		 * Return the instantaneous course at the point.
		 *
		 * @since 1.3
		 *
		 * @return the instantaneous course at the point.
		 */
		public Optional<Degrees> course() {
			return Optional.ofNullable(_course);
		}

		/**
		 * Sets the extensions object, which may be {@code null}. The root
		 * element of the extensions document must be {@code extensions}.
		 * <pre>{@code
		 * <extensions>
		 *     ...
		 * </extensions>
		 * }</pre>
		 *
		 * @since 1.5
		 *
		 * @param extensions the document
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws IllegalArgumentException if the root element is not the
		 *         an {@code extensions} node
		 */
		public Builder extensions(final Document extensions) {
			_extensions = XML.checkExtensions(extensions);
			return this;
		}

		/**
		 * Return the current extensions
		 *
		 * @since 1.5
		 *
		 * @return the extensions document
		 */
		public Optional<Document> extensions() {
			return Optional.ofNullable(_extensions);
		}

		/**
		 * Create a new way-point with the given latitude and longitude value.
		 *
		 * @param latitude the latitude of the way-point
		 * @param longitude the longitude of the way-point
		 * @return a newly created way-point
		 */
		public WayPoint build(final Latitude latitude, final Longitude longitude) {
			lat(latitude);
			lon(longitude);
			return build();
		}

		/**
		 * Create a new way-point with the given latitude and longitude value.
		 *
		 * @param latitude the latitude of the way-point
		 * @param longitude the longitude of the way-point
		 * @return a newly created way-point
		 */
		public WayPoint build(final double latitude, final double longitude) {
			return build(Latitude.ofDegrees(latitude), Longitude.ofDegrees(longitude));
		}

		/**
		 * Build a new way-point from the current builder state.
		 *
		 * @return a new way-point from the current builder state
		 * @throws IllegalStateException if the {@link WayPoint#getLatitude()}
		 *         or {@link WayPoint#getLongitude()} is {@code null} or has
		 *         not been set, respectively.
		 */
		public WayPoint build() {
			if (_latitude == null || _longitude == null) {
				throw new IllegalStateException(
					"Latitude and longitude value must be set " +
					"for creating a new 'WayPoint'."
				);
			}

			return new WayPoint(
				_latitude,
				_longitude,
				_elevation,
				_speed,
				_time,
				_magneticVariation,
				_geoidHeight,
				_name,
				_comment,
				_description,
				_source,
				_links,
				_symbol,
				_type,
				_fix,
				_sat,
				_hdop,
				_vdop,
				_pdop,
				_ageOfDGPSData,
				_dgpsID,
				_course,
				_extensions
			);
		}

	}

	/**
	 * Return a new {@code WayPoint} builder.
	 *
	 * @return a new {@code WayPoint} builder
	 */
	public static Builder builder() {
		return new Builder();
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new way-point with the given parameter.
	 *
	 * @since 1.5
	 *
	 * @param latitude the latitude of the point, WGS84 datum (mandatory)
	 * @param longitude the longitude of the point, WGS84 datum (mandatory)
	 * @param elevation the elevation (in meters) of the point (optional)
	 * @param speed the current GPS speed (optional)
	 * @param time creation/modification timestamp for an element. Conforms to ISO
	 *        8601 specification for date/time representation. Fractional seconds
	 *        are allowed for millisecond timing in tracklogs. (optional)
	 * @param magneticVariation the magnetic variation at the point (optional)
	 * @param geoidHeight height (in meters) of geoid (mean sea level) above
	 *        WGS84 earth ellipsoid. As defined in NMEA GGA message. (optional)
	 * @param name the GPS name of the way-point. This field will be transferred
	 *        to and from the GPS. GPX does not place restrictions on the length
	 *        of this field or the characters contained in it. It is up to the
	 *        receiving application to validate the field before sending it to
	 *        the GPS. (optional)
	 * @param comment GPS way-point comment. Sent to GPS as comment (optional)
	 * @param description a text description of the element. Holds additional
	 *        information about the element intended for the user, not the GPS.
	 *        (optional)
	 * @param source source of data. Included to give user some idea of
	 *        reliability and accuracy of data. "Garmin eTrex", "USGS quad
	 *        Boston North", e.g. (optional)
	 * @param links links to additional information about the way-point. Maybe
	 *        empty, but not {@code null}.
	 * @param symbol text of GPS symbol name. For interchange with other
	 *        programs, use the exact spelling of the symbol as displayed on the
	 *        GPS. If the GPS abbreviates words, spell them out. (optional)
	 * @param type type (classification) of the way-point (optional)
	 * @param fix type of GPX fix (optional)
	 * @param sat number of satellites used to calculate the GPX fix (optional)
	 * @param hdop horizontal dilution of precision (optional)
	 * @param vdop vertical dilution of precision (optional)
	 * @param pdop position dilution of precision. (optional)
	 * @param ageOfGPSData number of seconds since last DGPS update (optional)
	 * @param dgpsID ID of DGPS station used in differential correction (optional)
	 * @param course the Instantaneous course at the point
	 * @param extensions the extensions document
	 * @throws NullPointerException if the {@code latitude} or {@code longitude}
	 *         is {@code null}
	 * @return a new {@code WayPoint}
	 */
	public static WayPoint of(
		final Latitude latitude,
		final Longitude longitude,
		final Length elevation,
		final Speed speed,
		final Instant time,
		final Degrees magneticVariation,
		final Length geoidHeight,
		final String name,
		final String comment,
		final String description,
		final String source,
		final List<Link> links,
		final String symbol,
		final String type,
		final Fix fix,
		final UInt sat,
		final Double hdop,
		final Double vdop,
		final Double pdop,
		final Duration ageOfGPSData,
		final DGPSStation dgpsID,
		final Degrees course,
		final Document extensions
	) {
		return new WayPoint(
			latitude,
			longitude,
			elevation,
			speed,
			time,
			magneticVariation,
			geoidHeight,
			name,
			comment,
			description,
			source,
			links,
			symbol,
			type,
			fix,
			sat,
			hdop,
			vdop,
			pdop,
			ageOfGPSData,
			dgpsID,
			course,
			XML.extensions(XML.clone(extensions))
		);
	}

	/**
	 * Create a new {@code WayPoint} with the given {@code latitude} and
	 * {@code longitude} value.
	 *
	 * @param latitude the latitude of the point
	 * @param longitude the longitude of the point
	 * @return a new {@code WayPoint}
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static WayPoint of(
		final Latitude latitude,
		final Longitude longitude
	) {
		return new WayPoint(
			latitude,
			longitude,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null
		);
	}

	/**
	 * Create a new {@code WayPoint} with the given {@code latitude} and
	 * {@code longitude} value.
	 *
	 * @param latitudeDegree the latitude of the point
	 * @param longitudeDegree the longitude of the point
	 * @return a new {@code WayPoint}
	 * @throws IllegalArgumentException if the given latitude or longitude is not
	 *         in the valid range.
	 */
	public static WayPoint of(
		final double latitudeDegree,
		final double longitudeDegree
	) {
		return of(
			Latitude.ofDegrees(latitudeDegree),
			Longitude.ofDegrees(longitudeDegree)
		);
	}

	/**
	 * Create a new {@code WayPoint} with the given parameters.
	 *
	 * @param latitude the latitude of the point
	 * @param longitude the longitude of the point
	 * @param time the timestamp of the way-point
	 * @return a new {@code WayPoint}
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static WayPoint of(
		final Latitude latitude,
		final Longitude longitude,
		final Instant time
	) {
		return new WayPoint(
			latitude,
			longitude,
			null,
			null,
			time,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null
		);
	}

	/**
	 * Create a new {@code WayPoint} with the given parameters.
	 *
	 * @param latitudeDegree the latitude of the point
	 * @param longitudeDegree the longitude of the point
	 * @param timeEpochMilli the timestamp of the way-point
	 * @return a new {@code WayPoint}
	 * @throws IllegalArgumentException if one of the given arguments is invalid
	 */
	public static WayPoint of(
		final double latitudeDegree,
		final double longitudeDegree,
		final long timeEpochMilli
	) {
		return of(
			Latitude.ofDegrees(latitudeDegree),
			Longitude.ofDegrees(longitudeDegree),
			Instant.ofEpochMilli(timeEpochMilli)
		);
	}

	/**
	 * Create a new {@code WayPoint} with the given parameters.
	 *
	 * @param latitude the latitude of the point
	 * @param longitude the longitude of the point
	 * @param elevation the elevation of the point
	 * @param time the timestamp of the way-point
	 * @return a new {@code WayPoint}
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static WayPoint of(
		final Latitude latitude,
		final Longitude longitude,
		final Length elevation,
		final Instant time
	) {
		return new WayPoint(
			latitude,
			longitude,
			elevation,
			null,
			time,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null
		);
	}

	/**
	 * Create a new {@code WayPoint} with the given parameters.
	 *
	 * @param latitudeDegree the latitude of the point
	 * @param longitudeDegree the longitude of the point
	 * @param elevationMeter the elevation of the point
	 * @param timeEpochMilli the timestamp of the way-point
	 * @return a new {@code WayPoint}
	 * @throws IllegalArgumentException if one of the given arguments is invalid
	 */
	public static WayPoint of(
		final double latitudeDegree,
		final double longitudeDegree,
		final double elevationMeter,
		final long timeEpochMilli
	) {
		return of(
			Latitude.ofDegrees(latitudeDegree),
			Longitude.ofDegrees(longitudeDegree),
			Length.of(elevationMeter, METER),
			Instant.ofEpochMilli(timeEpochMilli)
		);
	}



	/**
	 * Create a new way-point with the given parameter.
	 *
	 * @since 1.3
	 *
	 * @param latitude the latitude of the point, WGS84 datum (mandatory)
	 * @param longitude the longitude of the point, WGS84 datum (mandatory)
	 * @param elevation the elevation (in meters) of the point (optional)
	 * @param speed the current GPS speed (optional)
	 * @param time creation/modification timestamp for an element. Conforms to ISO
	 *        8601 specification for date/time representation. Fractional seconds
	 *        are allowed for millisecond timing in tracklogs. (optional)
	 * @param magneticVariation the magnetic variation at the point (optional)
	 * @param geoidHeight height (in meters) of geoid (mean sea level) above
	 *        WGS84 earth ellipsoid. As defined in NMEA GGA message. (optional)
	 * @param name the GPS name of the way-point. This field will be transferred
	 *        to and from the GPS. GPX does not place restrictions on the length
	 *        of this field or the characters contained in it. It is up to the
	 *        receiving application to validate the field before sending it to
	 *        the GPS. (optional)
	 * @param comment GPS way-point comment. Sent to GPS as comment (optional)
	 * @param description a text description of the element. Holds additional
	 *        information about the element intended for the user, not the GPS.
	 *        (optional)
	 * @param source source of data. Included to give user some idea of
	 *        reliability and accuracy of data. "Garmin eTrex", "USGS quad
	 *        Boston North", e.g. (optional)
	 * @param links links to additional information about the way-point. Maybe
	 *        empty, but not {@code null}.
	 * @param symbol text of GPS symbol name. For interchange with other
	 *        programs, use the exact spelling of the symbol as displayed on the
	 *        GPS. If the GPS abbreviates words, spell them out. (optional)
	 * @param type type (classification) of the way-point (optional)
	 * @param fix type of GPX fix (optional)
	 * @param sat number of satellites used to calculate the GPX fix (optional)
	 * @param hdop horizontal dilution of precision (optional)
	 * @param vdop vertical dilution of precision (optional)
	 * @param pdop position dilution of precision. (optional)
	 * @param ageOfGPSData number of seconds since last DGPS update (optional)
	 * @param dgpsID ID of DGPS station used in differential correction (optional)
	 * @param course the Instantaneous course at the point
	 * @throws NullPointerException if the {@code latitude} or {@code longitude}
	 *         is {@code null}
	 * @return a new {@code WayPoint}
	 */
	public static WayPoint of(
		final Latitude latitude,
		final Longitude longitude,
		final Length elevation,
		final Speed speed,
		final Instant time,
		final Degrees magneticVariation,
		final Length geoidHeight,
		final String name,
		final String comment,
		final String description,
		final String source,
		final List<Link> links,
		final String symbol,
		final String type,
		final Fix fix,
		final UInt sat,
		final Double hdop,
		final Double vdop,
		final Double pdop,
		final Duration ageOfGPSData,
		final DGPSStation dgpsID,
		final Degrees course
	) {
		return new WayPoint(
			latitude,
			longitude,
			elevation,
			speed,
			time,
			magneticVariation,
			geoidHeight,
			name,
			comment,
			description,
			source,
			links,
			symbol,
			type,
			fix,
			sat,
			hdop,
			vdop,
			pdop,
			ageOfGPSData,
			dgpsID,
			course,
			null
		);
	}

	/**
	 * Return a (new) WayPoint from the given input {@code point}. If the given
	 * {@code point} is already a {@code WayPoint} instance, the input is casted
	 * to a {@code WayPoint} and returned.
	 *
	 * @since 1.4
	 *
	 * @param point the input {@code point} to create the {@code WayPoint} from
	 * @return a newly created {@code WayPoint} instance, or the input
	 *         {@code point} if it is already a {@code WayPoint} instance
	 * @throws NullPointerException if the given {@code point} is {@code null}
	 */
	public static WayPoint of(final Point point) {
		requireNonNull(point);

		return point instanceof WayPoint
			? (WayPoint)point
			: of(
				point.getLatitude(),
				point.getLongitude(),
				point.getElevation().orElse(null),
				point.getTime().orElse(null));
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() throws IOException {
		return new SerialProxy(SerialProxy.WAY_POINT, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		int existing = 0;
		if (_elevation != null) existing |= 1 << 0;
		if (_speed != null) existing |= 1 << 1;
		if (_time != null) existing |= 1 << 2;
		if (_magneticVariation != null) existing |= 1 << 3;
		if (_geoidHeight != null) existing |= 1 << 4;
		if (_name != null) existing |= 1 << 5;
		if (_comment != null) existing |= 1 << 6;
		if (_description != null) existing |= 1 << 7;
		if (_source != null) existing |= 1 << 8;
		if (_links != null && !_links.isEmpty()) existing |= 1 << 9;
		if (_symbol != null) existing |= 1 << 10;
		if (_type != null) existing |= 1 << 11;
		if (_fix != null) existing |= 1 << 12;
		if (_sat != null) existing |= 1 << 13;
		if (_hdop != null) existing |= 1 << 14;
		if (_vdop != null) existing |= 1 << 15;
		if (_pdop != null) existing |= 1 << 16;
		if (_ageOfGPSData != null) existing |= 1 << 17;
		if (_dgpsID != null) existing |= 1 << 18;
		if (_course != null) existing |= 1 << 19;
		if (_extensions != null) existing |= 1 << 20;

		out.writeInt(existing);
		out.writeDouble(_latitude.toDegrees());
		out.writeDouble(_longitude.toDegrees());
		if ((existing & (1 <<  0)) != 0) {
			assert _elevation != null;
			_elevation.write(out);
		}
		if ((existing & (1 <<  1)) != 0) {
			assert _speed != null;
			_speed.write(out);
		}
		if ((existing & (1 <<  2)) != 0) {
			assert _time != null;
			Instants.write(_time, out);
		}
		if ((existing & (1 <<  3)) != 0) {
			assert _magneticVariation != null;
			_magneticVariation.write(out);
		}
		if ((existing & (1 <<  4)) != 0) {
			assert _geoidHeight != null;
			_geoidHeight.write(out);
		}
		if ((existing & (1 <<  5)) != 0) {
			assert _name != null;
			IO.writeString(_name, out);
		}
		if ((existing & (1 <<  6)) != 0) {
			assert _comment != null;
			IO.writeString(_comment, out);
		}
		if ((existing & (1 <<  7)) != 0) {
			assert _description != null;
			IO.writeString(_description, out);
		}
		if ((existing & (1 <<  8)) != 0) {
			assert _source != null;
			IO.writeString(_source, out);
		}
		if ((existing & (1 <<  9)) != 0) {
			assert _links != null;
			IO.writes(_links, Link::write, out);
		}
		if ((existing & (1 << 10)) != 0) {
			assert _symbol != null;
			IO.writeString(_symbol, out);
		}
		if ((existing & (1 << 11)) != 0) {
			assert _type != null;
			IO.writeString(_type, out);
		}
		if ((existing & (1 << 12)) != 0) {
			assert _fix != null;
			IO.writeString(_fix.name(), out);
		}
		if ((existing & (1 << 13)) != 0) {
			assert _sat != null;
			_sat.write(out);
		}
		if ((existing & (1 << 14)) != 0) {
			assert _hdop != null;
			out.writeDouble(_hdop);
		}
		if ((existing & (1 << 15)) != 0) {
			assert _vdop != null;
			out.writeDouble(_vdop);
		}
		if ((existing & (1 << 16)) != 0) {
			assert _pdop != null;
			out.writeDouble(_pdop);
		}
		if ((existing & (1 << 17)) != 0) {
			assert _ageOfGPSData != null;
			out.writeLong(_ageOfGPSData.toMillis());
		}
		if ((existing & (1 << 18)) != 0) {
			assert _dgpsID != null;
			_dgpsID.write(out);
		}
		if ((existing & (1 << 19)) != 0) {
			assert _course != null;
			_course.write(out);
		}
		if ((existing & (1 << 20)) != 0) {
			assert _extensions != null;
			IO.write(_extensions, out);
		}
	}

	static WayPoint read(final DataInput in) throws IOException {
		final int existing = in.readInt();
		return new WayPoint(
			Latitude.ofDegrees(in.readDouble()),
			Longitude.ofDegrees(in.readDouble()),
			((existing & (1 <<  0)) != 0) ? Length.read(in) : null,
			((existing & (1 <<  1)) != 0) ? Speed.read(in) : null,
			((existing & (1 <<  2)) != 0) ? Instants.read(in) : null,
			((existing & (1 <<  3)) != 0) ? Degrees.read(in) : null,
			((existing & (1 <<  4)) != 0) ? Length.read(in) : null,
			((existing & (1 <<  5)) != 0) ? IO.readString(in) : null,
			((existing & (1 <<  6)) != 0) ? IO.readString(in) : null,
			((existing & (1 <<  7)) != 0) ? IO.readString(in) : null,
			((existing & (1 <<  8)) != 0) ? IO.readString(in) : null,
			((existing & (1 <<  9)) != 0) ? IO.reads(Link::read, in) : null,
			((existing & (1 << 10)) != 0) ? IO.readString(in) : null,
			((existing & (1 << 11)) != 0) ? IO.readString(in) : null,
			((existing & (1 << 12)) != 0) ? Fix.valueOf(IO.readString(in)) : null,
			((existing & (1 << 13)) != 0) ? UInt.read(in) : null,
			((existing & (1 << 14)) != 0) ? in.readDouble() : null,
			((existing & (1 << 15)) != 0) ? in.readDouble() : null,
			((existing & (1 << 16)) != 0) ? in.readDouble() : null,
			((existing & (1 << 17)) != 0) ? Duration.ofMillis(in.readLong()) : null,
			((existing & (1 << 18)) != 0) ? DGPSStation.read(in) : null,
			((existing & (1 << 19)) != 0) ? Degrees.read(in) : null,
			((existing & (1 << 20)) != 0) ? IO.readDoc(in) : null
		);
	}


	/* *************************************************************************
	 *  XML stream object serialization
	 * ************************************************************************/

	private static String url(final WayPoint point) {
		return point.getLinks().isEmpty()
			? null
			: point.getLinks().get(0).getHref().toString();
	}

	private static String urlname(final WayPoint point) {
		return point.getLinks().isEmpty()
			? null
			: point.getLinks().get(0).getText().orElse(null);
	}

	// Define the necessary writers for the different versions.
	private static XMLWriters<WayPoint>
	writers(final Function<? super Number, String> formatter) {
		return new XMLWriters<WayPoint>()
			.v00(XMLWriter.attr("lat").map(wp -> formatter.apply(wp._latitude)))
			.v00(XMLWriter.attr("lon").map(wp -> formatter.apply(wp._longitude)))
			.v00(XMLWriter.elem("ele").map(wp -> formatter.apply(wp._elevation)))
			.v00(XMLWriter.elem("speed").map(wp -> formatter.apply(wp._speed)))
			.v00(XMLWriter.elem("time").map(wp -> TimeFormat.format(wp._time)))
			.v00(XMLWriter.elem("magvar").map(wp -> formatter.apply(wp._magneticVariation)))
			.v00(XMLWriter.elem("geoidheight").map(wp -> formatter.apply(wp._geoidHeight)))
			.v00(XMLWriter.elem("name").map(wp -> wp._name))
			.v00(XMLWriter.elem("cmt").map(wp -> wp._comment))
			.v00(XMLWriter.elem("desc").map(wp -> wp._description))
			.v00(XMLWriter.elem("src").map(wp -> wp._source))
			.v11(XMLWriter.elems(Link.WRITER).map(wp -> wp._links))
			.v10(XMLWriter.elem("url").map(WayPoint::url))
			.v10(XMLWriter.elem("urlname").map(WayPoint::urlname))
			.v00(XMLWriter.elem("sym").map(wp -> wp._symbol))
			.v00(XMLWriter.elem("type").map(wp -> wp._type))
			.v00(XMLWriter.elem("fix").map(wp -> Fix.format(wp._fix)))
			.v00(XMLWriter.elem("sat").map(wp -> toIntString(wp._sat)))
			.v00(XMLWriter.elem("hdop").map(wp -> formatter.apply(wp._hdop)))
			.v00(XMLWriter.elem("vdop").map(wp -> formatter.apply(wp._vdop)))
			.v00(XMLWriter.elem("pdop").map(wp -> formatter.apply(wp._pdop)))
			.v00(XMLWriter.elem("ageofdgpsdata").map(wp -> toDurationString(wp._ageOfGPSData)))
			.v00(XMLWriter.elem("dgpsid").map(wp -> toIntString(wp._dgpsID)))
			.v10(XMLWriter.elem("course").map(wp -> formatter.apply(wp._course)))
			.v00(XMLWriter.doc("extensions").map(gpx -> gpx._extensions));
	}

	// Define the necessary readers for the different versions.
	private static XMLReaders
	readers(final Function<? super String, Length> lengthParser) {
		return new XMLReaders()
			.v00(XMLReader.attr("lat").map(Latitude::parse))
			.v00(XMLReader.attr("lon").map(Longitude::parse))
			.v00(XMLReader.elem("ele").map(lengthParser))
			.v00(XMLReader.elem("speed").map(Speed::parse))
			.v00(XMLReader.elem("time").map(TimeFormat::parse))
			.v00(XMLReader.elem("magvar").map(Degrees::parse))
			.v00(XMLReader.elem("geoidheight").map(lengthParser))
			.v00(XMLReader.elem("name"))
			.v00(XMLReader.elem("cmt"))
			.v00(XMLReader.elem("desc"))
			.v00(XMLReader.elem("src"))
			.v11(XMLReader.elems(Link.READER))
			.v10(XMLReader.elem("url").map(Format::parseURI))
			.v10(XMLReader.elem("urlname"))
			.v00(XMLReader.elem("sym"))
			.v00(XMLReader.elem("type"))
			.v00(XMLReader.elem("fix").map(Fix::parse))
			.v00(XMLReader.elem("sat").map(UInt::parse))
			.v00(XMLReader.elem("hdop").map(Format::parseDouble))
			.v00(XMLReader.elem("vdop").map(Format::parseDouble))
			.v00(XMLReader.elem("pdop").map(Format::parseDouble))
			.v00(XMLReader.elem("ageofdgpsdata").map(Format::parseDuration))
			.v00(XMLReader.elem("dgpsid").map(DGPSStation::parse))
			.v10(XMLReader.elem("course").map(Degrees::parse))
			.v00(XMLReader.doc("extensions"));
	}

	static XMLWriter<WayPoint> xmlWriter(
		final Version version,
		final String name,
		final Function<? super Number, String> formatter
	) {
		return XMLWriter.elem(name, writers(formatter).writers(version));
	}

	static XMLReader<WayPoint> xmlReader(
		final Version version,
		final String name,
		final Function<? super String, Length> lengthParser
	) {
		return XMLReader.elem(
			version == Version.V10
				? WayPoint::toWayPointV10
				: WayPoint::toWayPointV11,
			name,
			readers(lengthParser).readers(version)
		);
	}

	@SuppressWarnings("unchecked")
	private static WayPoint toWayPointV11(final Object[] v) {
		return new WayPoint(
			(Latitude)v[0],
			(Longitude)v[1],
			(Length)v[2],
			(Speed)v[3],
			(Instant)v[4],
			(Degrees)v[5],
			(Length)v[6],
			(String)v[7],
			(String)v[8],
			(String)v[9],
			(String)v[10],
			(List<Link>)v[11],
			(String)v[12],
			(String)v[13],
			(Fix)v[14],
			(UInt)v[15],
			(Double)v[16],
			(Double)v[17],
			(Double)v[18],
			(Duration)v[19],
			(DGPSStation)v[20],
			null,
			XML.extensions((Document)v[21])
		);
	}

	private static WayPoint toWayPointV10(final Object[] v) {
		return new WayPoint(
			(Latitude)v[0],
			(Longitude)v[1],
			(Length)v[2],
			(Speed)v[3],
			(Instant)v[4],
			(Degrees)v[5],
			(Length)v[6],
			(String)v[7],
			(String)v[8],
			(String)v[9],
			(String)v[10],
			v[11] != null
				? List.of(Link.of((URI)v[11], (String)v[12], null))
				: null,
			(String)v[13],
			(String)v[14],
			(Fix)v[15],
			(UInt)v[16],
			(Double)v[17],
			(Double)v[18],
			(Double)v[19],
			(Duration)v[20],
			(DGPSStation)v[21],
			(Degrees)v[22],
			XML.extensions((Document)v[23])
		);
	}

}
