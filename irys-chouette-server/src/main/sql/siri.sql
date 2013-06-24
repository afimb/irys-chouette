
CREATE SCHEMA :schemaname ;


ALTER SCHEMA :schemaname OWNER TO :username ;

SET search_path TO :schemaname ;

CREATE TABLE dated_calls (
    dated_vehicle_journey_id bigint NOT NULL,
    stop_point_id bigint NOT NULL,
    last_modification_date timestamp without time zone,
    departure_status character varying(255),
    arrival_status character varying(255),
    is_departure boolean,
    "position" bigint,
    is_arrival boolean DEFAULT false,
    expected_arrival_time timestamp without time zone,
    expected_departure_time timestamp without time zone,
    aimed_arrival_time timestamp without time zone,
    aimed_departure_time timestamp without time zone
);


ALTER TABLE dated_calls OWNER TO :username;

COMMENT ON TABLE dated_calls IS 'Vehicle Journey at Stop';
COMMENT ON COLUMN dated_calls.dated_vehicle_journey_id IS 'dated_vehicle_journeys foreign key (1st part of primary key)';
COMMENT ON COLUMN dated_calls.stop_point_id IS 'StopPoint objectId as foreign key (2nd part of primary key)';
COMMENT ON COLUMN dated_calls.last_modification_date IS 'last modification date';
COMMENT ON COLUMN dated_calls.departure_status IS 'ontime, arrived, canceled, ...';
COMMENT ON COLUMN dated_calls.arrival_status IS 'ontime, arrived, canceled, ...';
COMMENT ON COLUMN dated_calls.is_departure IS 'First stop of the vehicle journey';
COMMENT ON COLUMN dated_calls."position" IS 'order in JourneyPattern';
COMMENT ON COLUMN dated_calls.is_arrival IS 'Last stop of the vehicle journey';
COMMENT ON COLUMN dated_calls.expected_arrival_time IS 'Real Time Arrival time';
COMMENT ON COLUMN dated_calls.expected_departure_time IS 'Real Time Departure time';
COMMENT ON COLUMN dated_calls.aimed_arrival_time IS 'Theorical Arrival time';
COMMENT ON COLUMN dated_calls.aimed_departure_time IS 'Theorical Departure time';

CREATE TABLE dated_vehicle_journeys (
    id bigint NOT NULL,
    application_date date NOT NULL,
    last_modification_date timestamp without time zone,
    line_id bigint,
    route_id bigint,
    journey_pattern_id bigint,
    vehicle_journey_id bigint,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    published_journey_name character varying(255),
    published_journey_identifier character varying(255),
    transport_mode character varying(255),
    vehicle_type_identifier character varying(255),
    status_value character varying(255),
    facility character varying(255),
    number integer,
    comment character varying(255),
    company_id bigint,
    service_id bigint,
    service_order integer DEFAULT 0
);


