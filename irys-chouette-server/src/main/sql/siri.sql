
CREATE SCHEMA :schemaname ;


ALTER SCHEMA :schemaname OWNER TO :username ;

SET search_path TO :schemaname ;

CREATE TABLE datedcall (
    datedvehiclejourneyid bigint NOT NULL,
    stoppointid character varying(255) NOT NULL,
    last_modification_date timestamp without time zone,
    departurestatus character varying(255),
    arrivalstatus character varying(255),
    isdeparture boolean,
    "position" bigint,
    isarrival boolean DEFAULT false,
    expectedarrivaltime timestamp without time zone,
    expecteddeparturetime timestamp without time zone,
    aimedarrivaltime timestamp without time zone,
    aimeddeparturetime timestamp without time zone
);


ALTER TABLE datedcall OWNER TO :username;

COMMENT ON TABLE datedcall IS 'Vehicle Journey at Stop';
COMMENT ON COLUMN datedcall.datedvehiclejourneyid IS 'DatedVehicleJourney foreign key (1st part of primary key)';
COMMENT ON COLUMN datedcall.stoppointid IS 'StopPoint objectId as foreign key (2nd part of primary key)';
COMMENT ON COLUMN datedcall.last_modification_date IS 'last modification date';
COMMENT ON COLUMN datedcall.departurestatus IS 'ontime, arrived, canceled, ...';
COMMENT ON COLUMN datedcall.arrivalstatus IS 'ontime, arrived, canceled, ...';
COMMENT ON COLUMN datedcall.isdeparture IS 'First stop of the vehicle journey';
COMMENT ON COLUMN datedcall."position" IS 'order in JourneyPattern';
COMMENT ON COLUMN datedcall.isarrival IS 'Last stop of the vehicle journey';
COMMENT ON COLUMN datedcall.expectedarrivaltime IS 'Real Time Arrival time';
COMMENT ON COLUMN datedcall.expecteddeparturetime IS 'Real Time Departure time';
COMMENT ON COLUMN datedcall.aimedarrivaltime IS 'Theorical Arrival time';
COMMENT ON COLUMN datedcall.aimeddeparturetime IS 'Theorical Departure time';

