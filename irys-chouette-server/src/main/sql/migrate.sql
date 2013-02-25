set search_path to :schemaname; 

ALTER TABLE vehicle ADD COLUMN currentvehiclejourneyid bigint;
COMMENT ON COLUMN vehicle.currentvehiclejourneyid IS 'current vehicle journey';

ALTER TABLE vehicle ADD COLUMN currentstopid character varying(255);
COMMENT ON COLUMN vehicle.currentstopid IS 'current or next stop ';

ALTER TABLE ONLY vehicle
  ADD CONSTRAINT vehicle_dvj_fkey FOREIGN KEY (currentvehiclejourneyid)  REFERENCES :schemaname.datedvehiclejourney (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE SET NULL;

-- separe status
      
ALTER TABLE ONLY datedcall RENAME COLUMN status TO departurestatus;      
ALTER TABLE ONLY  datedcall ADD COLUMN arrivalstatus character varying(255);
COMMENT ON COLUMN  datedcall.arrivalstatus IS 'ontime, arrived, canceled, ...';


-- modification times

ALTER TABLE datedcall ADD COLUMN last_modification_date timestamp without time zone;
COMMENT ON COLUMN datedcall.last_modification_date IS 'last modification date';

ALTER TABLE datedvehiclejourney ADD COLUMN last_modification_date timestamp without time zone;
COMMENT ON COLUMN datedvehiclejourney.last_modification_date IS 'last modification date';

ALTER TABLE vehicle ADD COLUMN last_modification_date timestamp without time zone;
COMMENT ON COLUMN vehicle.last_modification_date IS 'last modification date';

