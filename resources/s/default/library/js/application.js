// ------------------------ Timeline display ------------------

// displays a Timeline view by fetching class data.
// uses parameters defined in the (link) that was clicked:
// 		href = the url that will fetch the XML data
// 		rel = 'session' or 'class' determines which will be fetched and displayed
// 		timeZone = the time zone that will be used to position the classes
function displayTimeline(link) {
	var xmldata = link.href;
	var reldata = String(link.getAttribute('rel'));
	var timeZone = String(link.getAttribute('timeZone'));   // Nov 2008 - get timezone from request
	hideSelectBoxes();
	hideFlash();
	
	//
	// 10 Apr 2008 -- Fix to show lightbox/overlay within the page scroll
	//From:
	//--------------------------------------------------------
	// var arrayPageSize = getPageSize();
	// $('overlay').style.width = arrayPageSize[0] +'px';
	// $('overlay').style.height = arrayPageSize[1] +'px';
	// To:
	//--------------------------------------------------------
	var arrayPageSize = getPageSize();
	$('overlay').style.width = arrayPageSize[0] +'px';		
	$('overlay').style.height = arrayPageSize[1] +'px';	
	var arrayPageScroll = getPageScroll();
	var padding = 10; // $('timeline-wrap').style.padding; // hmm doesn't seem to be available
	$('timeline-wrap').style.top = (arrayPageScroll[1] + (padding * 4)) + 'px';
	$('timeline-wrap').style.bottom = '0px';
	$('timeline-wrap').style.margin = '0px 0pt 0pt -375px';
	//--------------------------------------------------------
	
	new Effect.Appear('overlay', { duration: 0.2, from: 0.0, to: 0.4,
		afterFinish: function() { $('timeline-wrap').style.visibility = 'visible';} 
	});
	//tl = prepareTimeline(reldata);
	var today = new Date();
    var eventSource = new Timeline.DefaultEventSource(xmldata);
	if (reldata == 'session') {
		bandInfos = [
			Timeline.createBandInfo({
                timeZone:       timeZone,   // Nov 2008 - timezone from request
				date:           today,
				width:          "20%", 
				intervalUnit:   Timeline.DateTime.DAY, 
				intervalPixels: 100,
				eventSource:    eventSource
			}),
			Timeline.createBandInfo({
                timeZone:       timeZone,   // Nov 2008 - timezone from request
				date:           today,
				width:          "40%", 
				intervalUnit:   Timeline.DateTime.MONTH, 
				intervalPixels: 200,
				eventSource:    eventSource
			})
		];
	} 
	if (reldata == 'class') {
		bandInfos = [
			Timeline.createBandInfo({
                timeZone:       timeZone,   // Nov 2008 - timezone from request
				date:           today,
				width:          "60%", 
				intervalUnit:   Timeline.DateTime.MONTH, 
				intervalPixels: 100,
				eventSource:    eventSource
			}),
			Timeline.createBandInfo({
                timeZone:       timeZone,   // Nov 2008 - timezone from request
				date:           today,
				width:          "40%", 
				intervalUnit:   Timeline.DateTime.YEAR, 
				intervalPixels: 200,
				eventSource:    eventSource
			})
		];
	}

	bandInfos[1].syncWith = 0;
	bandInfos[1].highlight = true;
	tl = Timeline.create($('timeline'), bandInfos);
	
	tl.loadXML(xmldata, function(xml, url) { 
		eventSource.loadXML(xml, url); 
		//tl.getBand(0).scrollToCenter(eventSource.getEarliestDate());
		tl.getBand(0).setCenterVisibleDate(eventSource.getEarliestDate());
		
	});	
	// Sept 2009 - add table of sessions underneath the timeline bands in the lightbox
	if (reldata == 'session') {
		// copy the session table contents from the hidden div in the CourseClassListItem and display below bands
		var classID = '#sessions_for_class_' + xmldata.substring(xmldata.indexOf('?ids=')+5);
		$j('#timeline').append('<div class="timeline_stuffer" style="height:240px;"/>&nbsp;</div><div class="sessions_in_timeline" style="overflow:scroll; margin-left:30px; height:150px;">' + $j( classID).html() + '</div>' );
	}
	return false;
}

// Timeline support ----------------------

