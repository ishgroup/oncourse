/**
* gmap.js
*
* License: copyright ish group
* Purpose:
*  Events handler of google maps
*/

// Declaration of all required variable
var locationstTrigger, markers, infowindows, sitesMap, gmapCanvas, map, getDirButton;

// Add event handler after document content loaded
document.addEventListener("DOMContentLoaded", function(event) {

  locationstTrigger = document.querySelector(".toggle_locations");
  markers = [];
  infowindows = [];
  sitesMap = document.getElementById("sitesMap");
  gmapCanvas = document.getElementById("gmapCanvas");
  getDirButton = document.querySelector(".getDirections");

  if (locationstTrigger) {
    locationstTrigger.addEventListener("click", toggleAllMarkers);
  }
  if (getDirButton) {
    getDirButton.addEventListener("click", function() {
      return makeRoute(document.getElementById("from").value);
    });
  }
});

// Plotting of markers
document.addEventListener("click", function(e) {
  var target = e.target;

  while (target != this) {
    if (target === null) {
      return;
    }

    if (target.tagName === "A" && target.hasAttribute("data-coordinates")) {
      window.scrollTo({
        top: 0,
        behavior: "smooth"
      });

      if (!sitesMap.classList.contains("show")) {
        sitesMap.classList.add("show");
      }

      var intervalId = setInterval(function() {
        if (map) {
          clearInterval(intervalId);

          var markerAttributes = createMarkerAttributes(target);

          map.setCenter({
            lat: markerAttributes.lat,
            lng: markerAttributes.lng
          });

          infowindows.map(function(el) {
            return el.close();
          });

          if (!markers.some(function(el) {
            return el.details == markerAttributes.id;
          })) {
            var marker = new google.maps.Marker({
              position: {
                lat: markerAttributes.lat,
                lng: markerAttributes.lng
              },
              map: map,
              details: markerAttributes.id
            });
            markers.push(marker);

            var infowindow = new google.maps.InfoWindow({
              content: markerAttributes.content
            });
            infowindows.push(infowindow);

            marker.addListener("click", function() {
              infowindow.open(map, marker);
            });
          }

          var markerArrayID = markers.findIndex(function(el) {
            return el.details == markerAttributes.id;
          });
          map.setZoom(15);
          infowindows[markerArrayID].open(map, markers[markerArrayID]);
        }
      }, 1000);

      return;
    }

    target = target.parentNode;
  }
});

// Initialization of google maps and info window handler
function initMaps() {
  map = new google.maps.Map(gmapCanvas, {
    zoom: 0
  });

  if (gmapCanvas.hasAttribute("data-coordinates")) {
    var markerAttributes = createMarkerAttributes(gmapCanvas);

    map.setCenter({
      lat: markerAttributes.lat,
      lng: markerAttributes.lng
    });
    var marker = new google.maps.Marker({
      position: {
        lat: markerAttributes.lat,
        lng: markerAttributes.lng
      },
      map: map,
      details: markerAttributes.id
    });
    markers.push(marker);
    var infowindow = new google.maps.InfoWindow({
      content: markerAttributes.content
    });
    infowindows.push(infowindow);

    marker.addListener("click", function() {
      infowindow.open(map, marker);
    });

    map.setZoom(15);
    infowindow.open(map, marker);
  }
}

// Handling of toggling effect for google map markers and info window.
function toggleAllMarkers() {
  document.getElementById("sitesMap").classList.toggle("show");

  var intervalId = setInterval(function() {
    if (typeof map !== "undefined" && markers.length < 1) {
      clearInterval(intervalId);

      var bounds = new google.maps.LatLngBounds(),
        allMapLinks = document.querySelectorAll("a[data-coordinates]");

      allMapLinks.forEach(function(el) {
        clearInterval(intervalId);

        var markerAttributes = createMarkerAttributes(el);

        var marker = new google.maps.Marker({
          position: {
            lat: markerAttributes.lat,
            lng: markerAttributes.lng
          },
          map: map,
          details: markerAttributes.id
        });

        markers.push(marker);
        var infowindow = new google.maps.InfoWindow({
          content: markerAttributes.content
        });
        infowindows.push(infowindow);

        marker.addListener("click", function() {
          infowindow.open(map, marker);
        });

        bounds.extend({
          lat: markerAttributes.lat,
          lng: markerAttributes.lng
        });
      });
      map.fitBounds(bounds);
    }
  });
}

// Create marker pointer attributes based on co-ordinates content
function createMarkerAttributes(el) {
  var arrayOfDataset = el.dataset.coordinates.replace(/(")/g, "");
  arrayOfDataset = arrayOfDataset.split(",");
  arrayOfDataset = arrayOfDataset.map(function(el) {
    return el.trim();
  });

  var markerAttributes = {
    lat: +arrayOfDataset[0],
    lng: +arrayOfDataset[1],
    content: "<h4>" +
      arrayOfDataset[2] +
      "</h4>" +
      "<h5>" +
      arrayOfDataset[3] +
      "</h5>" +
      '<p><a href="' +
      arrayOfDataset[5] +
      '">Information and directions</a></p>',
    id: +arrayOfDataset[4]
  };

  return markerAttributes;
}

// Create direction routes of marker attribute and show direction panel
function makeRoute(origin) {
  document.getElementById("dirtxt").innerText = "";

  if (!mapsExists()) return;

  if (typeof directionsRenderer !== "undefined") {
    directionsRenderer.setMap(null);
  }

  var markerAttributes = createMarkerAttributes(gmapCanvas);
  var latLng = new google.maps.LatLng(
    markerAttributes.lat,
    markerAttributes.lng
  );

  window.directionsRenderer = new google.maps.DirectionsRenderer();
  directionsRenderer.setMap(map);
  directionsRenderer.setPanel(document.getElementById("dirtxt"));

  var directionsService = new google.maps.DirectionsService();
  var request = {
    origin: origin,
    destination: latLng,
    travelMode: google.maps.DirectionsTravelMode.DRIVING,
    unitSystem: google.maps.DirectionsUnitSystem.METRIC
  };
  directionsService.route(request, function(response, status) {
    if (status == google.maps.DirectionsStatus.OK) {
      directionsRenderer.setDirections(response);
    } else {
      alert(
        'Directions for the location "' +
        document.getElementById("from").value +
        '" cannot be found'
      );
    }
  });
}

function mapsExists() {
  return typeof google === 'object' && typeof google.maps === 'object';
}
