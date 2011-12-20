
CREATE SCHEMA :schemaname ;


ALTER SCHEMA :schemaname OWNER TO :username ;

CREATE TABLE :schemaname.datedcall (
    datedvehiclejourneyid bigint NOT NULL,
    stoppointid character varying(255) NOT NULL,
    status character varying(255),
    isdeparture boolean,
    "position" bigint,
    isarrival boolean DEFAULT false,
    expectedarrivaltime timestamp without time zone,
    expecteddeparturetime timestamp without time zone,
    aimedarrivaltime timestamp without time zone,
    aimeddeparturetime timestamp without time zone
);


ALTER TABLE :schemaname.datedcall OWNER TO :username;

COMMENT ON TABLE :schemaname.datedcall IS 'Vehicle Journey at Stop';
COMMENT ON COLUMN :schemaname.datedcall.datedvehiclejourneyid IS 'DatedVehicleJourney foreign key (1st part of primary key)';
COMMENT ON COLUMN :schemaname.datedcall.stoppointid IS 'StopPoint objectId as foreign key (2nd part of primary key)';
COMMENT ON COLUMN :schemaname.datedcall.status IS 'ontime, arrived, canceled, ...';
COMMENT ON COLUMN :schemaname.datedcall.isdeparture IS 'First stop of the vehicle journey';
COMMENT ON COLUMN :schemaname.datedcall."position" IS 'order in JourneyPattern';
COMMENT ON COLUMN :schemaname.datedcall.isarrival IS 'Last stop of the vehicle journey';
COMMENT ON COLUMN :schemaname.datedcall.expectedarrivaltime IS 'Real Time Arrival time';
COMMENT ON COLUMN :schemaname.datedcall.expecteddeparturetime IS 'Real Time Departure time';
COMMENT ON COLUMN :schemaname.datedcall.aimedarrivaltime IS 'Theorical Arrival time';
COMMENT ON COLUMN :schemaname.datedcall.aimeddeparturetime IS 'Theorical Departure time';

CREATE TABLE :schemaname.datedvehiclejourney (
    id bigint NOT NULL,
    application_date date NOT NULL,
    lineid character varying(255),
    routeid character varying(255),
    journeypatternid character varying(255),
    vehiclejourneyid character varying(255),
    objectid character varying(255) NOT NULL,
    objectversion integer,
    creationtime timestamp without time zone,
    creatorid character varying(255),
    publishedjourneyname character varying(255),
    publishedjourneyidentifier character varying(255),
    transportmode character varying(255),
    vehicletypeidentifier character varying(255),
    statusvalue character varying(255),
    facility character varying(255),
    number integer,
    comment character varying(255),
    companyid character varying(255),
    serviceid bigint,
    serviceorder integer DEFAULT 0
);


ALTER TABLE :schemaname.datedvehiclejourney OWNER TO :username ;
COMMENT ON TABLE :schemaname.datedvehiclejourney IS 'Vehicle journey';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.id IS 'Internal identification';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.application_date IS 'Applicable date';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.lineid IS 'Line objectId as foreign key ';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.routeid IS 'Route objectId as foreign key';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.journeypatternid IS 'JourneyPattern objectId as foreign key';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.vehiclejourneyid IS 'VehicleJourney objectId as foreign key';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.objectid IS 'Neptune identification';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.objectversion IS 'Version of this object';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.creationtime IS 'Creation date and time';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.creatorid IS 'Creator identification';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.publishedjourneyname IS 'Name for travelers';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.publishedjourneyidentifier IS 'Identifier for travelers';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.transportmode IS 'Transport mode';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.vehicletypeidentifier IS 'Vehicle type';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.statusvalue IS 'Service Status';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.facility IS 'facility';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.number IS 'Number of the vehicle journey';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.comment IS 'Comment';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.companyid IS 'Company objectId as foreign key';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.serviceid IS 'vehicle service attached to this vehiclejourney';
COMMENT ON COLUMN :schemaname.datedvehiclejourney.serviceorder IS 'order of vehiclejourney in service (0 = unknown)';


CREATE SEQUENCE :schemaname.datedvehiclejourney_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE :schemaname.datedvehiclejourney_id_seq OWNER TO :username ;
ALTER SEQUENCE :schemaname.datedvehiclejourney_id_seq OWNED BY :schemaname.datedvehiclejourney.id;


SET default_with_oids = true;
CREATE TABLE :schemaname.general_message (
    id integer NOT NULL,
    infochannel character varying(20) NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    creation_date timestamp without time zone NOT NULL,
    last_modification_date timestamp without time zone NOT NULL,
    valid_until_date timestamp without time zone,
    status character varying(3) DEFAULT 'OK'::character varying NOT NULL,
    objectid character varying(255)
);