function showSelectBoxes(){
	var selects = document.getElementsByTagName("select");
	for (i = 0; i != selects.length; i++) {
		selects[i].style.visibility = "visible";
	}
}

function hideSelectBoxes(){
	var selects = document.getElementsByTagName("select");
	for (i = 0; i != selects.length; i++) {
		selects[i].style.visibility = "hidden";
	}
}

function showFlash(){
	var flashObjects = document.getElementsByTagName("object");
	for (i = 0; i < flashObjects.length; i++) {
		flashObjects[i].style.visibility = "visible";
	}

	var flashEmbeds = document.getElementsByTagName("embed");
	for (i = 0; i < flashEmbeds.length; i++) {
		flashEmbeds[i].style.visibility = "visible";
	}
}

function hideFlash(){
	var flashObjects = document.getElementsByTagName("object");
	for (i = 0; i < flashObjects.length; i++) {
		flashObjects[i].style.visibility = "hidden";
	}

	var flashEmbeds = document.getElementsByTagName("embed");
	for (i = 0; i < flashEmbeds.length; i++) {
		flashEmbeds[i].style.visibility = "hidden";
	}

}

function getPageScroll(){

	var xScroll, yScroll;

	if (self.pageYOffset) {
		yScroll = self.pageYOffset;
		xScroll = self.pageXOffset;
	} else if (document.documentElement && document.documentElement.scrollTop){	 // Explorer 6 Strict
		yScroll = document.documentElement.scrollTop;
		xScroll = document.documentElement.scrollLeft;
	} else if (document.body) {// all other Explorers
		yScroll = document.body.scrollTop;
		xScroll = document.body.scrollLeft;	
	}

	arrayPageScroll = new Array(xScroll,yScroll) 
	return arrayPageScroll;
}

function getPageSize(){
	
	var xScroll, yScroll;
	
	if (window.innerHeight && window.scrollMaxY) {	
		xScroll = window.innerWidth + window.scrollMaxX;
		yScroll = window.innerHeight + window.scrollMaxY;
	} else if (document.body.scrollHeight > document.body.offsetHeight){ // all but Explorer Mac
		xScroll = document.body.scrollWidth;
		yScroll = document.body.scrollHeight;
	} else { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
		xScroll = document.body.offsetWidth;
		yScroll = document.body.offsetHeight;
	}
	
	var windowWidth, windowHeight;
	
//		console.log(self.innerWidth);
//		console.log(document.documentElement.clientWidth);

	if (self.innerHeight) {	// all except Explorer
		if(document.documentElement.clientWidth){
			windowWidth = document.documentElement.clientWidth; 
		} else {
			windowWidth = self.innerWidth;
		}
		windowHeight = self.innerHeight;
	} else if (document.documentElement && document.documentElement.clientHeight) { // Explorer 6 Strict Mode
		windowWidth = document.documentElement.clientWidth;
		windowHeight = document.documentElement.clientHeight;
	} else if (document.body) { // other Explorers
		windowWidth = document.body.clientWidth;
		windowHeight = document.body.clientHeight;
	}	
	
	// for small pages with total height less then height of the viewport
	if(yScroll < windowHeight){
		pageHeight = windowHeight;
	} else { 
		pageHeight = yScroll;
	}

//		console.log("xScroll " + xScroll)
//		console.log("windowWidth " + windowWidth)

	// for small pages with total width less then width of the viewport
	if(xScroll < windowWidth){	
		pageWidth = xScroll;		
	} else {
		pageWidth = windowWidth;
	}
//		console.log("pageWidth " + pageWidth)

	arrayPageSize = new Array(pageWidth,pageHeight,windowWidth,windowHeight) 
	return arrayPageSize;
}



// ----------------- Google Maps -------------------------
	
function drawGIcon(map, title, latitude, longitude, focusMatch, 
					image, shadow, iconWidth, iconHeight, shadowWidth, shadowHeight,
					anchorx, anchory, infoWindowx, infoWindowy, suburb, url) {
	var icon = null;
	icon = new GIcon();
	icon.image = image;
	icon.shadow = shadow;
	icon.iconSize = new GSize(iconWidth, iconHeight);
	icon.shadowSize = new GSize(shadowWidth, shadowHeight);
	icon.iconAnchor = new GPoint(anchorx, anchory);
	icon.infoWindowAnchor = new GPoint(infoWindowx, infoWindowy);
	var point = new GLatLng(latitude,longitude);
	marker = new GMarker(point, {icon:icon, title:title});
	marker.title = title;
	marker.suburb = suburb;
	marker.url = url;
	map.addOverlay(marker);
	//console.log("drew marker " + title + " at " + latitude + "," + longitude + " with " + image);
	return marker;
}

