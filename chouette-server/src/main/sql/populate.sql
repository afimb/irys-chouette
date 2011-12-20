delete from siri.datedvehiclejourney where date = current_date;

insert into siri.datedvehiclejourney (application_date,lineid,routeid,journeypatternid,vehiclejourneyid,objectid,objectversion,creationtime,number,companyid) 
	select current_date,l.objectid,r.objectid,j.objectid,v.objectid,v.objectid,1,current_timestamp,v.number,c.objectid 
	from chouette.vehiclejourney v join chouette.journeypattern j on (j.id = v.journeypatternid) 
	join chouette.route r on (r.id = v.routeid) join chouette.line l on (r.lineid = l.id) 
	join chouette.company c on (c.id = l.companyid) 
	order by v.objectid asc;

insert into siri.datedcall (datedvehiclejourneyid,stoppointid,status,isDeparture,position,isArrival, 
	aimedarrivaltime,aimeddeparturetime,expectedarrivaltime,expecteddeparturetime) 
	select dvj.id,s.objectid,'ok',vjas.isdeparture,vjas.position,vjas.isarrival,current_date+vjas.arrivaltime,
	       current_date+vjas.departuretime,current_date+vjas.arrivaltime,current_date+vjas.departuretime 
	from chouette.vehiclejourneyatstop vjas join chouette.vehiclejourney vj on (vjas.vehiclejourneyid = vj.id) 
	join siri.datedvehiclejourney dvj on (dvj.objectid = vj.objectid) join chouette.stoppoint s on (vjas.stoppointid = s.id) ;
	
	 
