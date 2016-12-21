
CREATE TABLE link(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	href VARCHAR(255) NOT NULL,
	text VARCHAR(255),
	type VARCHAR(255)
);
CREATE INDEX i_link_href ON link(href);

CREATE TABLE person(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255),
	email VARCHAR(255),
	link_id BIGINT,

	FOREIGN KEY (link_id) REFERENCES link(id)
);
CREATE UNIQUE INDEX i_person_name ON person(name);

CREATE TABLE copyright(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	author VARCHAR(255) NOT NULL,
	year INT,
	license VARCHAR(255)
);
CREATE UNIQUE INDEX i_copyright_author ON copyright(author);

CREATE TABLE bounds(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	minlat DOUBLE PRECISION NOT NULL,
	minlon DOUBLE PRECISION NOT NULL,
	maxlat DOUBLE PRECISION NOT NULL,
	maxlon DOUBLE PRECISION NOT NULL
);

CREATE TABLE metadata(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255),
	description TEXT,
	person_id BIGINT,
	copyright_id BIGINT,
	time TIMESTAMP,
	keywords VARCHAR(255),
	bounds_id BIGINT,

	FOREIGN KEY (person_id) REFERENCES person(id),
	FOREIGN KEY (bounds_id) REFERENCES bounds(id)
);
CREATE UNIQUE INDEX i_metadata_name ON metadata(name);

CREATE TABLE metadata_link(
	metadata_id BIGINT NOT NULL,
	link_id BIGINT NOT NULL,

	FOREIGN KEY (metadata_id) REFERENCES metadata(id),
	FOREIGN KEY (link_id) REFERENCES link(id)
);
CREATE UNIQUE INDEX i_metadata_id_link_id ON metadata_link(metadata_id, link_id);

CREATE TABLE way_point(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	lat DOUBLE PRECISION NOT NULL,
	lon DOUBLE PRECISION NOT NULL,
	ele DOUBLE PRECISION,
	speed DOUBLE PRECISION,
	time TIMESTAMP,
	magvar DOUBLE PRECISION,
	geohight DOUBLE PRECISION,
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

CREATE TABLE way_point_link(
	way_point_id BIGINT NOT NULL,
	link_id BIGINT NOT NULL,

	FOREIGN KEY (way_point_id) REFERENCES way_point(id),
	FOREIGN KEY (link_id) REFERENCES link(id)
);
CREATE UNIQUE INDEX i_way_point_link_id_link_id ON way_point_link(way_point_id, link_id);

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
	route_id BIGINT NOT NULL,
	link_id BIGINT NOT NULL,

	FOREIGN KEY (route_id) REFERENCES route(id),
	FOREIGN KEY (link_id) REFERENCES link(id)
);
CREATE UNIQUE INDEX i_route_link_id_link_id ON route_link(route_id, link_id);

CREATE TABLE route_way_point(
	route_id BIGINT NOT NULL,
	way_point_id BIGINT NOT NULL,

	FOREIGN KEY (route_id) REFERENCES route(id),
	FOREIGN KEY (way_point_id) REFERENCES way_point(id)
);
CREATE UNIQUE INDEX i_route_way_point_id_link_id ON route_way_point(route_id, way_point_id);

CREATE TABLE track_segment(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT
);

CREATE TABLE track_segment_way_point(
	track_segment_id BIGINT NOT NULL,
	way_point_id BIGINT NOT NULL,

	FOREIGN KEY (track_segment_id) REFERENCES track_segment(id),
	FOREIGN KEY (way_point_id) REFERENCES way_point(id)
);
CREATE UNIQUE INDEX i_track_segment_way_point_id_link_id
	ON track_segment_way_point(track_segment_id, way_point_id);


CREATE TABLE track(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255),
	cmt VARCHAR(255),
	`desc` TEXT,
	src VARCHAR(255),
	number INT,
	type VARCHAR(255)
);

CREATE TABLE track_track_segment(
	track_id BIGINT NOT NULL,
	track_segment_id BIGINT NOT NULL,

	FOREIGN KEY (track_id) REFERENCES track(id),
	FOREIGN KEY (track_segment_id) REFERENCES track_segment(id)
);
CREATE UNIQUE INDEX i_track_segment_way_point_id_link_id
	ON track_segment_way_point(track_segment_id, way_point_id);

CREATE TABLE track_link(
	track_id BIGINT NOT NULL,
	link_id BIGINT NOT NULL,

	FOREIGN KEY (track_id) REFERENCES track(id),
	FOREIGN KEY (link_id) REFERENCES link(id)
);
CREATE UNIQUE INDEX i_track_link_id_link_id ON track_link(track_id, link_id);

CREATE TABLE gpx(
	version VARCHAR(5) NOT NULL DEFAULT '1.1',
	creator VARCHAR(255) NOT NULL,
	metadata_id BIGINT,

	FOREIGN KEY (metadata_id) REFERENCES metadata(id)
);

CREATE TABLE gpx_way_point(
	gpx_id BIGINT NOT NULL,
	way_point_id BIGINT NOT NULL,

	FOREIGN KEY (gpx_id) REFERENCES gpx(id),
	FOREIGN KEY (way_point_id) REFERENCES way_point(id)
);
CREATE UNIQUE INDEX i_gpx_way_point_id_link_id ON gpx_way_point(gpx_id, way_point_id);

CREATE TABLE gpx_track(
	gpx_id BIGINT NOT NULL,
	track_id BIGINT NOT NULL,

	FOREIGN KEY (gpx_id) REFERENCES gpx(id),
	FOREIGN KEY (track_id) REFERENCES track(id)
);
CREATE UNIQUE INDEX i_gpx_track_gpx_id_track_id ON gpx_track(gpx_id, track_id);



