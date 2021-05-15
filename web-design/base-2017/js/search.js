/**
* search.js
*
*	License: copyright ish group
* Purpose:
*  Events and AJAX responses used to interact with the search app.
*
*	 - Quicksearch
*  - Find a suburb by name or postcode
*	 - Advanced search
*
*/

// Override search more text: More Options
if(searchMoreText === undefined)
	var searchMoreText = "More options";

// Override search fewer text: Fewer Options
if(searchFewerText === undefined)
	var searchFewerText = "Fewer options";


// open the infoWindow on the map with the site details for the site ID specified
function showSiteOnMap(siteID) {
	$j('#focus-map').show();
	$j("body").animate({ scrollTop: 0 }, "slow");

	if(window.map==null) {
		window.siteIDtoShow = siteID;
		mapLoadForID('mapDelayed');
	} else {
		id = 'id' + siteID;
		//console.log(".......... loading site info for " + id);
		marker = window.siteMarkers[id];
		//console.log(".......... loading site info for " + marker.title);
		if(marker) {
			window.map.openInfoWindowHtml( marker.getLatLng(), infoWindowContent(marker));
		} else {
			//console.log("No marker for " + id);
		}
	}
}

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
// $j('div').ClearTypeFadeIn({ speed: 1500 });
// $j('div').ClearTypeFadeIn({ speed: 1500, bgColor: '#ff6666', callback: myCallback });
// $j('div').ClearTypeFadeOut({ speed: 1500, callback: function() { alert('Fade Out complete') } });
//
// Notes on the interaction of ClearType with DXTransforms in IE7
// http://blogs.msdn.com/ie/archive/2006/08/31/730887.aspx
jQuery.fn.ClearTypeFadeTo = function(options) {
	if(options) {
		$j(this)
		.show()
		.fadeTo(options.speed, options.opacity);
	}
};

jQuery.fn.ClearTypeFadeIn = function(options) {
	if(options) {
		$j(this)
		.css({ opacity: 0 })
		.ClearTypeFadeTo({ speed: options.speed, opacity: 1 });
	}
};

jQuery.fn.ClearTypeFadeOut = function(options) {
	if(options) {
		$j(this)
		.css({ opacity: 1 })
		.ClearTypeFadeTo({ speed: options.speed, opacity: 0 });
	}
};

/* -----*/
// Override advanced search action method
if(advancedSearchAction === undefined)
	var advancedSearchAction = function(isVisible) { }

function advancedSearchCheck(isVisible) {
	if(advancedSearchAction !== undefined && typeof advancedSearchAction == "function")
		advancedSearchAction(isVisible);
}

// Toggles advanced search
function toggleAdvancedSearch() {
	if($j('#advanced_search').is(':visible')) {
		hideAdvancedSearch();
	} else {
		showAdvancedSearch();
	}
}

// Show advanced search form
function showAdvancedSearch() {
	$j('a.show-advanced-search span').addClass('adv_search_active').html(searchFewerText);

	$j('form#search').ClearTypeFadeTo({ speed: 450, opacity: 0.4 });
	$j('div#content').ClearTypeFadeTo({ speed: 450, opacity: 0.4 });
	$j('#advanced_search').show('slow', function() { });

	advancedSearchCheck(true);

	// install watcher to detect clicks on the background
	$j(document).on('mousedown', function(e) {
		// hide advanced search if click was not inside advanced search area
		if(!$j(e.target).is('#advanced_search_container *') && !$j(e.target).is('div.ac_results *') && !$j(e.target).is('.ui-autocomplete *')) {
			hideAdvancedSearch();
		}

	});
}

// Hide advanced search form
function hideAdvancedSearch() {
	$j(document).off('mousedown');
	$j('form#search').ClearTypeFadeTo({ speed: 450, opacity: 1 });
	$j('div#content').ClearTypeFadeTo({ speed: 450, opacity: 1 });
	$j('#advanced_search').hide('slow');

	$j('a.show-advanced-search span').removeClass('adv_search_active').html(searchMoreText);

	advancedSearchCheck(false);
}