ALTER TABLE dated_vehicle_journeys OWNER TO :username ;
COMMENT ON TABLE dated_vehicle_journeys IS 'Vehicle journey';
COMMENT ON COLUMN dated_vehicle_journeys.id IS 'Internal identification';
COMMENT ON COLUMN dated_vehicle_journeys.application_date IS 'Applicable date';
COMMENT ON COLUMN dated_vehicle_journeys.last_modification_date IS 'last modification date';
COMMENT ON COLUMN dated_vehicle_journeys.line_id IS 'Line objectId as foreign key ';
COMMENT ON COLUMN dated_vehicle_journeys.route_id IS 'Route objectId as foreign key';
COMMENT ON COLUMN dated_vehicle_journeys.journey_pattern_id IS 'JourneyPattern objectId as foreign key';
COMMENT ON COLUMN dated_vehicle_journeys.vehicle_journey_id IS 'VehicleJourney objectId as foreign key';
COMMENT ON COLUMN dated_vehicle_journeys.objectid IS 'Neptune identification';
COMMENT ON COLUMN dated_vehicle_journeys.object_version IS 'Version of this object';
COMMENT ON COLUMN dated_vehicle_journeys.creation_time IS 'Creation date and time';
COMMENT ON COLUMN dated_vehicle_journeys.creator_id IS 'Creator identification';
COMMENT ON COLUMN dated_vehicle_journeys.published_journey_name IS 'Name for travelers';
COMMENT ON COLUMN dated_vehicle_journeys.published_journey_identifier IS 'Identifier for travelers';
COMMENT ON COLUMN dated_vehicle_journeys.transport_mode IS 'Transport mode';
COMMENT ON COLUMN dated_vehicle_journeys.vehicle_type_identifier IS 'Vehicle type';
COMMENT ON COLUMN dated_vehicle_journeys.status_value IS 'Service Status';
COMMENT ON COLUMN dated_vehicle_journeys.facility IS 'facility';
COMMENT ON COLUMN dated_vehicle_journeys.number IS 'Number of the vehicle journey';
COMMENT ON COLUMN dated_vehicle_journeys.comment IS 'Comment';
COMMENT ON COLUMN dated_vehicle_journeys.company_id IS 'Company objectId as foreign key';
COMMENT ON COLUMN dated_vehicle_journeys.service_id IS 'vehicle service attached to this vehiclejourney';
COMMENT ON COLUMN dated_vehicle_journeys.service_order IS 'order of vehiclejourney in service (0 = unknown)';


CREATE SEQUENCE dated_vehicle_journeys_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE dated_vehicle_journeys_id_seq OWNER TO :username ;
ALTER SEQUENCE dated_vehicle_journeys_id_seq OWNED BY dated_vehicle_journeys.id;


SET default_with_oids = true;
CREATE TABLE general_messages (
    id bigint NOT NULL,
    info_channel character varying(20) NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    creation_date timestamp without time zone NOT NULL,
    last_modification_date timestamp without time zone NOT NULL,
    valid_until_date timestamp without time zone,
    status character varying(3) DEFAULT 'OK'::character varying NOT NULL,
    objectid character varying(255)
);


ALTER TABLE general_messages OWNER TO :username;
COMMENT ON TABLE general_messages IS 'messages for general message service';
COMMENT ON COLUMN general_messages.id IS 'message id';
COMMENT ON COLUMN general_messages.info_channel IS 'Information Channel : Information, Commercial or Perturbation';
COMMENT ON COLUMN general_messages.version IS 'message version';
COMMENT ON COLUMN general_messages.creation_date IS 'creation date';
COMMENT ON COLUMN general_messages.last_modification_date IS 'last modification date';
COMMENT ON COLUMN general_messages.valid_until_date IS 'expiration date';

CREATE SEQUENCE general_messages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE general_messages_id_seq OWNER TO :username ;
ALTER SEQUENCE general_messages_id_seq OWNED BY general_messages.id;


CREATE TABLE general_message_journey_patterns (
    general_message_id bigint NOT NULL,
    journey_pattern_id bigint NOT NULL
);


ALTER TABLE general_message_journey_patterns OWNER TO :username ;
COMMENT ON TABLE general_message_journey_patterns IS 'message journeypatterns relationships';
COMMENT ON COLUMN general_message_journey_patterns.general_message_id IS 'message';
COMMENT ON COLUMN general_message_journey_patterns.journey_pattern_id IS 'attached journeypattern';

CREATE TABLE general_message_lines (
    general_message_id bigint NOT NULL,
    line_id bigint NOT NULL
);


ALTER TABLE general_message_lines OWNER TO :username ;
COMMENT ON TABLE general_message_lines IS 'message lines relationships';
COMMENT ON COLUMN general_message_lines.general_message_id IS 'message';
COMMENT ON COLUMN general_message_lines.line_id IS 'attached line';