ALTER TABLE :schemaname.general_message OWNER TO :username;
COMMENT ON TABLE :schemaname.general_message IS 'messages for general message service';
COMMENT ON COLUMN :schemaname.general_message.id IS 'message id';
COMMENT ON COLUMN :schemaname.general_message.infochannel IS 'Information Channel : Information, Commercial or Perturbation';
COMMENT ON COLUMN :schemaname.general_message.version IS 'message version';
COMMENT ON COLUMN :schemaname.general_message.creation_date IS 'creation date';
COMMENT ON COLUMN :schemaname.general_message.last_modification_date IS 'last modification date';
COMMENT ON COLUMN :schemaname.general_message.valid_until_date IS 'expiration date';

CREATE SEQUENCE :schemaname.general_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE :schemaname.general_message_id_seq OWNER TO :username ;
ALTER SEQUENCE :schemaname.general_message_id_seq OWNED BY :schemaname.general_message.id;


CREATE TABLE :schemaname.gm_journeypatterns (
    gm_id bigint NOT NULL,
    journeypattern_id character varying(255) NOT NULL
);


ALTER TABLE :schemaname.gm_journeypatterns OWNER TO :username ;
COMMENT ON TABLE :schemaname.gm_journeypatterns IS 'message journeypatterns relationships';
COMMENT ON COLUMN :schemaname.gm_journeypatterns.gm_id IS 'message';
COMMENT ON COLUMN :schemaname.gm_journeypatterns.journeypattern_id IS 'attached journeypattern';

CREATE TABLE :schemaname.gm_lines (
    gm_id bigint NOT NULL,
    line_id character varying(255) NOT NULL
);


ALTER TABLE :schemaname.gm_lines OWNER TO :username ;
COMMENT ON TABLE :schemaname.gm_lines IS 'message lines relationships';
COMMENT ON COLUMN :schemaname.gm_lines.gm_id IS 'message';
COMMENT ON COLUMN :schemaname.gm_lines.line_id IS 'attached line';

CREATE TABLE :schemaname.gm_linesections (
    gm_id bigint NOT NULL,
    line_id character varying(255) NOT NULL,
    start_id character varying(255) NOT NULL,
    end_id character varying(255) NOT NULL
);


ALTER TABLE :schemaname.gm_linesections OWNER TO :username ;
COMMENT ON TABLE :schemaname.gm_linesections IS 'message line sections relationships';
COMMENT ON COLUMN :schemaname.gm_linesections.gm_id IS 'message';
COMMENT ON COLUMN :schemaname.gm_linesections.line_id IS 'attached line';
COMMENT ON COLUMN :schemaname.gm_linesections.start_id IS 'start of section';
COMMENT ON COLUMN :schemaname.gm_linesections.end_id IS 'end of section';

CREATE TABLE :schemaname.gm_message (
    gm_id bigint NOT NULL,
    language character varying(2) DEFAULT 'FR'::character varying NOT NULL,
    type character varying(20) NOT NULL,
    text character varying(1000) NOT NULL
);

ALTER TABLE :schemaname.gm_message OWNER TO :username ;
COMMENT ON TABLE :schemaname.gm_message IS 'effective messages';
COMMENT ON COLUMN :schemaname.gm_message.gm_id IS 'message';
COMMENT ON COLUMN :schemaname.gm_message.language IS 'coded language : FR, EN, DE, ...';
COMMENT ON COLUMN :schemaname.gm_message.type IS 'shortMessage, longMessage or codedMessage';
COMMENT ON COLUMN :schemaname.gm_message.text IS 'text or code';


CREATE TABLE :schemaname.gm_routes (
    gm_id bigint NOT NULL,
    route_id character varying(255) NOT NULL
);


ALTER TABLE :schemaname.gm_routes OWNER TO :username;
COMMENT ON TABLE :schemaname.gm_routes IS 'message routes relationships';
COMMENT ON COLUMN :schemaname.gm_routes.gm_id IS 'message';
COMMENT ON COLUMN :schemaname.gm_routes.route_id IS 'attached route';

CREATE TABLE :schemaname.gm_stopareas (
    gm_id bigint NOT NULL,
    stoparea_id character varying(255) NOT NULL
);


ALTER TABLE :schemaname.gm_stopareas OWNER TO :username ;

COMMENT ON TABLE :schemaname.gm_stopareas IS 'message stops relationships';
COMMENT ON COLUMN :schemaname.gm_stopareas.gm_id IS 'message';
COMMENT ON COLUMN :schemaname.gm_stopareas.stoparea_id IS 'attached stop area';

CREATE TABLE :schemaname.parameter (
    name character varying NOT NULL,
    value character varying NOT NULL
);


ALTER TABLE :schemaname.parameter OWNER TO :username ;


