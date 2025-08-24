-- -----------------------------------------------------------------------------
-- Create the `link` table.
-- -----------------------------------------------------------------------------
CREATE TABLE link(
	id BIGSERIAL NOT NULL PRIMARY KEY,
	href TEXT NOT NULL,
	text TEXT,
	type TEXT
);
CREATE INDEX i_link_href ON link(href);
CREATE INDEX i_link_text ON link(text);

-- -----------------------------------------------------------------------------
-- Create the `person` table.
-- -----------------------------------------------------------------------------
CREATE TABLE person(
	id BIGSERIAL NOT NULL PRIMARY KEY,
	name TEXT NOT NULL,
	email TEXT,
	link_id BIGINT REFERENCES link(id)
);
CREATE INDEX i_person_name ON person(name);
CREATE INDEX i_person_email ON person(email);
CREATE INDEX i_person_link_id ON person(link_id);

-- -----------------------------------------------------------------------------
-- Create the `copyright` table. Is bound to one metadata object.
-- -----------------------------------------------------------------------------
CREATE TABLE copyright(
	id BIGSERIAL NOT NULL PRIMARY KEY,
    author TEXT NOT NULL,
    copyright_year SMALLINT,
	license TEXT
);
CREATE INDEX i_copyright_author ON copyright(author);
CREATE INDEX i_copyright_year ON copyright(copyright_year);
CREATE INDEX i_copyright_license ON copyright(license);

-- -----------------------------------------------------------------------------
-- Create the `bounce` table. Is bound to one metadata object.
-- -----------------------------------------------------------------------------
CREATE TABLE bounds(
	id BIGSERIAL NOT NULL PRIMARY KEY,
	minlat NUMERIC(12, 9) NOT NULL,
	minlon NUMERIC(12, 9) NOT NULL,
	maxlat NUMERIC(12, 9) NOT NULL,
	maxlon NUMERIC(12, 9) NOT NULL
);

-- -----------------------------------------------------------------------------
-- Create the `metadata` table.
-- -----------------------------------------------------------------------------
CREATE TABLE metadata(
	id BIGSERIAL NOT NULL PRIMARY KEY,
	name TEXT,
	dscr TEXT,
    person_id BIGINT REFERENCES person(id),
    copyright_id BIGINT REFERENCES copyright(id),
	time TIMESTAMP WITH TIME ZONE,
	keywords TEXT,
	bounds_id BIGINT REFERENCES bounds(id),
    extensions TEXT
);
CREATE INDEX i_metadata_name ON metadata(name);
CREATE INDEX i_metadata_time ON metadata(time);
CREATE INDEX i_metadata_keywords ON metadata(keywords);
CREATE INDEX i_metadata_person_id ON metadata(person_id);
CREATE INDEX i_metadata_copyright_id ON metadata(copyright_id);
CREATE INDEX i_metadata_bounds_id ON metadata(bounds_id);

CREATE TABLE metadata_link(
	metadata_id BIGINT NOT NULL REFERENCES metadata(id) ON DELETE CASCADE,
	link_id BIGINT NOT NULL REFERENCES link(id),

	CONSTRAINT c_metadata_link_metadata_id_link_id UNIQUE (metadata_id, link_id)
);

-- -----------------------------------------------------------------------------
-- Create the `way_point` table.
-- -----------------------------------------------------------------------------
CREATE TABLE way_point(
	id BIGSERIAL NOT NULL PRIMARY KEY,
	lat NUMERIC(12, 9) NOT NULL,
	lon NUMERIC(12, 9) NOT NULL,
	ele NUMERIC(8, 2),
	speed NUMERIC(11, 2),
	time TIMESTAMP WITH TIME ZONE,
	magvar NUMERIC(8, 5),
	geoidheight NUMERIC(8, 2),
	name TEXT,
	cmt TEXT,
	dscr TEXT,
	src TEXT,
	sym TEXT,
	type TEXT,
	fix VARCHAR(4),
	sat INT,
	hdop NUMERIC(12, 2),
	vdop NUMERIC(12, 2),
	pdop NUMERIC(12, 2),
	ageofdgpsdata INT,
	dgpsid INT,
	course NUMERIC(6, 3),
	extensions TEXT
);
CREATE INDEX i_way_point_lat ON way_point(lat);
CREATE INDEX i_way_point_lon ON way_point(lon);
CREATE INDEX i_way_point_ele ON way_point(ele);
CREATE INDEX i_way_point_time ON way_point(time);

CREATE TABLE way_point_link(
	way_point_id BIGINT NOT NULL REFERENCES way_point(id) ON DELETE CASCADE,
	link_id BIGINT NOT NULL REFERENCES link(id),

	CONSTRAINT c_way_point_link_way_point_id_link_id UNIQUE (way_point_id, link_id)
);

