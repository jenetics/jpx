-- -----------------------------------------------------------------------------
-- -----------------------------------------------------------------------------
-- GPX data-model
-- -----------------------------------------------------------------------------
-- -----------------------------------------------------------------------------

-- -----------------------------------------------------------------------------
-- Create the `link` table.
-- -----------------------------------------------------------------------------
CREATE TABLE link(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	href VARCHAR(255) NOT NULL,
	text VARCHAR(255),
	type VARCHAR(255),

	CONSTRAINT c_link_href UNIQUE (href)
);

-- -----------------------------------------------------------------------------
-- Create the `person` table.
-- -----------------------------------------------------------------------------
CREATE TABLE person(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	email VARCHAR(255),
	link_id BIGINT REFERENCES link(id),

	CONSTRAINT c_person_name UNIQUE (name)
);

-- -----------------------------------------------------------------------------
-- Create the `copyright` table.
-- -----------------------------------------------------------------------------
CREATE TABLE copyright(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	author VARCHAR(255) NOT NULL,
	year INT,
	license VARCHAR(255),

	CONSTRAINT c_copyright_author UNIQUE (author)
);

-- -----------------------------------------------------------------------------
-- Create the `bounce` table.
-- -----------------------------------------------------------------------------
CREATE TABLE bounds(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	minlat DOUBLE PRECISION NOT NULL,
	minlon DOUBLE PRECISION NOT NULL,
	maxlat DOUBLE PRECISION NOT NULL,
	maxlon DOUBLE PRECISION NOT NULL
);

-- -----------------------------------------------------------------------------
-- Create the `metadata` table.
-- -----------------------------------------------------------------------------
CREATE TABLE metadata(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255),
	`desc` TEXT,
	person_id BIGINT REFERENCES person(id),
	copyright_id BIGINT,
	time TIMESTAMP,
	keywords VARCHAR(255),
	bounds_id BIGINT REFERENCES bounds(id)
);
CREATE INDEX i_metadata_name ON metadata(name);
CREATE INDEX i_metadata_keywords ON metadata(keywords);

CREATE TABLE metadata_link(
	metadata_id BIGINT NOT NULL REFERENCES metadata(id) ON DELETE CASCADE,
	link_id BIGINT NOT NULL REFERENCES link(id),

	CONSTRAINT c_metadata_link_metadata_id_link_id UNIQUE (metadata_id, link_id)
);

-- -----------------------------------------------------------------------------
-- Create the `way_point` table.
-- -----------------------------------------------------------------------------
CREATE TABLE way_point(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	lat DOUBLE PRECISION NOT NULL,
	lon DOUBLE PRECISION NOT NULL,
	ele DOUBLE PRECISION,
	speed DOUBLE PRECISION,
	time TIMESTAMP,
	magvar DOUBLE PRECISION,
	geoidheight DOUBLE PRECISION,
	name VARCHAR(255),
	cmt VARCHAR(255),
	`desc` TEXT,
	src VARCHAR(255),
	sym VARCHAR(255),
	type VARCHAR(255),
	fix VARCHAR(10),
	sat INT,
	hdop DOUBLE PRECISION,
	vdop DOUBLE PRECISION,
	pdop DOUBLE PRECISION,
	ageofdgpsdata INT,
	dgpsid INT
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
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255),
	cmt VARCHAR(255),
	`desc` TEXT,
	src VARCHAR(255),
	number INT,
	type VARCHAR(255)
);

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
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	number INT NOT NULL
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
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255),
	cmt VARCHAR(255),
	`desc` TEXT,
	src VARCHAR(255),
	number INT,
	type VARCHAR(255)
);
CREATE INDEX i_track_name ON track(name);

CREATE TABLE track_track_segment(
	track_id BIGINT NOT NULL REFERENCES track(id),
	track_segment_id BIGINT NOT NULL REFERENCES track_segment(id),

	CONSTRAINT c_track_track_segment_track_id_track_segment_id
		UNIQUE (track_segment_id, track_segment_id)
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
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	version VARCHAR(5) NOT NULL DEFAULT '1.1',
	creator VARCHAR(255) NOT NULL,
	metadata_id BIGINT REFERENCES metadata(id)
);
CREATE INDEX i_gpx_creator ON gpx(creator);

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