CREATE TABLE general_message_line_sections (
    general_message_id bigint NOT NULL,
    line_id bigint NOT NULL,
    start_id bigint NOT NULL,
    end_id bigint NOT NULL
);


ALTER TABLE general_message_line_sections OWNER TO :username ;
COMMENT ON TABLE general_message_line_sections IS 'message line sections relationships';
COMMENT ON COLUMN general_message_line_sections.general_message_id IS 'message';
COMMENT ON COLUMN general_message_line_sections.line_id IS 'attached line';
COMMENT ON COLUMN general_message_line_sections.start_id IS 'start of section';
COMMENT ON COLUMN general_message_line_sections.end_id IS 'end of section';

CREATE TABLE general_message_messages (
    general_message_id bigint NOT NULL,
    language character varying(2) DEFAULT 'FR'::character varying NOT NULL,
    type character varying(20) NOT NULL,
    text character varying(1000) NOT NULL
);

ALTER TABLE general_message_messages OWNER TO :username ;
COMMENT ON TABLE general_message_messages IS 'effective messages';
COMMENT ON COLUMN general_message_messages.general_message_id IS 'message';
COMMENT ON COLUMN general_message_messages.language IS 'coded language : FR, EN, DE, ...';
COMMENT ON COLUMN general_message_messages.type IS 'shortMessage, longMessage or codedMessage';
COMMENT ON COLUMN general_message_messages.text IS 'text or code';


CREATE TABLE general_message_routes (
    general_message_id bigint NOT NULL,
    route_id bigint NOT NULL
);


ALTER TABLE general_message_routes OWNER TO :username;
COMMENT ON TABLE general_message_routes IS 'message routes relationships';
COMMENT ON COLUMN general_message_routes.general_message_id IS 'message';
COMMENT ON COLUMN general_message_routes.route_id IS 'attached route';

CREATE TABLE general_message_stop_areas (
    general_message_id bigint NOT NULL,
    stop_area_id bigint NOT NULL
);


ALTER TABLE general_message_stop_areas OWNER TO :username ;

COMMENT ON TABLE general_message_stop_areas IS 'message stops relationships';
COMMENT ON COLUMN general_message_stop_areas.general_message_id IS 'message';
COMMENT ON COLUMN general_message_stop_areas.stop_area_id IS 'attached stop area';

CREATE TABLE parameter (
    name character varying NOT NULL,
    value character varying NOT NULL
);


ALTER TABLE parameter OWNER TO :username ;


CREATE TABLE vehicles (
    id bigint NOT NULL,
    application_date date NOT NULL,
    last_modification_date timestamp without time zone,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    vehicle_type_identifier character varying(255),
    status_value character varying(255),
    in_congestion boolean,
    in_panic boolean,
    longitude numeric(19,16),
    latitude numeric(19,16),
    long_lat_type character varying(255),
    x numeric(19,2),
    y numeric(19,2),
    projection_type character varying(255),
    is_monitored boolean DEFAULT true,
    monitoring_error character varying(255),
    bearing numeric(19,16),
    delay bigint,
    link_distance bigint,
    link_percentage numeric(19,16),
    message character varying(255),
    current_vehicle_journey_id bigint,
    current_stop_id bigint
);


ALTER TABLE vehicles OWNER TO :username ;