function mapLoad() {
		mapLoadForID('map');
}

function mapLoadForID(mapID) {
	if ($j('#' + mapID).length && window.map==null) {
		//--------
		var mapOptions = {
				zoom : 16,
				center : new google.maps.LatLng(vLatitude,vLongitude),
				mapTypeId : google.maps.MapTypeId.ROADMAP,
				mapTypeControl : true,
				navigationControl: true,
				navigationControlOptions: {
				      style: google.maps.NavigationControlStyle.ZOOM_PAN
				}

		}
		window.map = new google.maps.Map(document.getElementById(mapID), mapOptions);
		
		if (window.showMapItems) {
			setMarkers(sites);
		}
		//---------
		/*window.map = new GMap2(document.getElementById(mapID));
		window.map.setCenter(new GLatLng(vLatitude,vLongitude), 16);
		window.map.addControl(new GMapTypeControl());
		window.map.addControl(new GSmallMapControl());
		
		!!!!!!!!!!!!!!!!!!!!
		//TODO implement "near" marker
		if (window.showNearMarker) {
			// show the marker that matches the "near" search term
			var nearmarker = drawGIcon(window.map, "Near", vLatitude, vLongitude, null, 
					"/s/img/box-blue-star.png", "/s/img/box-shadow.png", 35, 35, 52, 35,
					6, 30, 10, 5, window.near);
			window.showNearMarker = false;
		} else if (vLatitude!=0 && vLongitude!=0) {
			// Create our "tiny" marker icon
			drawGIcon(window.map, "Site", vLatitude, vLongitude, null, 
					"/s/img/marker1.png", "/s/img/marker-shadow1.png", 20, 34, 36, 34,
					10, 34, 10, 5);
		}
		!!!!!!!!!!!!!!!!!!!!!!!
		
		if (window.showMapItems) {
			window.map.enableInfoWindow();
			GEvent.addListener(window.map, 'click', function(ol) {
				if (ol) {
					//console.log("clicked on " + ol + " at " + ol.getLatLng());
					window.map.openInfoWindowHtml(ol.getLatLng(), infoWindowContent(ol) );
				}
			});
	//		var dataURL = "/advanced/mapitems";
	//		if (window.searchParams.mapItemIDs != null) {
	//			dataURL += "?ids=" + window.searchParams.mapItemIDs;
	//		}
			$j.getJSON("/advanced/mapitems",function(data)
				{
					if ( data ) {
						var hadPerfect = false;
						bestBounds = new GLatLngBounds();
						otherBounds = new GLatLngBounds();
						if (vLatitude!=0 && vLongitude!=0) {
							bestBounds.extend(new GLatLng(vLatitude, vLongitude));
							otherBounds.extend(new GLatLng(vLatitude, vLongitude));
						}
						for (i=0; i<data.items.length; i++) {
							m = data.items[i];
							id = 'id' + m.id;
							window.siteMarkers[id] = drawGIcon(map, m.title, m.latitude, m.longitude, m.focusMatch, 
									m.imageSrc, m.shadowSrc, m.iconWidth, m.iconHeight, m.shadowWidth, m.shadowHeight,
									m.anchorx, m.anchory, m.infoWindowx, m.infoWindowy, m.suburb, m.url);
							//console.log('site ID = ' + id + " => " + window.siteMarkers[id]);
							otherBounds.extend(new GLatLng(m.latitude, m.longitude));
							//console.log("Extending bounds for match = " + m.focusMatch);
							if (m.focusMatch==1) {
								hadPerfect = true;
								bestBounds.extend(new GLatLng(m.latitude, m.longitude));
							}
						}
						bounds = hadPerfect ? bestBounds : otherBounds;
						zoom = window.map.getBoundsZoomLevel(bounds);
						window.map.setCenter(bounds.getCenter(), zoom);
						// if map was loaded by clicking on a site link, have to show the info window
						if (window.siteIDtoShow!=null) {
							showSiteOnMap(window.siteIDtoShow);
							window.siteIDtoShow = null;
						}
					}
				}
			);
		}*/
	}	
}