// Clear form fields
jQuery.fn.clearForm = function() {
	return this.each(function() {
		var type = this.type, tag = this.tagName.toLowerCase();

		if(tag == 'form')
			return $j(':input',this).clearForm();

		if(type == 'text' || type == 'password' || tag == 'textarea')
			this.value = '';
		else if(type == 'checkbox' || type == 'radio')
			this.checked = false;
		else if(tag == 'select')
			this.selectedIndex = -1;
	});
};

// Clear advanced search form fields
function clearAdvancedSearch() {
	$j('#search2').clearForm();
}


// clear default values from text fields with class defaultText that match the "default" attribute
function clearDefaultValues() {
	$j(".defaultText").each(function(srcc) {
		if($j(this).val() == $j(this).attr("default")) {
			$j(this).val("");
		}
	});
}

/* --------------- suburbs, states and postcodes ----------------- */

// extract suburb name, eg NEWTOWN from 'NEWTOWN 2042'
function suburbFromString(s) {
	postcodeLength = 4;
	if(s.length >= postcodeLength) {
		postcode = parseInt(s.substring(s.length-postcodeLength, s.length));
		if(postcode) {
			return jQuery.trim(s.substring(0,s.length-postcodeLength));
		}
	}
	return s;
}

//extract postcode, eg 2042 from 'NEWTOWN 2042'
function postcodeFromString(s) {
	postcodeLength = 4;
	if(s.length >= postcodeLength) {
		postcode = parseInt(s.substring(s.length-postcodeLength, s.length));
		if(postcode) {
			return "" + postcode;
		}
	}
	return '';
}

// work out state from an Australian postcode
function stateFromPostcode(postcode) {
	if(postcode.length < 4) return '';

	p = parseInt(postcode);
	if(p == null) return '';
	if(p >= 100 && p <= 2599) return "NSW";
	if(p >= 2619 && p <= 2898) return "NSW";
	if(p >= 2921 && p <= 2999) return "NSW";
	if(p >= 200 && p <= 299) return "ACT";
	if(p >= 2600 && p <= 2618) return "ACT";
	if(p >= 2900 && p <= 2920) return "ACT";
	if(p >= 3000 && p <= 3999) return "VIC";
	if(p >= 8000 && p <= 8999) return "VIC";
	if(p >= 4000 && p <= 4999) return "QLD";
	if(p >= 9000 && p <= 9999) return "QLD";
	if(p >= 5000 && p <= 5999) return "SA";
	if(p >= 6000 && p <= 6999) return "WA";
	if(p >= 7000 && p <= 7999) return "TAS";
	if(p >= 800 && p <= 999) return "NT";
	return '';
}


// set postcode and state fields from value of suburb field and remove the postcode from the suburb field
// if the postcode field does not exist, leave the suburb field alone
function setPostcodeAndStateFromSuburb(searchForm, suburbString) {
	if(suburbString) {
		var suburb = suburbFromString(suburbString);
		var postcode = postcodeFromString(suburbString); // otherwise it thinks it's a number
		var state = postcode.length==0 ? '' : stateFromPostcode(postcode);
		if(suburb.length > 0) {
			if(searchForm.postcode && postcode.length>0) {
				$j("#"+searchForm.postcode.id).val(postcode);
				//$j("."+suburbID).val(suburb);
			}
			if(searchForm.state && state.length>0) {
				$j("#"+searchForm.state.id).val(state);
			}
		}
	}
	return false;
}

// sets suburb field after auto complete dialog closes
function setSuburbAfterAutoComplete(searchForm) {
	var suburb = suburbFromString($j("#"+searchForm.suburb.id).val());
	if(suburb.length > 0) {
		$j("#"+searchForm.suburb.id).val(suburb);
	}
}

