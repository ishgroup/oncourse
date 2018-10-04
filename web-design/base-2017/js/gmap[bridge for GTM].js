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
var vLat;
var vLong;
var gMapOptions;
var near;
var vSiteAddress;
var vFrom;
var showNearMarker;
var showMapItems;
var zoom;
var map;
var siteMarkers;
var gMapSites;
var mapLoaded;

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
    mapLoaded=true;
    return "success";
}

/**
 * Creates the markers for the given locations and bounds them to the given map.
 * @param map
 * @param locations
 */
function setMarkers(map, locations) {
    //new empty array
    sites = new Array();
    //new empty bounds
    var latlngbounds = new google.maps.LatLngBounds();

    //iterate throught locations:
    for (var i = 0; i < locations.length; i++) {
        var loc = locations[i];
        var siteLatLng = new google.maps.LatLng(loc[0], loc[1]);
        //TODO var image = new google.maps.MarkerImage("path");
        //TODO var shadow = new google.maps.MarkerImage("path");

        //create marker for the location
        var marker = new google.maps.Marker({
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
        var info = attachMessage(map, marker, infoWindowContent(marker));
        //set new item to the sites array
        sites[i] = new Array(marker, info);
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
 * @return s
 */
function infoWindowContent(marker) {
    s = '<h4>' + marker.title + '</h4><h5>' + marker.suburb + '</h5>';
    if (marker.url != null) s += '<p><a href="' + marker.url + '">Information and directions</a></p>';
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
    var infowindow = new google.maps.InfoWindow({
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
function zoomMapForSite(siteId) {

    if(!mapLoaded) {
        mapLoad('gmapCanvas', gMapSites, gMapOptions);
    }
    $j("html, body").animate({ scrollTop: 0 }, "slow",
        function(){
            $j('#sitesMap').addClass('show');
        });

    //get the {marker, infowindow} pair
    var siteMarker = getSiteMarkerBySiteId(siteId);
    if (siteMarker == null) {
        alert("There's no such site on the current map");
    } else {
        map.setCenter(siteMarker[0].position);
        map.setZoom(17);
        siteMarker[1].open(map, siteMarker[0]);
    }

    return false;
}


/**
 * Gets the site marker with correspondent infowindow by the given siteId.
 * @param siteId
 * @return s
 */
function getSiteMarkerBySiteId(siteId) {
    for (var i = 0; i < sites.length; i++) {
        if (sites[i][0].id == siteId) {
            return sites[i];
        }
    }
    return null;
}

function dirLoad() {
    dirLoader('dirmap', 'dirtxt');
}

function dirLoadForId(dirId) {
    dirLoader(dirId, 'dirtxt');
}

function dirLoader(dirmap, dirtxt) {
    var geocoder = null;
    var addressMarker;
    var gMap = null;
    var latLng = new google.maps.LatLng(vLat, vLong);

    mapControl = document.getElementById('displayDirectionsMap');
    if (dirmap && document.getElementById(dirmap)) {
        document.getElementById(dirmap).innerHTML = '';
    }
    if (dirmap && document.getElementById(dirmap) && (mapControl == null || mapControl.value)) {
        dmap = document.getElementById(dirmap);
        $j(dmap).removeClass("mapCollapsed");
        $j(dmap).addClass("mapExpanded");
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
                alert('Directions for the location "'+document.getElementById('from').value+'" cannot be found');
            }
        });
    }

}


function initialize() {
    vLat = globalGoogleMapOptions.vLat;
    vLong = globalGoogleMapOptions.vLong;

    gMapOptions = {
        zoom: 14,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    if (globalGoogleMapOptions.center) {
        gMapOptions.center = globalGoogleMapOptions.center();
    }

    near = "${searchingNear}";
    vSiteAddress = globalGoogleMapOptions.vSiteAddress;
    vFrom = globalGoogleMapOptions.vFrom;
    showNearMarker = globalGoogleMapOptions.showNearMarker;
    showMapItems = globalGoogleMapOptions.showMapItems;
    zoom = null;
    siteMarkers = new Array();
    gMapSites = globalGoogleMapOptions.gMapSites;
    mapLoaded=false;

    mapLoad('gmapCanvas', gMapSites, gMapOptions);
}

/**
 * the function makes async call to load google map
 */
// function loadMap() {
//   var script = document.createElement('script');
//   script.type = 'text/javascript';
//   //we use this hard code to convert &amp; to real amp.
//   script.src = _keyMap.url('https://maps.googleapis.com/maps/api/js', {key: _keys.googleMaps, v: '3.exp', callback: 'initialize'});
//   document.body.appendChild(script);
// }

jQuery(document).ready(function() {
    if (jQuery('#gmapCanvas').length) {
        if (typeof google === 'object') {
            if (typeof google.maps === 'object') {
                initialize();
            }
        }
    }
});

(function($) {
    $(document).ready(function() {
        $(document).on('click', '.class-item-info-r .location a.tooltip', function(e) {
            e.preventDefault();
        });

        $(document).on('click', '.showLocationMap', function() {
            return false;
        });

        $(document).on('click', '.classItemLocation + dd a', function() {
            $("body").animate({ scrollTop: 0 }, "slow");
            return false;
        });

    });
})(jQuery);