CREATE TABLE :schemaname.vehicle (
    id bigint NOT NULL,
    application_date date NOT NULL,
    objectid character varying(255) NOT NULL,
    objectversion integer,
    creationtime timestamp without time zone,
    creatorid character varying(255),
    vehicletypeidentifier character varying(255),
    statusvalue character varying(255),
    incongestion boolean,
    inpanic boolean,
    longitude numeric(19,16),
    latitude numeric(19,16),
    longlattype character varying(255),
    x numeric(19,2),
    y numeric(19,2),
    projectiontype character varying(255),
    ismonitored boolean DEFAULT true,
    monitoringerror character varying(255),
    bearing numeric(19,16),
    delay time without time zone
);


ALTER TABLE :schemaname.vehicle OWNER TO :username ;

COMMENT ON TABLE :schemaname.vehicle IS 'Vehicle';
COMMENT ON COLUMN :schemaname.vehicle.id IS 'Internal identification';
COMMENT ON COLUMN :schemaname.vehicle.application_date IS 'Applicable date';
COMMENT ON COLUMN :schemaname.vehicle.objectid IS 'Neptune identification';
COMMENT ON COLUMN :schemaname.vehicle.objectversion IS 'Version of this object';
COMMENT ON COLUMN :schemaname.vehicle.creationtime IS 'Creation date and time';
COMMENT ON COLUMN :schemaname.vehicle.creatorid IS 'Creator identification';
COMMENT ON COLUMN :schemaname.vehicle.vehicletypeidentifier IS 'Vehicle type';
COMMENT ON COLUMN :schemaname.vehicle.statusvalue IS 'Service Status';
COMMENT ON COLUMN :schemaname.vehicle.incongestion IS 'indicate if vehicle is in a traffic congestion';
COMMENT ON COLUMN :schemaname.vehicle.inpanic IS 'indicate if alarm is on';
COMMENT ON COLUMN :schemaname.vehicle.longitude IS 'Longitude';
COMMENT ON COLUMN :schemaname.vehicle.latitude IS 'Latitude';
COMMENT ON COLUMN :schemaname.vehicle.longlattype IS 'Model used for Longitude and Latitude (Standard, WGS84 or WGS92)';
COMMENT ON COLUMN :schemaname.vehicle.x IS 'X coordinate';
COMMENT ON COLUMN :schemaname.vehicle.y IS 'Y coordinate';
COMMENT ON COLUMN :schemaname.vehicle.projectiontype IS 'Projection used for coordinates (epsg code)';
COMMENT ON COLUMN :schemaname.vehicle.ismonitored IS 'indicate if bus is localised';
COMMENT ON COLUMN :schemaname.vehicle.monitoringerror IS 'if ismonitored is false : gives a error message';
COMMENT ON COLUMN :schemaname.vehicle.bearing IS 'absolute bearing of the bus';
COMMENT ON COLUMN :schemaname.vehicle.delay IS 'delay value (negative when early)';

CREATE SEQUENCE :schemaname.vehicle_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

ALTER TABLE :schemaname.vehicle_id_seq OWNER TO :username ;

ALTER SEQUENCE :schemaname.vehicle_id_seq OWNED BY vehicle.id;

CREATE TABLE :schemaname.vehicleservice (
    id bigint NOT NULL,
    application_date date NOT NULL,
    objectid character varying(255) NOT NULL,
    objectversion integer,
    creationtime timestamp without time zone,
    creatorid character varying(255),
    vehicleid bigint
);


ALTER TABLE :schemaname.vehicleservice OWNER TO :username ;

COMMENT ON TABLE :schemaname.vehicleservice IS 'Vehicle Service';
COMMENT ON COLUMN :schemaname.vehicleservice.id IS 'Internal identification';
COMMENT ON COLUMN :schemaname.vehicleservice.application_date IS 'Applicable date';
COMMENT ON COLUMN :schemaname.vehicleservice.objectid IS 'Neptune identification';
COMMENT ON COLUMN :schemaname.vehicleservice.objectversion IS 'Version of this object';
COMMENT ON COLUMN :schemaname.vehicleservice.creationtime IS 'Creation date and time';
COMMENT ON COLUMN :schemaname.vehicleservice.creatorid IS 'Creator identification';
COMMENT ON COLUMN :schemaname.vehicleservice.vehicleid IS 'Vehicle executing this service';

CREATE SEQUENCE :schemaname.vehicleservice_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

ALTER TABLE :schemaname.vehicleservice_id_seq OWNER TO :username ;

ALTER SEQUENCE :schemaname.vehicleservice_id_seq OWNED BY vehicleservice.id;

ALTER TABLE :schemaname.datedvehiclejourney ALTER COLUMN id SET DEFAULT nextval(':schemaname.datedvehiclejourney_id_seq'::regclass);

ALTER TABLE :schemaname.general_message ALTER COLUMN id SET DEFAULT nextval(':schemaname.general_message_id_seq'::regclass);

