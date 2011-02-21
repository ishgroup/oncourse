/**
 *  gmap.js
 *
 *  GMap3 implementation.
 *
 *
 */

/**
 * array that contain the siteMarker and the corresponding infowindow as items.
 */
var sites;

/**
 * Creates the Map components for div with the given id and options, sets markers to the given sites.
 * @param mapID
 * @param vSites
 * @param vOptions
 * @returns {String}
 */
function mapLoad(mapID, vSites, vOptions) {
	map = new google.maps.Map(document.getElementById(mapID), vOptions);
	setMarkers(map, vSites);
	return "success";
}

/**
 * Creates the markers for the given locations and bounds them to the given map.
 * @param map
 * @param locations
 */
function setMarkers(map, locations) {
	//new empty array
	sites=new Array();
	//new empty bounds
	var latlngbounds = new google.maps.LatLngBounds();

	//iterate throught locations: 
	for ( var i = 0; i < locations.length; i++) {
		var loc = locations[i];
		var siteLatLng = new google.maps.LatLng(loc[0], loc[1]);
		//TODO var image = new google.maps.MarkerImage("path");
		//TODO var shadow = new google.maps.MarkerImage("path");

		//create marker for the location
		var marker = new google.maps.Marker( {
			position : siteLatLng,
			map : map,
			//TODO shadow: shadow,
			//TODO icon: image,
			title : loc[2],
			id : loc[4],
			suburb : loc[3],
			url : loc[5]

		});
		//create infowindow for the location
		info=attachMessage(map, marker, infoWindowContent(marker));
		//set new item to the sites array
		sites[i]=new Array(marker,info);
		//extend the common bounds with this location
		latlngbounds.extend(siteLatLng);

	}
	//fit result bounds for all the locations
	if (sites.length > 1) {
		map.fitBounds(latlngbounds);
	}
}

/**
 * Retrieves the content for the marker's infowindow.
 * @param marker
 * @returns
 */
function infoWindowContent(marker) {
	s = '<h4>' + marker.title + '</h4><h5>' + marker.suburb + '</h5>';
	if (marker.url!=null) s += '<p><a href="' + marker.url + '">Information and directions</a></p>';
	return s;
}

/**
 * Creates infowindow for the given map and marker, adds the listener to the 'click' on marker event.
 * @param map
 * @param marker
 * @param content
 * @returns {google.maps.InfoWindow}
 */
function attachMessage(map, marker, content) {
	var infowindow = new google.maps.InfoWindow( {
 		content : content
 	});
	google.maps.event.addListener(marker, 'click', function() {
		infowindow.open(map, marker);
	});
	return infowindow;
}

/**
 * Zooms in and centers view to display the site with the given id.
 * @param siteId
 */
function zoomMapForSite(siteId){
	//seems to be always shown now
   /* $j('#gmapCanvas').show();
    $j("body").animate({ scrollTop: 0 }, "slow");*/ 
	//get the {marker, infowindow} pair
	var siteMarker=getSiteMarkerBySiteId(siteId);
	if(siteMarker==null){
		alert("There's no such a site on current map");
	}else{
		map.setCenter(siteMarker[0].position);
		map.setZoom(17);
		siteMarker[1].open(map, siteMarker[0]);
	}
}

 
/**
 * Gets the site marker with correspondent infowindow by the given siteId.
 * @param siteId
 * @returns
 */
function getSiteMarkerBySiteId(siteId){
	for(var i=0;i<sites.length;i++){
		if(sites[i][0].id==siteId){
			return sites[i];
		}
	}
	return null;
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
	var latLng = new google.maps.LatLng(vLat, vLong);
	
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
					center : latLng,
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
			  destination: latLng,
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
	
	try {
		if ($j('#gmapCanvas').length) mapLoad('gmapCanvas', gMapSites, gMapOptions); 
	} catch(e) {
		// ignore this-- it means there was no real map on the page after all
	}
	
	// hide the location map after it has been filled by Google (above), then reveal it when its control is clicked.
	//$j('.collapsedLocationMap').hide();
	
	$j('.showLocationMap').click( function() {
		return false;
	});
	
	// Lets collapse the course list gMap after populating it. 
	//$j("#ListPage #gmapCanvas").hide();
	
	$j('.classItemLocation + dd a').click(function() {
		//var siteLink = $j(this).attr("href");
		//var siteID =  siteLink.split("/");
		
		$j("body").animate({ scrollTop: 0 }, "slow"); 
		//$j("#ListPage #gmapCanvas").show();
		
		/* The next 2 lines will get around the gmaps "grey box" problem */
		//google.maps.event.trigger(map, 'resize');
		//map.setZoom(14);
		
		/* TODO - show info window for the site clicked, center the map*/
		
		return false;
	});
	
});