CREATE TABLE datedvehiclejourney (
    id bigint NOT NULL,
    application_date date NOT NULL,
    last_modification_date timestamp without time zone,
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


ALTER TABLE datedvehiclejourney OWNER TO :username ;
COMMENT ON TABLE datedvehiclejourney IS 'Vehicle journey';
COMMENT ON COLUMN datedvehiclejourney.id IS 'Internal identification';
COMMENT ON COLUMN datedvehiclejourney.application_date IS 'Applicable date';
COMMENT ON COLUMN datedvehiclejourney.last_modification_date IS 'last modification date';
COMMENT ON COLUMN datedvehiclejourney.lineid IS 'Line objectId as foreign key ';
COMMENT ON COLUMN datedvehiclejourney.routeid IS 'Route objectId as foreign key';
COMMENT ON COLUMN datedvehiclejourney.journeypatternid IS 'JourneyPattern objectId as foreign key';
COMMENT ON COLUMN datedvehiclejourney.vehiclejourneyid IS 'VehicleJourney objectId as foreign key';
COMMENT ON COLUMN datedvehiclejourney.objectid IS 'Neptune identification';
COMMENT ON COLUMN datedvehiclejourney.objectversion IS 'Version of this object';
COMMENT ON COLUMN datedvehiclejourney.creationtime IS 'Creation date and time';
COMMENT ON COLUMN datedvehiclejourney.creatorid IS 'Creator identification';
COMMENT ON COLUMN datedvehiclejourney.publishedjourneyname IS 'Name for travelers';
COMMENT ON COLUMN datedvehiclejourney.publishedjourneyidentifier IS 'Identifier for travelers';
COMMENT ON COLUMN datedvehiclejourney.transportmode IS 'Transport mode';
COMMENT ON COLUMN datedvehiclejourney.vehicletypeidentifier IS 'Vehicle type';
COMMENT ON COLUMN datedvehiclejourney.statusvalue IS 'Service Status';
COMMENT ON COLUMN datedvehiclejourney.facility IS 'facility';
COMMENT ON COLUMN datedvehiclejourney.number IS 'Number of the vehicle journey';
COMMENT ON COLUMN datedvehiclejourney.comment IS 'Comment';
COMMENT ON COLUMN datedvehiclejourney.companyid IS 'Company objectId as foreign key';
COMMENT ON COLUMN datedvehiclejourney.serviceid IS 'vehicle service attached to this vehiclejourney';
COMMENT ON COLUMN datedvehiclejourney.serviceorder IS 'order of vehiclejourney in service (0 = unknown)';


CREATE SEQUENCE datedvehiclejourney_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE datedvehiclejourney_id_seq OWNER TO :username ;
ALTER SEQUENCE datedvehiclejourney_id_seq OWNED BY datedvehiclejourney.id;


SET default_with_oids = true;
CREATE TABLE general_message (
    id bigint NOT NULL,
    infochannel character varying(20) NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    creation_date timestamp without time zone NOT NULL,
    last_modification_date timestamp without time zone NOT NULL,
    valid_until_date timestamp without time zone,
    status character varying(3) DEFAULT 'OK'::character varying NOT NULL,
    objectid character varying(255)
);


ALTER TABLE general_message OWNER TO :username;
COMMENT ON TABLE general_message IS 'messages for general message service';
COMMENT ON COLUMN general_message.id IS 'message id';
COMMENT ON COLUMN general_message.infochannel IS 'Information Channel : Information, Commercial or Perturbation';
COMMENT ON COLUMN general_message.version IS 'message version';
COMMENT ON COLUMN general_message.creation_date IS 'creation date';
COMMENT ON COLUMN general_message.last_modification_date IS 'last modification date';
COMMENT ON COLUMN general_message.valid_until_date IS 'expiration date';

CREATE SEQUENCE general_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE general_message_id_seq OWNER TO :username ;
ALTER SEQUENCE general_message_id_seq OWNED BY general_message.id;


CREATE TABLE gm_journeypatterns (
    gm_id bigint NOT NULL,
    journeypattern_id character varying(255) NOT NULL
);


ALTER TABLE gm_journeypatterns OWNER TO :username ;
COMMENT ON TABLE gm_journeypatterns IS 'message journeypatterns relationships';
COMMENT ON COLUMN gm_journeypatterns.gm_id IS 'message';
COMMENT ON COLUMN gm_journeypatterns.journeypattern_id IS 'attached journeypattern';

CREATE TABLE gm_lines (
    gm_id bigint NOT NULL,
    line_id character varying(255) NOT NULL
);


ALTER TABLE gm_lines OWNER TO :username ;
COMMENT ON TABLE gm_lines IS 'message lines relationships';
COMMENT ON COLUMN gm_lines.gm_id IS 'message';
COMMENT ON COLUMN gm_lines.line_id IS 'attached line';

CREATE TABLE gm_linesections (
    gm_id bigint NOT NULL,
    line_id character varying(255) NOT NULL,
    start_id character varying(255) NOT NULL,
    end_id character varying(255) NOT NULL
);


ALTER TABLE gm_linesections OWNER TO :username ;
COMMENT ON TABLE gm_linesections IS 'message line sections relationships';
COMMENT ON COLUMN gm_linesections.gm_id IS 'message';
COMMENT ON COLUMN gm_linesections.line_id IS 'attached line';
COMMENT ON COLUMN gm_linesections.start_id IS 'start of section';
COMMENT ON COLUMN gm_linesections.end_id IS 'end of section';

CREATE TABLE gm_message (
    gm_id bigint NOT NULL,
    language character varying(2) DEFAULT 'FR'::character varying NOT NULL,
    type character varying(20) NOT NULL,
    text character varying(1000) NOT NULL
);

ALTER TABLE gm_message OWNER TO :username ;
COMMENT ON TABLE gm_message IS 'effective messages';
COMMENT ON COLUMN gm_message.gm_id IS 'message';
COMMENT ON COLUMN gm_message.language IS 'coded language : FR, EN, DE, ...';
COMMENT ON COLUMN gm_message.type IS 'shortMessage, longMessage or codedMessage';
COMMENT ON COLUMN gm_message.text IS 'text or code';


CREATE TABLE gm_routes (
    gm_id bigint NOT NULL,
    route_id character varying(255) NOT NULL
);


ALTER TABLE gm_routes OWNER TO :username;
COMMENT ON TABLE gm_routes IS 'message routes relationships';
COMMENT ON COLUMN gm_routes.gm_id IS 'message';
COMMENT ON COLUMN gm_routes.route_id IS 'attached route';

CREATE TABLE gm_stopareas (
    gm_id bigint NOT NULL,
    stoparea_id character varying(255) NOT NULL
);


ALTER TABLE gm_stopareas OWNER TO :username ;

COMMENT ON TABLE gm_stopareas IS 'message stops relationships';
COMMENT ON COLUMN gm_stopareas.gm_id IS 'message';
COMMENT ON COLUMN gm_stopareas.stoparea_id IS 'attached stop area';

CREATE TABLE parameter (
    name character varying NOT NULL,
    value character varying NOT NULL
);


ALTER TABLE parameter OWNER TO :username ;


CREATE TABLE vehicle (
    id bigint NOT NULL,
    application_date date NOT NULL,
    last_modification_date timestamp without time zone,
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
    delay bigint,
    linkdistance bigint,
    linkpercentage numeric(19,16),
    message character varying(255),
    currentvehiclejourneyid bigint,
    currentstopid character varying(255)
);


ALTER TABLE vehicle OWNER TO :username ;

COMMENT ON TABLE vehicle IS 'Vehicle';
COMMENT ON COLUMN vehicle.id IS 'Internal identification';
COMMENT ON COLUMN vehicle.application_date IS 'Applicable date';
COMMENT ON COLUMN vehicle.last_modification_date IS 'last modification date';
COMMENT ON COLUMN vehicle.objectid IS 'Neptune identification';
COMMENT ON COLUMN vehicle.objectversion IS 'Version of this object';
COMMENT ON COLUMN vehicle.creationtime IS 'Creation date and time';
COMMENT ON COLUMN vehicle.creatorid IS 'Creator identification';
COMMENT ON COLUMN vehicle.vehicletypeidentifier IS 'Vehicle type';
COMMENT ON COLUMN vehicle.statusvalue IS 'Service Status';
COMMENT ON COLUMN vehicle.incongestion IS 'indicate if vehicle is in a traffic congestion';
COMMENT ON COLUMN vehicle.inpanic IS 'indicate if alarm is on';
COMMENT ON COLUMN vehicle.longitude IS 'Longitude';
COMMENT ON COLUMN vehicle.latitude IS 'Latitude';
COMMENT ON COLUMN vehicle.longlattype IS 'Model used for Longitude and Latitude (Standard, WGS84 or WGS92)';
COMMENT ON COLUMN vehicle.x IS 'X coordinate';
COMMENT ON COLUMN vehicle.y IS 'Y coordinate';
COMMENT ON COLUMN vehicle.projectiontype IS 'Projection used for coordinates (epsg code)';
COMMENT ON COLUMN vehicle.ismonitored IS 'indicate if bus is localised';
COMMENT ON COLUMN vehicle.monitoringerror IS 'if ismonitored is false : gives a error message';
COMMENT ON COLUMN vehicle.bearing IS 'absolute bearing of the bus';
COMMENT ON COLUMN vehicle.delay IS 'delay value in seconds :negative when early, null if unknown';
COMMENT ON COLUMN vehicle.linkdistance IS 'distance between last and next stop in meters, null if unknown';
COMMENT ON COLUMN vehicle.linkpercentage IS 'percentage of link already covered by vehicle, null if unknown';
COMMENT ON COLUMN vehicle.message IS 'message about vehicle, null if unknown';
COMMENT ON COLUMN vehicle.currentvehiclejourneyid IS 'current vehicle journey';
COMMENT ON COLUMN vehicle.currentstopid IS 'current or next stop ';

CREATE SEQUENCE vehicle_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

ALTER TABLE vehicle_id_seq OWNER TO :username ;

ALTER SEQUENCE vehicle_id_seq OWNED BY vehicle.id;

CREATE TABLE vehicleservice (
    id bigint NOT NULL,
    application_date date NOT NULL,
    objectid character varying(255) NOT NULL,
    objectversion integer,
    creationtime timestamp without time zone,
    creatorid character varying(255),
    vehicleid bigint
);


ALTER TABLE vehicleservice OWNER TO :username ;

COMMENT ON TABLE vehicleservice IS 'Vehicle Service';
COMMENT ON COLUMN vehicleservice.id IS 'Internal identification';
COMMENT ON COLUMN vehicleservice.application_date IS 'Applicable date';
COMMENT ON COLUMN vehicleservice.objectid IS 'Neptune identification';
COMMENT ON COLUMN vehicleservice.objectversion IS 'Version of this object';
COMMENT ON COLUMN vehicleservice.creationtime IS 'Creation date and time';
COMMENT ON COLUMN vehicleservice.creatorid IS 'Creator identification';
COMMENT ON COLUMN vehicleservice.vehicleid IS 'Vehicle executing this service';

CREATE SEQUENCE vehicleservice_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

ALTER TABLE vehicleservice_id_seq OWNER TO :username ;

ALTER SEQUENCE vehicleservice_id_seq OWNED BY vehicleservice.id;

ALTER TABLE datedvehiclejourney ALTER COLUMN id SET DEFAULT nextval('datedvehiclejourney_id_seq'::regclass);

ALTER TABLE general_message ALTER COLUMN id SET DEFAULT nextval('general_message_id_seq'::regclass);

ALTER TABLE vehicle ALTER COLUMN id SET DEFAULT nextval('vehicle_id_seq'::regclass);

ALTER TABLE vehicleservice ALTER COLUMN id SET DEFAULT nextval('vehicleservice_id_seq'::regclass);

ALTER TABLE ONLY general_message
    ADD CONSTRAINT "PK_general_message" PRIMARY KEY (id);

ALTER TABLE ONLY gm_journeypatterns
    ADD CONSTRAINT "PK_gm_journeypatterns" PRIMARY KEY (gm_id, journeypattern_id);

ALTER TABLE ONLY gm_lines
    ADD CONSTRAINT "PK_gm_lines" PRIMARY KEY (gm_id, line_id);

ALTER TABLE ONLY gm_linesections
    ADD CONSTRAINT "PK_gm_linesection" PRIMARY KEY (gm_id, line_id, start_id, end_id);

ALTER TABLE ONLY gm_routes
    ADD CONSTRAINT "PK_gm_routes" PRIMARY KEY (gm_id, route_id);

ALTER TABLE ONLY gm_stopareas
    ADD CONSTRAINT "PK_gm_stopareas" PRIMARY KEY (gm_id, stoparea_id);

ALTER TABLE ONLY datedcall
    ADD CONSTRAINT datedcall_pkey PRIMARY KEY (datedvehiclejourneyid, stoppointid);

ALTER TABLE ONLY datedvehiclejourney
    ADD CONSTRAINT datedvehiclejourney_pkey PRIMARY KEY (id);

ALTER TABLE ONLY datedvehiclejourney
    ADD CONSTRAINT dvj_objectid_ukey UNIQUE (objectid, application_date);

ALTER TABLE ONLY parameter
    ADD CONSTRAINT pk_parameter PRIMARY KEY (name);

ALTER TABLE ONLY vehicle
    ADD CONSTRAINT vehicle_pkey PRIMARY KEY (id);

ALTER TABLE ONLY vehicleservice
    ADD CONSTRAINT vehicleservice_pkey PRIMARY KEY (id);

ALTER TABLE ONLY vehicle
    ADD CONSTRAINT vh_objectid_ukey UNIQUE (objectid, application_date);

ALTER TABLE ONLY vehicleservice
    ADD CONSTRAINT vs_objectid_ukey UNIQUE (objectid, application_date);

CREATE INDEX fki_gm_m_fkey ON gm_message USING btree (gm_id);

ALTER TABLE ONLY gm_journeypatterns
    ADD CONSTRAINT "FK_gm_journeypatterns_gm" FOREIGN KEY (gm_id) REFERENCES general_message(id);

ALTER TABLE ONLY gm_lines
    ADD CONSTRAINT "FK_gm_lines_gm" FOREIGN KEY (gm_id) REFERENCES general_message(id);

ALTER TABLE ONLY gm_linesections
    ADD CONSTRAINT "FK_gm_linesection_gm" FOREIGN KEY (gm_id) REFERENCES general_message(id);

ALTER TABLE ONLY gm_routes
    ADD CONSTRAINT "FK_gm_routes_gm" FOREIGN KEY (gm_id) REFERENCES general_message(id);

ALTER TABLE ONLY gm_stopareas
    ADD CONSTRAINT "FK_gm_stoparea_gm" FOREIGN KEY (gm_id) REFERENCES general_message(id);

ALTER TABLE ONLY datedcall
    ADD CONSTRAINT dc_dvj_fkey FOREIGN KEY (datedvehiclejourneyid) REFERENCES datedvehiclejourney(id) ON DELETE CASCADE;

ALTER TABLE ONLY datedvehiclejourney
    ADD CONSTRAINT dvj_service_fkey FOREIGN KEY (serviceid) REFERENCES vehicleservice(id);

ALTER TABLE ONLY gm_message
    ADD CONSTRAINT gm_m_fkey FOREIGN KEY (gm_id) REFERENCES general_message(id) ON DELETE CASCADE;

ALTER TABLE ONLY vehicleservice
    ADD CONSTRAINT vehicle_fkey FOREIGN KEY (vehicleid) REFERENCES vehicle(id) ON DELETE CASCADE;

ALTER TABLE ONLY vehicle
  ADD CONSTRAINT vehicle_dvj_fkey FOREIGN KEY (currentvehiclejourneyid)  REFERENCES datedvehiclejourney (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE SET NULL;

