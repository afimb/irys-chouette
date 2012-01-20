ALTER TABLE siri.vehicle DROP COLUMN delay;

ALTER TABLE siri.vehicle ADD COLUMN delay bigint;
ALTER TABLE siri.vehicle ALTER COLUMN delay SET STORAGE PLAIN;
COMMENT ON COLUMN siri.vehicle.delay IS 'delay value in seconds :negative when early, null if unknown';