ALTER TABLE :schemaname.vehicle ALTER COLUMN id SET DEFAULT nextval(':schemaname.vehicle_id_seq'::regclass);

ALTER TABLE :schemaname.vehicleservice ALTER COLUMN id SET DEFAULT nextval(':schemaname.vehicleservice_id_seq'::regclass);

ALTER TABLE ONLY :schemaname.general_message
    ADD CONSTRAINT "PK_general_message" PRIMARY KEY (id);

ALTER TABLE ONLY :schemaname.gm_journeypatterns
    ADD CONSTRAINT "PK_gm_journeypatterns" PRIMARY KEY (gm_id, journeypattern_id);

ALTER TABLE ONLY :schemaname.gm_lines
    ADD CONSTRAINT "PK_gm_lines" PRIMARY KEY (gm_id, line_id);

ALTER TABLE ONLY :schemaname.gm_linesections
    ADD CONSTRAINT "PK_gm_linesection" PRIMARY KEY (gm_id, line_id, start_id, end_id);

ALTER TABLE ONLY :schemaname.gm_routes
    ADD CONSTRAINT "PK_gm_routes" PRIMARY KEY (gm_id, route_id);

ALTER TABLE ONLY :schemaname.gm_stopareas
    ADD CONSTRAINT "PK_gm_stopareas" PRIMARY KEY (gm_id, stoparea_id);

ALTER TABLE ONLY :schemaname.datedcall
    ADD CONSTRAINT datedcall_pkey PRIMARY KEY (datedvehiclejourneyid, stoppointid);

ALTER TABLE ONLY :schemaname.datedvehiclejourney
    ADD CONSTRAINT datedvehiclejourney_pkey PRIMARY KEY (id);

ALTER TABLE ONLY :schemaname.datedvehiclejourney
    ADD CONSTRAINT dvj_objectid_ukey UNIQUE (objectid, application_date);

ALTER TABLE ONLY :schemaname.parameter
    ADD CONSTRAINT pk_parameter PRIMARY KEY (name);

ALTER TABLE ONLY :schemaname.vehicle
    ADD CONSTRAINT vehicle_pkey PRIMARY KEY (id);

ALTER TABLE ONLY :schemaname.vehicleservice
    ADD CONSTRAINT vehicleservice_pkey PRIMARY KEY (id);

ALTER TABLE ONLY :schemaname.vehicle
    ADD CONSTRAINT vh_objectid_ukey UNIQUE (objectid, application_date);

ALTER TABLE ONLY :schemaname.vehicleservice
    ADD CONSTRAINT vs_objectid_ukey UNIQUE (objectid, application_date);

CREATE INDEX fki_gm_m_fkey ON :schemaname.gm_message USING btree (gm_id);

ALTER TABLE ONLY :schemaname.gm_journeypatterns
    ADD CONSTRAINT "FK_gm_journeypatterns_gm" FOREIGN KEY (gm_id) REFERENCES :schemaname.general_message(id);

ALTER TABLE ONLY :schemaname.gm_lines
    ADD CONSTRAINT "FK_gm_lines_gm" FOREIGN KEY (gm_id) REFERENCES :schemaname.general_message(id);

ALTER TABLE ONLY :schemaname.gm_linesections
    ADD CONSTRAINT "FK_gm_linesection_gm" FOREIGN KEY (gm_id) REFERENCES :schemaname.general_message(id);

ALTER TABLE ONLY :schemaname.gm_routes
    ADD CONSTRAINT "FK_gm_routes_gm" FOREIGN KEY (gm_id) REFERENCES :schemaname.general_message(id);

ALTER TABLE ONLY :schemaname.gm_stopareas
    ADD CONSTRAINT "FK_gm_stoparea_gm" FOREIGN KEY (gm_id) REFERENCES :schemaname.general_message(id);

ALTER TABLE ONLY :schemaname.datedcall
    ADD CONSTRAINT dc_dvj_fkey FOREIGN KEY (datedvehiclejourneyid) REFERENCES :schemaname.datedvehiclejourney(id) ON DELETE CASCADE;

ALTER TABLE ONLY :schemaname.datedvehiclejourney
    ADD CONSTRAINT dvj_service_fkey FOREIGN KEY (serviceid) REFERENCES :schemaname.vehicleservice(id);

ALTER TABLE ONLY :schemaname.gm_message
    ADD CONSTRAINT gm_m_fkey FOREIGN KEY (gm_id) REFERENCES :schemaname.general_message(id) ON DELETE CASCADE;

ALTER TABLE ONLY :schemaname.vehicleservice
    ADD CONSTRAINT vehicle_fkey FOREIGN KEY (vehicleid) REFERENCES :schemaname.vehicle(id) ON DELETE CASCADE;

