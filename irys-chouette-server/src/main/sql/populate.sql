delete from :schemaname.dated_vehicle_journeys;

insert into :schemaname.dated_vehicle_journeys (application_date,last_modification_date,line_id,route_id,journey_pattern_id,vehicle_journey_id,objectid,object_version,creation_time,number,company_id) 
	select current_date,current_timestamp,l.objectid,r.objectid,j.objectid,v.objectid,v.objectid,1,current_timestamp,v.number,c.objectid 
	from :chouette.vehicle_journeys v 
	join :chouette.journey_patterns j on (j.id = v.journey_pattern_id) 
	join :chouette.routes r on (r.id = v.route_id) join :chouette.lines l on (r.line_id = l.id) 
	join :chouette.companies c on (c.id = l.company_id) 
	order by v.objectid asc;

insert into :schemaname.dated_calls (dated_vehicle_journey_id,stop_point_id,departure_status,arrival_status,is_Departure,position,is_Arrival, 
	aimed_arrival_time,aimed_departure_time,expected_arrival_time,expected_departure_time) 
	select dvj.id,s.objectid,'ok','ok',s.id = j.departure_stop_point_id,s.position,s.id = j.arrival_stop_point_id,current_date+vjas.arrival_time,
	       current_date+vjas.departure_time,current_date+vjas.arrival_time,current_date+vjas.departure_time 
	from :chouette.vehicle_journey_at_stops vjas 
	join :chouette.vehicle_journeys vj on (vjas.vehicle_journey_id = vj.id) 
	join :schemaname.dated_vehicle_journeys dvj on (dvj.objectid = vj.objectid) 
	join :chouette.journey_patterns j on (j.id = vj.journey_pattern_id) 
	join :chouette.stop_points s on (vjas.stop_point_id = s.id) ;
	
	
	 
