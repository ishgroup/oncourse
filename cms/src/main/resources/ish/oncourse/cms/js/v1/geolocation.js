	var latitude;
    var longitude;
    var accuracy;
    
    function loadLocation() {
    
        if(navigator.geolocation) {
			var status = document.getElementById("status");
			if (status != null) {
            	status.innerHTML = "HTML5 Geolocation is supported in your browser.";
            	status.style.color = "#1ABC3C";
            
            	if($j.cookie("posLat")) {
                	latitude = $j.cookie("posLat");
                	longitude = $j.cookie("posLon");
                	accuracy = $j.cookie("posAccuracy");
                	status.innerHTML = "Location data retrieved from cookies. <a id=\"clear_cookies\" href=\" javascript:clear_cookies();\" style=\"cursor:pointer; margin-left: 15px;\"> clear cookies</a>";
                	updateDisplay();
            	} else {
                	navigator.geolocation.getCurrentPosition(success_handler,error_handler,{timeout:10000});
            	}
			}
        }
    }

    function success_handler(position) {
        latitude = position.coords.latitude;
        longitude = position.coords.longitude;
        accuracy = position.coords.accuracy;
        
        if (!latitude || !longitude) {
            document.getElementById("status").innerHTML = "HTML5 Geolocation supported, but location data is currently unavailable.";
            return;
        }
        
        updateDisplay();
        
        $j.cookie("posLat", latitude);
        $j.cookie("posLon", longitude);
        $j.cookie("posAccuracy", accuracy);
      
    }
    
    function updateDisplay() {
        var gmapdata = '<img src="http://maps.google.com/maps/api/staticmap?center=' + latitude + ',' + longitude + '&zoom=16&size=425x350&sensor=false" />';
        document.getElementById("geolocationPlaceholder").innerHTML = gmapdata;
        document.getElementById("latitude").innerHTML = latitude;
        document.getElementById("longitude").innerHTML = longitude;
		document.getElementById("geolocationInfo").setAttribute("style", "display:block");
    }
    
    
    function error_handler(error) {
        var locationError = '';
        
        switch(error.code){
        case 0:
            locationError = "There was an error while retrieving your location: " + error.message;
            break;
        case 1:
            locationError = "The user prevented this page from retrieving a location.";
            break;
        case 2:
            locationError = "The browser was unable to determine your location: " + error.message;
            break;
        case 3:
            locationError = "The browser timed out before retrieving the location.";
            break;
        }
		var status = document.getElementById("status");
        status.innerHTML = locationError;
        status.style.color = "#D03C02";
    }
    
    function clear_cookies() {
        $j.cookie('posLat', null);
        document.getElementById("status").innerHTML = "Cookies cleared.";
		document.getElementById("geolocationInfo").setAttribute("style", "display:none");
    }

	$j(document).ready(function() {
	    loadLocation();
	});