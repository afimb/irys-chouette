//namespace
var DemoSiri = {
  //vars
  map: null,
  markersLayer: null,
  generalMessageUpdater: null,
  stopMonitoringUpdater: null,
  currentStopVisits: null,
  currentStopVisitId: null,
  colours: null,

  stopPointOnRouteMap: null,

  //files paths constants
 
  JSON_LINES_PATH: "tatrobus_lines.json",
  JSON_MARKERS_PATH: "tatrobus_stops.json",
  KML_LINES_PATH: "tatrobus_lines.kml",
  JSON_GENERAL_MESSAGE_PATH: "siri?action=generalMessage&format=json&InfoChannel=#{info_channel}&Language= ",
  JSON_STOP_MONITORING_PATH: "siri?action=stopMonitoring&format=json&StopId=#{stop_id}&LineId=&DestId=&OperatorId=&Start=&Preview=&TypeVisit=&MaxStop=&MinStLine=2&OnWard=0",

  //time constants
  GENERAL_MESSAGE_REFRESHING_PERIOD: 30, //in seconds
  STOP_MONITORING_REFRESHING_PERIOD: 30, //in seconds

  //styles
  stopPlacesStyles: new OpenLayers.StyleMap({
    "default": new OpenLayers.Style({
      externalGraphic: "images/bus-stop.png",
      graphicWidth: 20,
      graphicHeight: 32,
      graphicXOffset: -10,
      graphicYOffset: -32
    }),
    "select": new OpenLayers.Style({
      externalGraphic: "images/selectbus-stop.png",
      graphicWidth: 40,
      graphicHeight: 65,
      graphicXOffset: -20,
      graphicYOffset: -65
    }),
    "onward": new OpenLayers.Style({
      externalGraphic: "images/routebus-stop.png",
      graphicWidth: 20,
      graphicHeight: 32,
      graphicXOffset: -10,
      graphicYOffset: -32
    })
  })

};

function initialize() {
  initMap();
  initUpdaters();
  getColours();
}

function initMap() {
  var options = {
    maxResolution: 305.7481130859375,
    maxExtent: new OpenLayers.Bounds(556404.23807,5754752.766222,697048.37009,5895396.898242),
    restrictedExtent: new OpenLayers.Bounds(556404.23807,5754752.766222,697048.37009,5895396.898242)
  };
  DemoSiri.map = new OpenLayers.Map('map', options);
  var osmLayer = new OpenLayers.Layer.OSM("fond de carte");
  var kmlLayer = new OpenLayers.Layer.GML("lignes", DemoSiri.KML_LINES_PATH, 
               {
                format: OpenLayers.Format.KML, 
                formatOptions: {
                  extractStyles: true
                }
               });
  DemoSiri.markersLayer = new OpenLayers.Layer.Vector("arrêts", {styleMap: DemoSiri.stopPlacesStyles, rendererOptions: {yOrdering: true}});
  DemoSiri.map.addLayers([osmLayer, kmlLayer, DemoSiri.markersLayer]);
  DemoSiri.map.addControl(new OpenLayers.Control.ScaleLine({bottomInUnits: "", bottomOutUnits: ""}));
  DemoSiri.map.addControl(new OpenLayers.Control.LoadingPanel());

  //Create a select feature control to show popup on mouseover
  var highlightCtrl = new OpenLayers.Control.SelectFeature(DemoSiri.markersLayer, { 
    hover: true, highlightOnly: true, renderIntent: "",
    eventListeners: { featurehighlighted: showTooltipOnEvent, featureunhighlighted: hideTooltipOnEvent } });
  DemoSiri.map.addControl(highlightCtrl);
  highlightCtrl.activate();


  // Create a select feature control to show stopMonitoring on click
  var select = new OpenLayers.Control.SelectFeature(DemoSiri.markersLayer, {clickout: false});
  DemoSiri.map.addControl(select);
  select.activate();

  DemoSiri.markersLayer.events.on({"featureselected": selectMarkerOnEvent});

  OpenLayers.loadURL(DemoSiri.JSON_MARKERS_PATH,null,null,onJsonMarkersAjaxSuccess,null);
}

function selectMarkerOnEvent(event) {
  hideTooltipOnEvent(event);

  var stopPlaceInfos = event.feature.attributes;
  DemoSiri.stopMonitoringUpdater.stop();
  DemoSiri.stopMonitoringUpdater.url = DemoSiri.JSON_STOP_MONITORING_PATH.replace('#{stop_id}',stopPlaceInfos.siri_id);
  $$("#stop-messages .message-scroll-pane")[0].innerHTML="";
  DemoSiri.stopMonitoringUpdater.start();

  $$("#stop-messages h1")[0].innerHTML=stopPlaceInfos.name;
  $('stop-message-hint').hide();
  $('stop-messages').show();
    
  $('on-wards').hide();

  if($("StopAreaFilter").checked == true)
    updateGeneralMessage();
}

