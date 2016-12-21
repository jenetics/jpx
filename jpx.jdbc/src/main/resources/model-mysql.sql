
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

CREATE TABLE bounce(
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

CREATE TABLE gpx(
	version VARCHAR(5) NOT NULL DEFAULT '1.1',
	creator VARCHAR(255) NOT NULL
	-- private final Metadata _metadata;
	-- private final List<WayPoint> _wayPoints;;
	-- private final List<Route> _routes;
	-- private final List<Track> _tracks;
);