function setMarkers(locations) {
	var latlngbounds = new google.maps.LatLngBounds();

	
	for ( var i = 0; i < locations.length; i++) {
		var loc = locations[i];
		latitude=loc[0];
		longitude=loc[1];
		title=loc[2];
		suburb=loc[3];
		id=loc[4];
		imagePath=loc[5];
		shadowPath=loc[6];
		iconWidth=loc[7];
		iconHeight=loc[8];
		shadowWidth=loc[9];
		shadowHeight=loc[10];
		anchorx=loc[11];
		anchory=loc[12];
		infoWindowx=loc[13];
		infoWindowy=loc[14];
		
		var siteLatLng = new google.maps.LatLng(latitude, longitude);
		var image = new google.maps.MarkerImage(imagePath, new google.maps.Size(iconWidth, iconHeight),
				null, new google.maps.Point(anchorx,anchory));
		var shadow = new google.maps.MarkerImage(shadowPath, new google.maps.Size(shadowWidth, shadowHeight),
				null, new google.maps.Point(anchorx,anchory));

		var marker = new google.maps.Marker( {
			position : siteLatLng,
			map : map,
			shadow: shadow,
			icon: image,
			title : title,
			id : id,
			suburb : suburb,
			url : "/site/" + id
		});
		siteMarkers[i]=marker;
		attachMessage(map, marker, /*"<h4>" + loc[2] + "</h4><h5>" + loc[3]
				+ "</h5>" + "<p><a href=\"/site/" + loc[4]
				+ "\">Information and directions</a></p>"*/ infoWindowContent(marker));
		latlngbounds.extend(siteLatLng);

	}
	map.fitBounds(latlngbounds);

}

function infoWindowContent(marker) {
	s = '<h4>' + marker.title + '</h4><h5>' + marker.suburb + '</h5>';
	if (marker.url!=null) s += '<p><a href="' + marker.url + '">Information and directions</a></p>';
	return s;
}

function attachMessage(map, marker, content) {
	google.maps.event.addListener(marker, 'click', function() {
		openInfoWindow(map, marker, content);
	});
}

function openInfoWindow(map, marker, content){
	if(infowindow!=null){
		infowindow.close();
	}
	infowindow = new google.maps.InfoWindow( {
		content : content
	});
	infowindow.open(map, marker);
}

function zoomMapForSite(siteId){
    $j('#focus-map').show();
    $j("body").animate({ scrollTop: 0 }, "slow");
    if (window.map==null) {
		mapLoadForID('mapDelayed');
	}
	var siteMarker=getSiteMarkerBySiteId(siteId);
	if(siteMarker==null){
		alert("There's no such a site on current map");
	}else{
		map.setCenter(siteMarker.position);
		map.setZoom(16);
		openInfoWindow(map, siteMarker, infoWindowContent(siteMarker));
		latlngbounds = new google.maps.LatLngBounds();
		latlngbounds.extend(siteMarker.position);
		map.fitBounds(latlngbounds);
	}
	
}