COMMENT ON TABLE vehicles IS 'Vehicle';
COMMENT ON COLUMN vehicles.id IS 'Internal identification';
COMMENT ON COLUMN vehicles.application_date IS 'Applicable date';
COMMENT ON COLUMN vehicles.last_modification_date IS 'last modification date';
COMMENT ON COLUMN vehicles.objectid IS 'Neptune identification';
COMMENT ON COLUMN vehicles.object_version IS 'Version of this object';
COMMENT ON COLUMN vehicles.creation_time IS 'Creation date and time';
COMMENT ON COLUMN vehicles.creator_id IS 'Creator identification';
COMMENT ON COLUMN vehicles.vehicle_type_identifier IS 'Vehicle type';
COMMENT ON COLUMN vehicles.status_value IS 'Service Status';
COMMENT ON COLUMN vehicles.in_congestion IS 'indicate if vehicle is in a traffic congestion';
COMMENT ON COLUMN vehicles.in_panic IS 'indicate if alarm is on';
COMMENT ON COLUMN vehicles.longitude IS 'Longitude';
COMMENT ON COLUMN vehicles.latitude IS 'Latitude';
COMMENT ON COLUMN vehicles.long_lat_type IS 'Model used for Longitude and Latitude (Standard, WGS84 or WGS92)';
COMMENT ON COLUMN vehicles.x IS 'X coordinate';
COMMENT ON COLUMN vehicles.y IS 'Y coordinate';
COMMENT ON COLUMN vehicles.projection_type IS 'Projection used for coordinates (epsg code)';
COMMENT ON COLUMN vehicles.is_monitored IS 'indicate if bus is localised';
COMMENT ON COLUMN vehicles.monitoring_error IS 'if ismonitored is false : gives a error message';
COMMENT ON COLUMN vehicles.bearing IS 'absolute bearing of the bus';
COMMENT ON COLUMN vehicles.delay IS 'delay value in seconds :negative when early, null if unknown';
COMMENT ON COLUMN vehicles.link_distance IS 'distance between last and next stop in meters, null if unknown';
COMMENT ON COLUMN vehicles.link_percentage IS 'percentage of link already covered by vehicle, null if unknown';
COMMENT ON COLUMN vehicles.message IS 'message about vehicle, null if unknown';
COMMENT ON COLUMN vehicles.current_vehicle_journey_id IS 'current vehicle journey';
COMMENT ON COLUMN vehicles.current_stop_id IS 'current or next stop ';

CREATE SEQUENCE vehicles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

ALTER TABLE vehicles_id_seq OWNER TO :username ;

ALTER SEQUENCE vehicles_id_seq OWNED BY vehicles.id;

CREATE TABLE vehicle_services (
    id bigint NOT NULL,
    application_date date NOT NULL,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    vehicle_id bigint
);


ALTER TABLE vehicle_services OWNER TO :username ;

COMMENT ON TABLE vehicle_services IS 'Vehicle Service';
COMMENT ON COLUMN vehicle_services.id IS 'Internal identification';
COMMENT ON COLUMN vehicle_services.application_date IS 'Applicable date';
COMMENT ON COLUMN vehicle_services.objectid IS 'Neptune identification';
COMMENT ON COLUMN vehicle_services.object_version IS 'Version of this object';
COMMENT ON COLUMN vehicle_services.creation_time IS 'Creation date and time';
COMMENT ON COLUMN vehicle_services.creator_id IS 'Creator identification';
COMMENT ON COLUMN vehicle_services.vehicle_id IS 'Vehicle executing this service';

CREATE SEQUENCE vehicle_services_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

ALTER TABLE vehicle_services_id_seq OWNER TO :username ;

ALTER SEQUENCE vehicle_services_id_seq OWNED BY vehicle_services.id;

ALTER TABLE dated_vehicle_journeys ALTER COLUMN id SET DEFAULT nextval('dated_vehicle_journeys_id_seq'::regclass);

ALTER TABLE general_messages ALTER COLUMN id SET DEFAULT nextval('general_messages_id_seq'::regclass);

ALTER TABLE vehicles ALTER COLUMN id SET DEFAULT nextval('vehicles_id_seq'::regclass);

ALTER TABLE vehicle_services ALTER COLUMN id SET DEFAULT nextval('vehicle_services_id_seq'::regclass);

ALTER TABLE ONLY general_messages
    ADD CONSTRAINT "PK_general_messages" PRIMARY KEY (id);

ALTER TABLE ONLY general_message_journey_patterns
    ADD CONSTRAINT "PK_general_message_journey_patterns" PRIMARY KEY (general_message_id, journey_pattern_id);