function showTooltipOnEvent(event){
  var feature = event.feature;
  if(feature.renderIntent != "select"){
    DemoSiri.map.addPopup(feature.popup);
  }
}

function hideTooltipOnEvent(event){
  var feature = event.feature;
  DemoSiri.map.removePopup(feature.popup);
}

function initUpdaters() {
  var generalMessageOptions = {method: 'get', onSuccess: populateGeneralMessages, requestHeaders: ['cache-control','no-cache','pragma','no-cache','expires','-1']};
  var stopMonitoringOptions = {method: 'get', onSuccess: populateStopMonitoring, requestHeaders: ['cache-control','no-cache','pragma','no-cache','expires','-1']};

  generalMessageOptions.frequency = DemoSiri.GENERAL_MESSAGE_REFRESHING_PERIOD;
  DemoSiri.generalMessageUpdater = new Ajax.PeriodicalUpdater('general_messages', getGeneralMessageUrl(), generalMessageOptions);
	
  stopMonitoringOptions.frequency = DemoSiri.STOP_MONITORING_REFRESHING_PERIOD;
  DemoSiri.stopMonitoringUpdater = new Ajax.PeriodicalUpdater('stop_monitoring', "" , stopMonitoringOptions);
}

function getColours() {
  if (!DemoSiri.colours) {
    new Ajax.Request(DemoSiri.JSON_LINES_PATH,
      {
        method:'get',
        onSuccess: function(transport){
          response = transport.responseText.evalJSON();
          DemoSiri.colours = response.colours;
        }
      });
  }
}

function onJsonMarkersAjaxSuccess(transport) {
  var format = new OpenLayers.Format.JSON();
  var data = format.read(transport.responseText);
  DemoSiri.stopPointOnRouteMap = data.stoppointmap;
  var stopPlacesMarkers = [];
  data.stopplaces.each(function(stopPlaceInfos){
    stopPlacesMarkers.push(createMarker(stopPlaceInfos));
  });
  DemoSiri.markersLayer.addFeatures(stopPlacesMarkers);
  DemoSiri.map.zoomToExtent(DemoSiri.markersLayer.getDataExtent());
}

function createMarker(stopPlaceInfos){
  var point = new OpenLayers.Geometry.Point(stopPlaceInfos.coordinates.lng,stopPlaceInfos.coordinates.lat);
  point.transform(new OpenLayers.Projection("EPSG:4326"),new OpenLayers.Projection("EPSG:900913"));
  var marker = new OpenLayers.Feature.Vector(point, stopPlaceInfos);
  var popup = new OpenLayers.Popup("tooltip", 
              marker.geometry.getBounds().getCenterLonLat(),
              null,
              "<div class='tooltip'>"+marker.attributes.name+"</div>",
              false);
  popup.autoSize = true;
  marker.popup = popup;
  return marker;
}

function populateGeneralMessages(transport) {
  var messages = '<ul class="messages">';
  var data = transport.responseText.evalJSON();
  data.generalMessages.each(function(generalMessage, index){
    if($("StopAreaFilter").checked == false || isGeneralMessageOnCurrentSelection(generalMessage) == true){
      var message = "<li class='" + generalMessage.messageClass + "'>" +
                    '<span class="icon"></span>' +
                    '<span class="message">' + generalMessage.message  + "</span>" +
                    '<span class="debut">' + generalMessage.startDate  + "</span>" +
                    '<span class="debut-time">' + generalMessage.startTime  + toIfRequired(generalMessage) + "</span>" +
                    '<span class="fin">' + generalMessage.endDate  + "</span>" +
                    '<span class="fin-time">' + generalMessage.endTime  + "</span>" +
                    '<div class="bottom"></div>' +
                    "</li>";
      messages += message;
    }
  });
  messages += '</ul>';
  $$('#general-messages .message-scroll-pane')[0].innerHTML=messages;

}

function isGeneralMessageOnCurrentSelection(generalMessage){
  var currentSelection = DemoSiri.markersLayer.selectedFeatures;
  if(currentSelection.length == 0)
    return false;
  
  //take first (and normally only one) selected feature infos
  var currentStopPlaceInfos = currentSelection[0].attributes;

  if(typeof(generalMessage.stopPoints) != "undefined" && generalMessage.stopPoints.map(function(sp){return sp.id;}).include(currentStopPlaceInfos.siri_id)){
    return true;
  }

  if(typeof(generalMessage.lines) != "undefined"){
    var messageLineIds = generalMessage.lines.map(function(gml){return gml.id;});
    return currentStopPlaceInfos.line_ids.any(function(markerLineId){return messageLineIds.include(markerLineId);});
  }

  return false;
}
                                    
