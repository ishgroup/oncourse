/**
 *  gmap.js
 *
 *  GMap3 implementation.
 *
 *
 */
 
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

 
function dirLoad() {
	dirLoader('dirmap','dirtxt');
}

function dirLoadForId(dirId) {
	dirLoader(dirId,'dirtxt');
}

function dirLoader( dirmap, dirtxt ) {
	var geocoder = null;
	var addressMarker;
	var gMap = null;
	mapControl = document.getElementById('displayDirectionsMap');
		if (dirmap && document.getElementById(dirmap)) {
			document.getElementById(dirmap).innerHTML = '';
		}
		if (dirmap && document.getElementById(dirmap) && (mapControl==null || mapControl.value)) {
			dmap = document.getElementById(dirmap);
			dmap.removeClassName("mapCollapsed");
			dmap.addClassName("mapExpanded");
			var gMapOptions = {
					zoom : 12,
					center : new google.maps.LatLng(vLatitude,vLongitude),
					mapTypeId : google.maps.MapTypeId.ROADMAP,
					mapTypeControl : true,
					navigationControl: true,
					navigationControlOptions: {
					      style: google.maps.NavigationControlStyle.ZOOM_PAN
					}
			};
			gMap = new google.maps.Map(dmap, gMapOptions);
			
		}
		if (dirtxt && document.getElementById(dirtxt)) {
			document.getElementById(dirtxt).innerHTML = '';
			var directionsRenderer = new google.maps.DirectionsRenderer();
			directionsRenderer.setMap(gMap);    
			directionsRenderer.setPanel(document.getElementById(dirtxt));
			
			var directionsService = new google.maps.DirectionsService();
			var request = {
			  origin: document.getElementById('from').value, 
			  destination: vSiteAddress,
			  travelMode: google.maps.DirectionsTravelMode.DRIVING,
			  unitSystem: google.maps.DirectionsUnitSystem.METRIC
			};
			directionsService.route(request, function(response, status) {
			  if (status == google.maps.DirectionsStatus.OK) {
			    directionsRenderer.setDirections(response);
			  } else {
			    alert('Error: ' + status);
			  }
			});
		}
	
} 	


$j(document).ready(function() {
	// Google maps
	try {
		if ($j('#map').length) mapLoad(); 
	} catch(e) {
		// ignore this-- it means there was no real map on the page after all
	}
	if (document.getElementById("dirmap")) dirLoad();
	

	// hide the location map after it has been filled by Google (above), then reveal it when its control is clicked.
	$j('.collapsedLocationMap').hide();
	$j('.showLocationMap').click( function() {
		$j('.showLocationMap').hide();
		$j('#location').show();
		//$j('#location').removeClass('collapsedLocationMap');
		//mapLoadForID('map');
		return false;
	});
	
});