ALTER TABLE ONLY general_message_lines
    ADD CONSTRAINT "PK_general_message_lines" PRIMARY KEY (general_message_id, line_id);

ALTER TABLE ONLY general_message_line_sections
    ADD CONSTRAINT "PK_general_message_line_section" PRIMARY KEY (general_message_id, line_id, start_id, end_id);

ALTER TABLE ONLY general_message_routes
    ADD CONSTRAINT "PK_general_message_routes" PRIMARY KEY (general_message_id, route_id);

ALTER TABLE ONLY general_message_stop_areas
    ADD CONSTRAINT "PK_general_message_stop_areas" PRIMARY KEY (general_message_id, stop_area_id);

ALTER TABLE ONLY dated_calls
    ADD CONSTRAINT dated_calls_pkey PRIMARY KEY (dated_vehicle_journey_id, stop_point_id);

ALTER TABLE ONLY dated_vehicle_journeys
    ADD CONSTRAINT dated_vehicle_journeys_pkey PRIMARY KEY (id);

ALTER TABLE ONLY dated_vehicle_journeys
    ADD CONSTRAINT dvj_objectid_ukey UNIQUE (objectid, application_date);

ALTER TABLE ONLY parameter
    ADD CONSTRAINT pk_parameter PRIMARY KEY (name);

ALTER TABLE ONLY vehicles
    ADD CONSTRAINT vehicles_pkey PRIMARY KEY (id);

ALTER TABLE ONLY vehicle_services
    ADD CONSTRAINT vehicle_services_pkey PRIMARY KEY (id);

ALTER TABLE ONLY vehicles
    ADD CONSTRAINT vh_objectid_ukey UNIQUE (objectid, application_date);

ALTER TABLE ONLY vehicle_services
    ADD CONSTRAINT vs_objectid_ukey UNIQUE (objectid, application_date);

CREATE INDEX fki_general_message_m_fkey ON general_message_messages USING btree (general_message_id);

ALTER TABLE ONLY general_message_journey_patterns
    ADD CONSTRAINT "FK_general_message_journey_patterns_gm" FOREIGN KEY (general_message_id) REFERENCES general_messages(id);

ALTER TABLE ONLY general_message_lines
    ADD CONSTRAINT "FK_general_message_lines_gm" FOREIGN KEY (general_message_id) REFERENCES general_messages(id);

ALTER TABLE ONLY general_message_line_sections
    ADD CONSTRAINT "FK_general_message_line_sections_gm" FOREIGN KEY (general_message_id) REFERENCES general_messages(id);

ALTER TABLE ONLY general_message_routes
    ADD CONSTRAINT "FK_general_message_routes_gm" FOREIGN KEY (general_message_id) REFERENCES general_messages(id);

ALTER TABLE ONLY general_message_stop_areas
    ADD CONSTRAINT "FK_general_message_stop_areas_gm" FOREIGN KEY (general_message_id) REFERENCES general_messages(id);

ALTER TABLE ONLY dated_calls
    ADD CONSTRAINT dc_dvj_fkey FOREIGN KEY (dated_vehicle_journey_id) REFERENCES dated_vehicle_journeys(id) ON DELETE CASCADE;

ALTER TABLE ONLY dated_vehicle_journeys
    ADD CONSTRAINT dvj_service_fkey FOREIGN KEY (service_id) REFERENCES vehicle_services(id);

ALTER TABLE ONLY general_message_messages
    ADD CONSTRAINT general_message_m_fkey FOREIGN KEY (general_message_id) REFERENCES general_messages(id) ON DELETE CASCADE;

ALTER TABLE ONLY vehicle_services
    ADD CONSTRAINT vehicle_fkey FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE;

ALTER TABLE ONLY vehicles
  ADD CONSTRAINT vehicle_dvj_fkey FOREIGN KEY (current_vehicle_journey_id)  REFERENCES dated_vehicle_journeys (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE SET NULL;