-- -----------------------------------------------------------------------------
-- Create the `route` table.
-- -----------------------------------------------------------------------------
CREATE TABLE route(
	id BIGSERIAL NOT NULL PRIMARY KEY,
	name TEXT,
	cmt TEXT,
	dscr TEXT,
	src TEXT,
	number INT,
	type TEXT,
    extensions TEXT
);
CREATE INDEX i_route_name ON route(name);
CREATE INDEX i_route_src ON route(src);
CREATE INDEX i_route_number ON route(number);
CREATE INDEX i_route_type ON route(type);

CREATE TABLE route_link(
	route_id BIGINT NOT NULL REFERENCES route(id) ON DELETE CASCADE,
	link_id BIGINT NOT NULL REFERENCES link(id),

	CONSTRAINT c_route_link_route_id_link_id UNIQUE (route_id, link_id)
);

CREATE TABLE route_way_point(
	route_id BIGINT NOT NULL REFERENCES route(id) ON DELETE CASCADE,
	way_point_id BIGINT NOT NULL REFERENCES way_point(id),

	CONSTRAINT c_route_way_point_way_point_id UNIQUE (way_point_id)
);

-- -----------------------------------------------------------------------------
-- Create the `track_segment` table.
-- -----------------------------------------------------------------------------
CREATE TABLE track_segment(
	id BIGSERIAL NOT NULL PRIMARY KEY,
	number INT NOT NULL,
    extensions TEXT
);
CREATE INDEX i_track_segment_number ON track_segment(number);

CREATE TABLE track_segment_way_point(
	track_segment_id BIGINT NOT NULL REFERENCES track_segment(id) ON DELETE CASCADE,
	way_point_id BIGINT NOT NULL REFERENCES way_point(id),

	CONSTRAINT c_track_segment_way_point_track_segment_id_way_point_id
		UNIQUE (track_segment_id, way_point_id)
);

-- -----------------------------------------------------------------------------
-- Create the `track` table.
-- -----------------------------------------------------------------------------
CREATE TABLE track(
	id BIGSERIAL NOT NULL PRIMARY KEY,
	name TEXT,
	cmt TEXT,
	dscr TEXT,
	src TEXT,
	number INT,
	type TEXT,
    extensions TEXT
);
CREATE INDEX i_track_name ON track(name);
CREATE INDEX i_track_src ON track(src);
CREATE INDEX i_track_number ON track(number);
CREATE INDEX i_track_type ON track(type);

CREATE TABLE track_track_segment(
	track_id BIGINT NOT NULL REFERENCES track(id),
	track_segment_id BIGINT NOT NULL REFERENCES track_segment(id),

	CONSTRAINT c_track_track_segment_track_id_track_segment_id
		UNIQUE (track_segment_id, track_id)
);

CREATE TABLE track_link(
	track_id BIGINT NOT NULL REFERENCES track(id),
	link_id BIGINT NOT NULL REFERENCES link(id),

	CONSTRAINT c_track_link_track_id_link_id UNIQUE (track_id, link_id)
);

-- -----------------------------------------------------------------------------
-- Create the `gpx` table.
-- -----------------------------------------------------------------------------
CREATE TABLE gpx(
	id BIGSERIAL NOT NULL PRIMARY KEY,
	version VARCHAR(5) NOT NULL DEFAULT '1.1',
	creator TEXT NOT NULL,
	metadata_id BIGINT REFERENCES metadata(id),
    extensions TEXT
);
CREATE INDEX i_gpx_creator ON gpx(creator);
CREATE INDEX i_gpx_metadata_id ON gpx(metadata_id);

CREATE TABLE gpx_way_point(
	gpx_id BIGINT NOT NULL REFERENCES gpx(id),
	way_point_id BIGINT NOT NULL REFERENCES way_point(id),

	CONSTRAINT c_gpx_way_point_gpx_id_way_point_id UNIQUE (gpx_id, way_point_id)
);

CREATE TABLE gpx_route(
	gpx_id BIGINT NOT NULL REFERENCES gpx(id),
	route_id BIGINT NOT NULL REFERENCES route(id),

	CONSTRAINT c_gpx_track_gpx_id_route_id UNIQUE (gpx_id, route_id)
);

CREATE TABLE gpx_track(
	gpx_id BIGINT NOT NULL REFERENCES gpx(id),
	track_id BIGINT NOT NULL REFERENCES track(id),

	CONSTRAINT c_gpx_track_gpx_id_track_id UNIQUE (gpx_id, track_id)
);