function populateStopMonitoring(transport) {
      var visits = '<ul class="messages">';
      var data = transport.responseText.evalJSON();
      DemoSiri.currentStopVisits = [];
      data.monitoredStopVisits.each(function(stopVisit, index) {
             DemoSiri.currentStopVisits[stopVisit.id] = stopVisit;
             var visit = '<li onmouseover="highlightVehicleJourney(\'' + stopVisit.id + '\',true)" onmouseout="highlightVehicleJourney(\'' + stopVisit.id + '\',false)" onclick="showOnWards(\'' + stopVisit.id + '\')" class=' + stopVisit.status + ">" +
                         '<span class="arret">' + stopVisit.name  + "</span>" +
                         '<span class="ligne" style="' + getColourStyle(stopVisit.lineId)+'">' + stopVisit.lineName  + "</span>" +
                         '<span class="direction">' + stopVisit.directionName  + "</span>" +
                         '<span class="mode">' + stopVisit.transportModeName  + "</span>" +
                         '<span class="depart"><span class="actual">' + stopVisit.expectedDeparturTime + '</span><span class="programmed">(' + stopVisit.aimedDeparturTime + ")</span></span>" +
                         "</li>";
              visits += visit;
      });
      visits += '</ul>';
      $$('#stop-messages .message-scroll-pane')[0].innerHTML=visits;
      populateOnWards(DemoSiri.currentStopVisitId);
}

function showOnWards(stopVisitId) {
      DemoSiri.currentStopVisitId = stopVisitId;
      populateOnWards(DemoSiri.currentStopVisitId);
      $('on-wards').show();
}

function populateOnWards(stopVisitId) {
      if(stopVisitId == null)
            return;
      var stopVisit = DemoSiri.currentStopVisits[stopVisitId];
      var onWards = '<ul class="messages">';
      stopVisit.onwards.each(function(onWardStop, index) {
             var onWard = '<li>' +
                         '<span class="arret">' + onWardStop.name  + "</span>" +
                         '<span class="depart"><span class="actual">' + onWardStop.expectedDeparturTime + '</span><span class="programmed">(' + onWardStop.aimedDeparturTime + ")</span></span>" +
                            "</li>";
              onWards += onWard;
      });
      onWards += '</ul>';
      
      $('on-wards-line').setStyle(getColourStyle(stopVisit.lineId));
      $('on-wards-line').innerHTML = stopVisit.lineName;
      $$('#on-wards .message-scroll-pane')[0].innerHTML=onWards;
}


function highlightVehicleJourney(stopVisitId,active){
  if(typeof(DemoSiri.currentStopVisits[stopVisitId].onwards) != "undefined"){
    //find concerned features
    var onWardsIds = DemoSiri.currentStopVisits[stopVisitId].onwards.map(function(ow){return DemoSiri.stopPointOnRouteMap[ow.id];});
    var features = DemoSiri.markersLayer.features.select(function(f){
      return onWardsIds.include(f.attributes.siri_id);
    });

    //change feature style
    var renderIntent = active ? "onward" : "default";
    features.each(function(feature){
      feature.renderIntent = renderIntent;
    });
    DemoSiri.markersLayer.redraw();
  }
}



function getColourStyle(lineId) {
  var style = ''
  if (DemoSiri.colours) {
    var colour = DemoSiri.colours[lineId];
    if(colour) {
      style = 'color:' + colour ; 
    }
  }
  return style;
}                                                

function getGeneralMessageUrl(){
  var infoChannel = [];
  if($("Commercial").checked == true){
    infoChannel[infoChannel.length] = "Commercial";
  }
  if($("Information").checked == true){
    infoChannel[infoChannel.length] = "Information";
  }
  if($("Perturbation").checked == true){
    infoChannel[infoChannel.length] = "Perturbation";
  }
  if(infoChannel.length == 0){
    return "";
  }
  else{
    return DemoSiri.JSON_GENERAL_MESSAGE_PATH.replace('#{info_channel}', infoChannel.join(","));
  }
}

function updateGeneralMessage(){ 
  DemoSiri.generalMessageUpdater.stop();
  DemoSiri.generalMessageUpdater.url = getGeneralMessageUrl();
  DemoSiri.generalMessageUpdater.start();

  if(DemoSiri.generalMessageUpdater.url == ""){
    $$('#general-messages .message-scroll-pane')[0].innerHTML="";
  }
}


function toIfRequired(message) {
	if(endDateOrTimePresent(message)) {
		return ' &agrave;'; 		
	}
	return '';
}

function endDateOrTimePresent(message) {
	return message.endDate && message.endTime;
}
