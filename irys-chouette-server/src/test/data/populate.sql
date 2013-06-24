delete from :schemaname.dated_vehicle_journeys;

insert into :schemaname.dated_vehicle_journeys (application_date,last_modification_date,line_id,route_id,journey_pattern_id,vehicle_journey_id,
                                                objectid,object_version,creation_time,number,company_id) 
	select current_date,current_timestamp,l.id,r.id,j.id,v.id,v.objectid,1,current_timestamp,v.number,c.id 
	from :chouette.vehicle_journeys v 
	join :chouette.journey_patterns j on (j.id = v.journey_pattern_id) 
	join :chouette.routes r on (r.id = v.route_id) join :chouette.lines l on (r.line_id = l.id) 
	join :chouette.companies c on (c.id = l.company_id) 
	order by v.objectid asc;

insert into :schemaname.dated_calls (dated_vehicle_journey_id,stop_point_id,departure_status,arrival_status,is_departure,position,is_arrival, 
	aimed_arrival_time,aimed_departure_time,expected_arrival_time,expected_departure_time) 
	select dvj.id,s.id,'ok','ok',s.id = j.departure_stop_point_id,s.position,s.id = j.arrival_stop_point_id,current_date+vjas.arrival_time,
	       current_date+vjas.departure_time,current_date+vjas.arrival_time,current_date+vjas.departure_time 
	from :chouette.vehicle_journey_at_stops vjas 
	join :chouette.vehicle_journeys vj on (vjas.vehicle_journey_id = vj.id) 
	join :schemaname.dated_vehicle_journeys dvj on (dvj.objectid = vj.objectid) 
	join :chouette.journey_patterns j on (j.id = vj.journey_pattern_id) 
	join :chouette.stop_points s on (vjas.stop_point_id = s.id) ;
	
	 
delete from :schemaname.general_message_stop_areas;
delete from :schemaname.general_message_lines;
delete from :schemaname.general_message_messages;
delete from :schemaname.general_messages;

	 
insert into :schemaname.general_messages (info_channel,version,creation_date,last_modification_date,valid_until_date,status,objectid)
	values ('Information',1,current_timestamp,current_timestamp,current_timestamp + interval '1 hour','OK','NINOXE:GeneralMessage:1');
insert into :schemaname.general_message_messages (general_message_id,language,type,text) 
   select id ,'FR','shortMessage','Info courte' from :schemaname.general_messages gm where gm.objectid = 'NINOXE:GeneralMessage:1' ;
insert into :schemaname.general_message_messages (general_message_id,language,type,text) 
   select id ,'FR','longMessage','Information longue' from :schemaname.general_messages gm where gm.objectid = 'NINOXE:GeneralMessage:1' ;
insert into :schemaname.general_message_messages (general_message_id,language,type,text) 
   select id ,'EN','shortMessage','Short info' from :schemaname.general_messages gm where gm.objectid = 'NINOXE:GeneralMessage:1' ;
insert into :schemaname.general_message_lines (general_message_id,line_id) 
   select gm.id,l.id from :schemaname.general_messages gm , :chouette.lines l where gm.objectid = 'NINOXE:GeneralMessage:1' AND l.objectid = 'NINOXE:Line:15577792';
insert into :schemaname.general_message_lines (general_message_id,line_id) 
   select gm.id,l.id from :schemaname.general_messages gm , :chouette.lines l where gm.objectid = 'NINOXE:GeneralMessage:1' AND l.objectid = 'NINOXE:Line:15625451' ;

     
insert into :schemaname.general_messages (info_channel,version,creation_date,last_modification_date,valid_until_date,status,objectid)
	values ('Perturbation',2,current_timestamp,current_timestamp,NULL,'OK','NINOXE:GeneralMessage:2');
insert into :schemaname.general_message_messages (general_message_id,language,type,text) 
   select id ,'FR','shortMessage','Perturbation courte' from :schemaname.general_messages gm where gm.objectid = 'NINOXE:GeneralMessage:2' ;
insert into :schemaname.general_message_messages (general_message_id,language,type,text) 
   select id ,'FR','longMessage','Perturbation longue' from :schemaname.general_messages gm where gm.objectid = 'NINOXE:GeneralMessage:2' ;
insert into :schemaname.general_message_lines (general_message_id,line_id) 
   select gm.id,l.id from :schemaname.general_messages gm , :chouette.lines l where gm.objectid = 'NINOXE:GeneralMessage:2' AND l.objectid = 'NINOXE:Line:15577792';
insert into :schemaname.general_message_stop_areas (general_message_id,stop_area_id) 
   select gm.id,sa.id from :schemaname.general_messages gm, :chouette.stop_areas sa where gm.objectid = 'NINOXE:GeneralMessage:2' AND sa.objectid = 'NINOXE:StopArea:15571500';
insert into :schemaname.general_message_stop_areas (general_message_id,stop_area_id) 
   select gm.id,sa.id from :schemaname.general_messages gm, :chouette.stop_areas sa where gm.objectid = 'NINOXE:GeneralMessage:2' AND sa.objectid = 'NINOXE:StopArea:15577806';


insert into :schemaname.general_messages (info_channel,version,creation_date,last_modification_date,valid_until_date,status,objectid)
	values ('Commercial',1,current_timestamp,current_timestamp,NULL,'OK','NINOXE:GeneralMessage:3');
insert into :schemaname.general_message_messages (general_message_id,language,type,text) 
   select id ,'FR','shortMessage','Commercial court' from :schemaname.general_messages gm where gm.objectid = 'NINOXE:GeneralMessage:3' ;
insert into :schemaname.general_message_messages (general_message_id,language,type,text) 
   select id ,'FR','longMessage','Commercial long' from :schemaname.general_messages gm where gm.objectid = 'NINOXE:GeneralMessage:3' ;

insert into :schemaname.general_messages (info_channel,version,creation_date,last_modification_date,valid_until_date,status,objectid)
	values ('Information',1,current_timestamp - interval '2 hour',current_timestamp - interval '2 hour',current_timestamp,'OK','NINOXE:GeneralMessage:4');
insert into :schemaname.general_message_messages (general_message_id,language,type,text) 
   select id ,'FR','shortMessage','Info courte perimee' from :schemaname.general_messages gm where gm.objectid = 'NINOXE:GeneralMessage:4' ;
insert into :schemaname.general_message_messages (general_message_id,language,type,text) 
   select id ,'FR','longMessage','Info longue perimee' from :schemaname.general_messages gm where gm.objectid = 'NINOXE:GeneralMessage:4' ;
	
