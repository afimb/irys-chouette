delete from :schemaname.datedvehiclejourney;

insert into :schemaname.datedvehiclejourney (application_date,lineid,routeid,journeypatternid,vehiclejourneyid,objectid,objectversion,creationtime,number,companyid) 
	select current_date,l.objectid,r.objectid,j.objectid,v.objectid,v.objectid,1,current_timestamp,v.number,c.objectid 
	from :chouette.vehiclejourney v join :chouette.journeypattern j on (j.id = v.journeypatternid) 
	join :chouette.route r on (r.id = v.routeid) join :chouette.line l on (r.lineid = l.id) 
	join :chouette.company c on (c.id = l.companyid) 
	order by v.objectid asc;

insert into :schemaname.datedcall (datedvehiclejourneyid,stoppointid,status,isDeparture,position,isArrival, 
	aimedarrivaltime,aimeddeparturetime,expectedarrivaltime,expecteddeparturetime) 
	select dvj.id,s.objectid,'ok',vjas.isdeparture,vjas.position,vjas.isarrival,current_date+vjas.arrivaltime,
	       current_date+vjas.departuretime,current_date+vjas.arrivaltime,current_date+vjas.departuretime 
	from :chouette.vehiclejourneyatstop vjas join :chouette.vehiclejourney vj on (vjas.vehiclejourneyid = vj.id) 
	join :schemaname.datedvehiclejourney dvj on (dvj.objectid = vj.objectid) join :chouette.stoppoint s on (vjas.stoppointid = s.id) ;
	
	 
insert into :schemaname.general_message (infochannel,version,creation_date,last_modification_date,valid_until_date,status,objectid)
	values ('Information',1,current_timestamp,current_timestamp,current_timestamp + interval '1 hour','OK','NINOXE:GeneralMessage:1');
insert into :schemaname.gm_message (gm_id,language,type,text) 
   select id ,'FR','shortMessage','Info courte' from :schemaname.general_message gm where gm.objectid = 'NINOXE:GeneralMessage:1' ;
insert into :schemaname.gm_message (gm_id,language,type,text) 
   select id ,'FR','longMessage','Information longue' from :schemaname.general_message gm where gm.objectid = 'NINOXE:GeneralMessage:1' ;
insert into :schemaname.gm_message (gm_id,language,type,text) 
   select id ,'EN','shortMessage','Short info' from :schemaname.general_message gm where gm.objectid = 'NINOXE:GeneralMessage:1' ;
insert into :schemaname.gm_lines (gm_id,line_id) 
   select id,'NINOXE:Line:15577792' from :schemaname.general_message gm where gm.objectid = 'NINOXE:GeneralMessage:1' ;
insert into :schemaname.gm_lines (gm_id,line_id) 
   select id,'NINOXE:Line:15625451' from :schemaname.general_message gm where gm.objectid = 'NINOXE:GeneralMessage:1' ;

     
insert into :schemaname.general_message (infochannel,version,creation_date,last_modification_date,valid_until_date,status,objectid)
	values ('Perturbation',2,current_timestamp,current_timestamp,NULL,'OK','NINOXE:GeneralMessage:2');
insert into :schemaname.gm_message (gm_id,language,type,text) 
   select id ,'FR','shortMessage','Perturbation courte' from :schemaname.general_message gm where gm.objectid = 'NINOXE:GeneralMessage:2' ;
insert into :schemaname.gm_message (gm_id,language,type,text) 
   select id ,'FR','longMessage','Perturbation longue' from :schemaname.general_message gm where gm.objectid = 'NINOXE:GeneralMessage:2' ;
insert into :schemaname.gm_lines (gm_id,line_id) 
   select id,'NINOXE:Line:15577792' from :schemaname.general_message gm where gm.objectid = 'NINOXE:GeneralMessage:2' ;
insert into :schemaname.gm_stopareas (gm_id,stoparea_id) 
   select id,'NINOXE:StopArea:15571500' from :schemaname.general_message gm where gm.objectid = 'NINOXE:GeneralMessage:2' ;
insert into :schemaname.gm_stopareas (gm_id,stoparea_id) 
   select id,'NINOXE:StopArea:15577806' from :schemaname.general_message gm where gm.objectid = 'NINOXE:GeneralMessage:2' ;


insert into :schemaname.general_message (infochannel,version,creation_date,last_modification_date,valid_until_date,status,objectid)
	values ('Commercial',1,current_timestamp,current_timestamp,NULL,'OK','NINOXE:GeneralMessage:3');
insert into :schemaname.gm_message (gm_id,language,type,text) 
   select id ,'FR','shortMessage','Commercial court' from :schemaname.general_message gm where gm.objectid = 'NINOXE:GeneralMessage:3' ;
insert into :schemaname.gm_message (gm_id,language,type,text) 
   select id ,'FR','longMessage','Commercial long' from :schemaname.general_message gm where gm.objectid = 'NINOXE:GeneralMessage:3' ;

insert into :schemaname.general_message (infochannel,version,creation_date,last_modification_date,valid_until_date,status,objectid)
	values ('Information',1,current_timestamp - interval '2 hour',current_timestamp - interval '2 hour',current_timestamp,'OK','NINOXE:GeneralMessage:4');
insert into :schemaname.gm_message (gm_id,language,type,text) 
   select id ,'FR','shortMessage','Info courte perimee' from :schemaname.general_message gm where gm.objectid = 'NINOXE:GeneralMessage:4' ;
insert into :schemaname.gm_message (gm_id,language,type,text) 
   select id ,'FR','longMessage','Info longue perimee' from :schemaname.general_message gm where gm.objectid = 'NINOXE:GeneralMessage:4' ;
	
