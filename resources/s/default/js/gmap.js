var sites;

function mapLoad(mapID, sites) {
	var myOptions = {
		zoom : 0,
		center : latlng,
		mapTypeId : google.maps.MapTypeId.ROADMAP
	}
	map = new google.maps.Map(document.getElementById(mapID), myOptions);
	setMarkers(map, sites);
	return "success";
}

function setMarkers(map, locations) {
	sites=new Array();
	var latlngbounds = new google.maps.LatLngBounds();

	for ( var i = 0; i < locations.length; i++) {
		var loc = locations[i];
		var siteLatLng = new google.maps.LatLng(loc[0], loc[1]);
		//TODO var image = new google.maps.MarkerImage("path");
		//TODO var shadow = new google.maps.MarkerImage("path");

		var marker = new google.maps.Marker( {
			position : siteLatLng,
			map : map,
			//TODO shadow: shadow,
			//TODO icon: image,
			title : loc[2],
			id : loc[4]            
		});
		sites[i]=marker;
		attachMessage(map, marker, "<h4>" + loc[2] + "</h4><h5>" + loc[3]
				+ "</h5>" + "<p><a href=\"/site/" + loc[4]
				+ "\">Information and directions</a></p>");
		latlngbounds.extend(siteLatLng);

	}
	map.fitBounds(latlngbounds);

}

function getSiteMarkerBySiteId(siteId){
	for(var i=0;i<sites.length;i++){
		if(sites[i].id==siteId){
			return sites[i];
		}
	}
	return null;
}

function attachMessage(map, marker, content) {
	var infowindow = new google.maps.InfoWindow( {
		content : content
	});
	google.maps.event.addListener(marker, 'click', function() {
		infowindow.open(map, marker);
	});
}

function zoomMapForSite(siteId){
    $j('#focus-map').show();
    $j("body").animate({ scrollTop: 0 }, "slow"); 
	var siteMarker=getSiteMarkerBySiteId(siteId);
	if(siteMarker==null){
		alert("There's no such a site on current map");
	}else{
		map.setCenter(siteMarker.position);
		map.setZoom(17);
	}
	
}