// Describe advanced search form
jQuery.fn.quickSearch = function(url, settings) {
	return this.each(function() {
		var minInput = 3;
		var textInput = $j(this);
		var divContainer = $j('.quicksearch-wrap');
		var thisObject = this;
		var selectedIndex = -1;

		// function triggered after contents is updated
		updatedFunction = function() {
			show();

			// observe checkboxes
			$j('.suburb-choice').each(function() {
				$j(this).on("click", updateFunction);
			});
		};

		loadFunction = function(url, params, callbackFunction) {
			selectedIndex = -1;
			divContainer.load(url, params, callbackFunction);
		};

		// function for updating the list via site selections
		updateFunction = function withToggle(event) {
			var srcElement = null;
			if(event && (srcElement = event['srcElement'])) {

				// build array of query params
				var params = [];
				$j('.suburb-choice:checked').each(function() {
					var me = $j(this);
					var val = me.attr('value');
					params.push(val);
				});

				textInput.addClass('throbber');
				var terms = textInput.val();
				loadFunction(url, {text: terms, suburb: params}, updatedFunction);
			}
		};

		// take terms and query the server-side
		matchesFunction = function getMatches(terms) {
			if(!terms || terms.length < minInput) {
				hide();
				return;
			}

			textInput.addClass('throbber');
			loadFunction(url, {text: terms, directSearch:true}, updatedFunction);
		};

		// Show advanced search
		function show() {
			textInput.removeClass('throbber');
			$j('div.advanced-search-button').fadeTo(0, 0);
			hideAdvancedSearch();
			divContainer.addClass('show-quick-search').show();

			// install watcher to detect clicks on the background
			$j(document).on('mousedown.quicksearch', function(click) {
				// hide quicksearch if click was not inside quicksearch area
				if($j(click.target).parents('div.quicksearch-wrap').length == 0 && click.target != thisObject) {
					hide();
				}
			});
		}

		// Hide advanced search
		function hide() {
			selectedIndex = -1;
			allItems = null;
			textInput.removeClass('throbber');
			//divContainer.removeShadow();
			$j(document).off('mousedown.quicksearch');
			divContainer.slideUp(400);
			$j('div.advanced-search-button').fadeTo(0, 1);
			divContainer.removeClass('show-quick-search');
		}

		// Handles keyup events for text input
		textInput.on('keyup', function(key) {
			if(key.which == 27) { // escape
				hide();
				return false;
			}

			// copy the text of the quick search field into the advanced search field
			$j("#adv_keyword").val($j(this).attr('value'));

			matchesFunction(textInput.val());

			oldIndex = selectedIndex;
			allItems = divContainer.children('div').children('ul').children('li').not('.title');
			selectedItem = null;

			if(allItems.size() > 0) {
				if(selectedIndex >= 0) {
					selectedItem = allItems.eq(selectedIndex);
				}

				if(key.which == 13) { // return key
					if(selectedItem == null) {
						$j('form#search').submit();
						return true;
					} else {
						window.location = selectedItem.children('a').attr('href');
						key.preventDefault();
						return false;
					}
				}


				if(allItems.size() == 1 && (key.which == 40 || key.which == 9 || key.which == 38)) {
					selectedIndex = 0;
				} else {
					if(key.which == 40 || key.which == 9) { // down key
						if(selectedIndex >= (allItems.size() - 1)) {
							selectedIndex = 0;
						} else {
							selectedIndex++;
						}
					}

					if(key.which == 38) { // up key
						if(selectedIndex <= 0) {
							selectedIndex = allItems.size() - 1;
						} else {
							selectedIndex--;
						}
					}
					// update if necessary
					if(selectedIndex != oldIndex) {
						if(selectedItem != null) {
							selectedItem.removeClass('selected');
						}

						allItems.eq(selectedIndex).addClass('selected');
					}
				}

				return false;
			}
			return true;
		});
	});
};

// Describe hide quick search form
jQuery.fn.hideQuickSearch = function(url, settings) {
	return this.each(function() {
		this.hide();
	})
};