function getSiteMarkerBySiteId(siteId){
	for(var i=0;i<siteMarkers.length;i++){
		if(siteMarkers[i].id==siteId){
			return siteMarkers[i];
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


// open the infoWindow on the map with the site details for the site ID specified
function showSiteOnMap(siteID) {
	$j('#focus-map').show();
	$j("body").animate({ scrollTop: 0 }, "slow"); 
	if (window.map==null) {
		window.siteIDtoShow = siteID;
		mapLoadForID('mapDelayed');
	} else {
		id = 'id' + siteID;
		//console.log(".......... loading site info for " + id);
		marker = window.siteMarkers[id];
		//console.log(".......... loading site info for " + marker.title);
		if (marker) {
			window.map.openInfoWindowHtml( marker.getLatLng(), infoWindowContent(marker));
		} else {
			//console.log("No marker for " + id);
		}
	}
}



// Cookies

function getCookie( name ) {
	var start = document.cookie.indexOf( name + "=" );
	var len = start + name.length + 1;
	if ( ( !start ) && ( name != document.cookie.substring( 0, name.length ) ) ) {
		return null;
	}
	if ( start == -1 ) return null;
	var end = document.cookie.indexOf( ';', len );
	if ( end == -1 ) end = document.cookie.length;
	return unescape( document.cookie.substring( len, end ) );
}

function setCookie( name, value, expires, path, domain, secure ) {
	var today = new Date();
	today.setTime( today.getTime() );
	if ( expires ) {
		expires = expires * 1000 * 60 * 60 * 24;
	}
	var expires_date = new Date( today.getTime() + (expires) );
	document.cookie = name+'='+escape( value ) +
		( ( expires ) ? ';expires='+expires_date.toGMTString() : '' ) + //expires.toGMTString()
		( ( path ) ? ';path=' + path : '' ) +
		( ( domain ) ? ';domain=' + domain : '' ) +
		( ( secure ) ? ';secure' : '' );
}

function deleteCookie( name, path, domain ) {
	if ( getCookie( name ) ) document.cookie = name + '=' +
			( ( path ) ? ';path=' + path : '') +
			( ( domain ) ? ';domain=' + domain : '' ) +
			';expires=Thu, 01-Jan-1970 00:00:01 GMT';
}

// Web Page Editing and Portal -----------------
function insertImage(fieldID, id, alt, w, h) { 
	ins = '{image id:"' + id + '" alt:"' + alt + '" width:"' + w + '" height:"' + h + '"}';
	el = $(fieldID);
	//alert("adding " + ins + " to " + el.value);
	if (el.setSelectionRange) { 
    	el.value = el.value.substring(0,el.selectionStart) + "\n" + ins + "\n" + el.value.substring(el.selectionEnd,el.value.length); 
		} else { 
    	el.value = el.value + "\n" + ins;
	} 
} 

// -------------- Enrolment -----------------

// calculates the total price when you change the enrolment checkboxes
function totalPrice() {
	$j('form#enrolmentform input:not(:checked)').each(function(i) {
		var id = $j(this).parent().attr('id').substring(1);
		$j('td#p' + id).addClass('price-crossed');
	});
	var total = 0;
	var discountTotal = 0;
	$j('form#enrolmentform input:checked').each(function(i) {
		var id = $j(this).parent().attr('id').substring(1);
		var price = $j('td#p' + id).removeClass('price-crossed');
		discountTotal += parseFloat($j('#d' + id).text());
		total += parseFloat(price.children('.fee-discounted').text().substring(1));
	});
	$j('#discounttotal').text(makeMoney(discountTotal));
	$j('#total').text(makeMoney(total));
	$j('#cardtotalstring').text(makeMoney(total));
}

function makeMoney(money) {
	var value = money.toString().split('.');
	var cent  = value.length==1 ? '00' : (value[1].length == 1 ? value[1]+'0' : value[1].substring(0,2));
	return '$' + value[0] + '.' + cent;
}

jQuery.fn.clearForm = function() {
	return this.each(function() {
		var type = this.type, tag = this.tagName.toLowerCase();
		if (tag == 'form')
			return $j(':input',this).clearForm();
		if (type == 'text' || type == 'password' || tag == 'textarea')
			this.value = '';
		else if (type == 'checkbox' || type == 'radio')
			this.checked = false;
		else if (tag == 'select')
			this.selectedIndex = -1;
	});
};


/* ----------------- Advanced Search ------------------------- */

//-------------------------------------------------------------------------------------------------------
// ClearTypeFadeTo / ClearTypeFadeIn / ClearTypeFadeOut
//
// Custom fade in and fade out functions for jQuery that will work around
// IE's bug with bold text in elements that have opacity filters set when
// also using Window's ClearType text rendering.
//
// New Parameter:
// bgColor    The color to set the background if none specified in the CSS (default is '#fff')
//
// Examples:
// $('div').ClearTypeFadeIn({ speed: 1500 });
// $('div').ClearTypeFadeIn({ speed: 1500, bgColor: '#ff6666', callback: myCallback });
// $('div').ClearTypeFadeOut({ speed: 1500, callback: function() { alert('Fade Out complete') } });
//
// Notes on the interaction of ClearType with DXTransforms in IE7
// http://blogs.msdn.com/ie/archive/2006/08/31/730887.aspx
	$j.fn.ClearTypeFadeTo = function(options) {
		if (options)
			$j(this)
				.show()
				.each(function() {
					if (jQuery.browser.msie) {
						// Save the original background color
						$j(this).attr('oBgColor', $j(this).css('background-color'));
						// Set the bgColor so that bold text renders correctly (bug with IE/ClearType/bold text)
						// !!! Disabled the following as it does not seem to be picking up the element bgColor, causing sites to be unviewable in IE (-cll)
						//$j(this).css({ 'background-color': (options.bgColor ? options.bgColor : '#fff') })
					}
				})
				.fadeTo(options.speed, options.opacity, function() {
					if (jQuery.browser.msie) {
						// ClearType can only be turned back on if this is a full fade in or
						// fade out. Partial opacity will still have the problem because the
						// filter style must remain. So, in the latter case, we will leave the
						// background color and 'filter' style in place.
						if (options.opacity == 0 || options.opacity == 1) {
							// Reset the background color if we saved it previously
							$j(this).css({ 'background-color': $j(this).attr('oBgColor') }).removeAttr('oBgColor');
							// Remove the 'filter' style to restore ClearType functionality.
							$j(this).get(0).style.removeAttribute('filter');
						}
					}
					if (options.callback != undefined) options.callback();
				});
	};

	$j.fn.ClearTypeFadeIn = function(options) {
		if (options)
			$j(this)
				.css({ opacity: 0 })
				.ClearTypeFadeTo({ speed: options.speed, opacity: 1, callback: options.callback });
	};

	$j.fn.ClearTypeFadeOut = function(options) {
		if (options)
			$j(this)
				.css({ opacity: 1 })
				.ClearTypeFadeTo({ speed: options.speed, opacity: 0, callback: options.callback });
	};

/* -----*/

function toggleAdvancedSearch() {
	if ($j('#advanced_search').is(':visible')) {
		hideAdvancedSearch();
	} else {
		showAdvancedSearch();
	}
}

function showAdvancedSearch() {
	$j('a.show-advanced-search').css('background','transparent url(/s/img/bg_advsearchbox.png)');
	$j('a.show-advanced-search').css('border-color',$j('div#advanced_search').css('border-left-color'));
	$j('a.show-advanced-search').css('border-style','solid');
	$j('a.show-advanced-search').css('border-width','1px 1px 0 1px');
	$j('a.show-advanced-search span').addClass('adv_search_active');
	$j('a.show-advanced-search span').css('background','transparent url(/s/img/arrow_up.png) no-repeat 100%')	
		.text("Fewer options");
	$j('form#search').ClearTypeFadeTo({ speed: 450, opacity: 0.4 });
	$j('div#content').ClearTypeFadeTo({ speed: 450, opacity: 0.4 });
	$j('#advanced_search').show('slow', function() {
	});

	// install watcher to detect clicks on the background					
	$j(document).bind('mousedown', function(e) { 
		// hide advanced search if click was not inside advanced search area
		if(!$j(e.target).is('#advanced_search_container *') && !$j(e.target).is('div.ac_results *')) {
			hideAdvancedSearch();
		}
		
	});
}

function hideAdvancedSearch() {
	$j(document).unbind('mousedown');
	$j('form#search').ClearTypeFadeTo({ speed: 450, opacity: 1 });
	$j('div#content').ClearTypeFadeTo({ speed: 450, opacity: 1 });	
	$j('#advanced_search').hide('slow');
	$j('a.show-advanced-search').css('background','');
	$j('a.show-advanced-search').css('border','none')
	$j('a.show-advanced-search span').removeClass('adv_search_active');
	$j('a.show-advanced-search span').css('background','transparent url(/s/img/arrow_down.png) no-repeat 100%')
	.text("More options");
}

function clearAdvancedSearch() {
	$j('#search2').clearForm();
}

// clear default values from text fields with class defaultText that match the "default" attribute
function clearDefaultValues() {
	$j(".defaultText").each(function(srcc)
    {
        if ($j(this).val() == $j(this).attr("default"))
        {
            $j(this).val("");
        }
    });
}

/* --------------- suburbs, states and postcodes ----------------- */

// extract suburb name, eg NEWTOWN from 'NEWTOWN 2042'
function suburbFromString(s) {
	postcodeLength = 4;
	if (s.length >= postcodeLength) {
		postcode = parseInt(s.substring(s.length-postcodeLength, s.length));
		if (postcode) {
			return jQuery.trim(s.substring(0,s.length-postcodeLength));
		}
	}
	return s;
}

//extract postcode, eg 2042 from 'NEWTOWN 2042'
function postcodeFromString(s) {
	postcodeLength = 4;
	if (s.length >= postcodeLength) {
		postcode = parseInt(s.substring(s.length-postcodeLength, s.length));
		if (postcode) {
			return "" + postcode;
		}
	}
	return '';
}

// work out state from an Australian postcode
function stateFromPostcode(postcode) {
	if (postcode.length < 4) return '';
	p = parseInt(postcode);
	if (p==null) return '';
	if (p >= 100 && p <= 2599 ) return "NSW";
	if (p >= 2619 && p <= 2898 ) return "NSW";
	if (p >= 2921 && p <= 2999 ) return "NSW";
	if (p >= 200 && p <= 299 ) return "ACT";
	if (p >= 2600 && p <= 2618 ) return "ACT";
	if (p >= 2900 && p <= 2920 ) return "ACT";
	if (p >= 3000 && p <= 3999 ) return "VIC";
	if (p >= 8000 && p <= 8999 ) return "VIC";
	if (p >= 4000 && p <= 4999 ) return "QLD";
	if (p >= 9000 && p <= 9999 ) return "QLD";
	if (p >= 5000 && p <= 5999 ) return "SA";
	if (p >= 6000 && p <= 6999 ) return "WA";
	if (p >= 7000 && p <= 7999 ) return "TAS";
	if (p >= 800 && p <= 999 ) return "NT";
	return '';
}


// set postcode and state fields from value of suburb field and remove the postcode from the suburb field
// if the postcode field does not exist, leave the suburb field alone
function setPostcodeAndStateFromSuburb(searchForm, suburbString) {
	if (suburbString) {
		var suburb = suburbFromString(suburbString);
		var postcode = postcodeFromString(suburbString); // otherwise it thinks it's a number
		var state = postcode.length==0 ? '' : stateFromPostcode(postcode);
		if (suburb.length > 0) {
			if (searchForm.postcode && postcode.length>0) {
				$j("#"+searchForm.postcode.id).val(postcode);
				//$j("."+suburbID).val(suburb);
			}
			if (searchForm.state && state.length>0) {
				$j("#"+searchForm.state.id).val(state);
			}		
		}
	}
	return false;
}


// change the content editor to use CKEditor
function editWebContentWithCKEditor(fieldID) {
	CKEDITOR.config.toolbar_Full = [
		['PasteText','PasteFromWord','-', 'SpellChecker', 'Scayt'],
		['Undo','Redo','-','Find','Replace','-','RemoveFormat'],
		['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],
		['Templates','-', 'Source'],
		'/',
		['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
		['Link','Unlink','Anchor'],	['Image','Table','HorizontalRule','Smiley','SpecialChar','PageBreak'],
		['Styles','Format', 'ShowBlocks']
	 ];
	//CKEDITOR.replace( fieldID, { customConfig : '../ckconfig.js' } );
	CKEDITOR.replace(fieldID );
}

function initHints(){
	// Show us hints on entering the field if the input is not invalid
	$j('span.valid input').bind("focus blur", function() {
		$j(this).next().next().children('.hint').toggleClass('hidden-text');
		//$j(this).parent().nextAll('.hint:first').toggle();
	});

	// Show us reasons for errors on entering the field if the input IS invalid
	$j('span.validate input').bind("focus blur", function() {
		$j(this).next().next().children('.reason').toggleClass('hidden-text');
	});
	$j('span.validate select').bind("focus blur", function() {
		$j(this).next().next().children('.reason').toggleClass('hidden-text');
	});
}

/**
 * Refreshes the shortList component and if the courseClassToRefresh!=null, 
 * refreshes the shortlistControl for this class.
 */
function refreshShortList(courseClassToRefresh){
	$j.ajax({
		type: "GET",
		url:  '/refreshShortList',
		success: function(msg){
					$j('#shortlist').replaceWith(msg);	
					if(courseClassToRefresh!=null){
						$j.ajax({
							type: "GET",
							url:  '/refreshShortListControl?courseClassId=' + courseClassToRefresh,
							success: function(msg){
									$j('#m' + courseClassToRefresh).replaceWith(msg);
									// set up the 'email a friend' modal link for ajax-loaded content
									$j('.nyromodaliframe').nyroModal( { type:'iframe'} );
									}
						});
					}	
			}
	});	
}