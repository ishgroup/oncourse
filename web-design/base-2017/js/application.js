/**
*  application.js
*
*  Base JS functionality that apply across all onCourse apps.
*
*
*/

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

function submitTellAFriend() {
	// process form here
	var code = $j("input#code").val();
	var name = $j("input#sendername").val();
	var email = $j("input#senderemail").val();
	var friendname = $j("input#friendname").val();
	var friendemail = $j("input#friendemail").val();
	var message = $j("#message").val();
	var dataString = 'sendername='+ name + '&senderemail=' + email + '&code=' + code
	+ '&friendname='+ friendname + '&friendemail=' + friendemail + '&source=ajax';
	if (message) dataString += '&message=' + message;
	$j.ajax({
		type: "POST",
		url: "/tellAFriend",
		data: dataString,
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//alert("Error: " + textStatus);
		},
		success: function(msg) {
		 // alert("Success: " + msg);
		 $j('#popup-content').html(msg);
		}
	});
}

function initAddStudentButton(){
	//	Show the "add a student" section
	$j(document.body).on('click', '#addstudent', function() {
		$j('#addstudent-block').slideToggle(400);
	});
}

function initAddConcessionButtons(){
	$j(document.body).on('click', '.add-concession', function() {
		$j('#'+this.id+'-block').slideToggle(400);
	});
}

function initSuburbAutoComplete(){

	if ($j(".suburb-autocomplete")) {
		$j(".suburb-autocomplete").autocomplete({source: '/ish/internal/autocomplete.sub', minLength: 3,
			select: function(event, ui) {
				setPostcodeAndStateFromSuburb(this.form, ui.item.value);
			}
		});

		$j(".suburb-autocomplete").autocomplete({
			close: function(event, ui) {
				setSuburbAfterAutoComplete(this.form);
			}
		});
	}

	// return selects the suburb from the popup list. Eat it so it doesn't submit the form.
	$j(document.body).on('keydown', '.suburb-autocomplete', function(key) {
		if (key.which == 13) {
			key.preventDefault();
			return false;
		}
	});

}
function initAddDiscountForm()
{

	$j(document.body).on('click', '#addDiscountButton', function() {
		var actionLink = "/ui/adddiscount:adddiscountevent";
		var f = $j("[id*=addDiscountForm]")[0];
		var data = $j(f).serialize();
		var refreshId = "#addDiscountZone";

		$j.ajax({
			type: "GET",
			url: actionLink,
			data: data,
			async: false,
			//if the parameter is not set internet explorer loads content from cache
			cache: false,
			success: function(data) {
				if (data.content)
				{
					$j(refreshId).html(data.content);
					initAddDiscountForm();
				}
			},
			error:  function(data) {
			}
		});

	});
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


(function($j) {
	$j(document.body).ready(function() {

		//	General navigation: Hide empty dropdown
		if (jQuery.browser.msie && parseInt(jQuery.browser.version) <= 8) {
			$j('#nav ul').each(function() {
				if ($j(this).children('li').size() == 0) {
					$j(this).css('visibility', 'hidden');
				}
			});
		}

		initAddStudentButton();
		initSuburbAutoComplete();
		initAddConcessionButtons();
		initAddDiscountForm();

		// if you hit the enter key in the EnrolmentContactEntry component, click "enrol" instead of paying
		// but not in the suburb autocomplete, where enter will select the suburb
		$j(document.body).on('keydown', 'fieldset#student_enrol_credentials input,fieldset#student_enrol_details input', function(e) {
			if (e.keyCode == 13 && $j(this).attr('class').indexOf('suburb-autocomplete')==-1) {
				$j("#add-student-enrol").click();
			}
		});

		// for ajax-inserted elements, register them to do the slide toggle
		$j(document.body).on('click', '.toggler', function() {
			$j(this).toggleClass("clicked");
			$j(this).toggleClass('toggler-expanded');
			$j(this).nextAll('.post-collapse:first').slideToggle(400);
			$j(this).nextAll('.collapse:first').slideToggle(400);
		});

		// display hidden imperfectly matching classes
		$j(document.body).on('click', '.more-classes-link', function() {
			$j(this).hide();
			$j(this).nextAll('.more-classes').slideToggle(400);
		});

		initOtherClassesControl();

		// hide the hints and errors at the start
		$j('.reason', '.hint').toggleClass('hidden-text');

		initHints();

		// When a user logs in, show them the company fields or the individual's fields
		$j(document.body).on('click', "#iscompany", function() {
			$j(this).nextAll('.company-details:first').slideToggle(400);
			$j(this).nextAll('.user-details:first').slideToggle(400);
		});

		// show default text in text fields with class defaultText using "default" attribute
		$j(document.body).on('focus', ".defaultText", function(srcc) {
			if ($j(this).val() == $j(this).attr("default"))
			{
				$j(this).removeClass("defaultTextActive");
				$j(this).val("");
			}
		});

		$j(document.body).on('blur', '.defaultText', function() {
			if ($j(this).val() == "")
			{
				$j(this).addClass("defaultTextActive");
				$j(this).val($j(this).attr("default"));
			}
		});
		$j(".defaultText").blur();

		// clear default values when submitting any form
		$j(document.body).on('submit', 'form', function() {
			clearDefaultValues();
			return true;
		});

		// Advanced search

		$j(document.body).on('click', '.show-advanced-search,div#advanced-search-background', function() {
			toggleAdvancedSearch();
		});

		$j(document.body).on('click', '#cancel-search', function() {
			toggleAdvancedSearch();
		});

		// make the search string in the advanced search update the basic form search input area
		$j(document.body).on('keyup', '#adv_keyword', function(e) {
			text = $j(this).val();
			$j("form#search > input[type=text]").val(text);
			if (e.keyCode == 13) $j('form#search2').submit();
		});

		if($j('input.quicksearch').length > 0) {
			$j('input.quicksearch').quickSearch("/advanced/keyword");
		}

		// reload the tell-a-friend form using JQuery ajax.
		// Have to do it this way because thickbox is incompatible with Wonder Ajax
		$j(document.body).on('click', '#submitTellAFriendLink', function() {
			submitTellAFriend();
			return false;
		});

		// on the course detail page, display the class info if it is small, or collapse it if it is too big, with a twiddle to expand
		$j("#course_detail .detail_for_class").each(function(i) {
			// only do this collapsing on the course list page
			elem = $j(this);
			if (elem.height() > 50) {
				var id = elem.attr('id');
				elem.before('<div class="show_more_class_info" style="clear:both;" id=x' + id + '><a href="javascript:void();"  >more</a></div>');
				$j('#x'+id).live('click', function() {
					$j('#' + id).show();
					this.hide();
				});
				elem.hide();
			} else {
				this.show();
			}
		});

		// if you give any HTML element the class "tooltip" then its title attribute will be displayed over the cursor on hover
		// requires jquery.tooltip plugin
		$j('.tooltip').tooltip({
			top: 0,
			track: true,
			delay: 0,
			showURL: false,
			showBody: " - ",
			fade: 250
		});

		$j(document.body).on('click', '.nyromodalreload', function(e) {
			e.preventDefault();
			var href = $j(this).attr('href');

			var closeFn = Ish.modal.openModal({
				content: '<iframe src="'+ href +'" width="100%" height="100%">'
				  +'<p>Your browser does not support iframes.</p>'
				+'</iframe>',
				animation: 'flip',
				duration: 600,
				width: 1000,
				height: 400
			});
		});

		var _modalCalled = false;
		$j(document.body).on('click', '.nyromodal', function(e) {
			e.preventDefault();
			var href = $j(this).attr('href');

			if(!_modalCalled) {
				_modalCalled = true;
				$j.ajax({
					url: href,
					type: 'GET',
					methodType: 'text',
					success: function(data) {
						if(data !== undefined) {
							var closeFn = Ish.modal.openModal({
								content: data,
								animation: 'flip',
								duration: 600,
								width: 1000,
								height: 400/*,
								onClose: function() { }*/
							});
							_modalCalled = false;
						}
					},
					error: function(error) {
						_modalCalled = false;
					}
				});
			}
		});

		// hide the location map after it has been filled by Google (above), then reveal it when its control is clicked.
		$j('.collapsedLocationMap').hide();
		$j(document.body).on('click', '.showLocationMap', function() {
			$j('.showLocationMap').hide();
			$j('#location').show();
			$j('#location').removeClass('collapsedLocationMap');
			mapLoadForID('map');
			return false;
		});

		// Show map in search results

		$j(document.body).on('click', '.toggle_locations', function(e){
			e.preventDefault();
			if ($j('#gmapCanvas').length&&!mapLoaded) {
				mapLoad('gmapCanvas', gMapSites, gMapOptions);
			}
			$j('#sitesMap').toggleClass('show');
		});

		$j(document.body).on('click', '.location-course', function() {
			var rel = $j(this).attr('rel');
			if(rel != '') {
				zoomMapForSite(rel);
			}
		});

		$j(document.body).on('click', 'a.timeline', function(e) {
			e.preventDefault();
			e.stopPropagation();
			var id = '#sessions_for_class_'+$j(this).parents('.classItem').attr('data-classid');

			var closeFn = Ish.modal.openModal({
				content: $j(id).html(),
				animation: 'flip',
				duration: 600,
				width: 1000,
				height: 400/*,
				onClose: function() { }*/
			});
		});

		$j(document.body).on('click', '#overlay, #timelineClose', function(e) {
			$j('overlay').hide();
			$j('timeline-wrap').style.visibility = 'hidden';
			return false;
		});


		// Add HTML5 placeholder in ie8
		$j(".ie8 input[type='text'], .ie9 input[type='text']").each(function() {
			if ($j(this).val() == "" && $j(this).attr("placeholder") != "") {
				$j(this).val($j(this).attr("placeholder"));
				$j(this).on('focus', function() {
					if ($j(this).val() == $j(this).attr("placeholder")) $j(this).val("");
				});
				$j(this).on('blur', function() {
					if ($j(this).val() == "") $j(this).val($j(this).attr("placeholder"));
				});
			}
		});


		var _processing = false;
		$j(document.body).on('click', '#showMoreCourses', function() {
			$j('#showMore').append("<div class='message'></div>");
			var link = this.href;
			_processing = true;

			$j.ajax({
				type: "GET",
				url:  link,
				success: function(msg) {
					$j('#showMore').replaceWith(msg);

					// Since this call possibly returns react markers,
					// we should told react about it.
					window.Ish.react.bootstrap();
					if($j.trim($j('#sitesParameter').text()) != '') {
						$j.ajax({
							type: "GET",
							url:  "/coursesSitesMap?sites="+$j('#sitesParameter').text(),
							success: function(msg1) {
								$j('#sitesMap').replaceWith(msg1);
								_processing = false;
							}
						});
					}
					else {
						_processing = false;
						clearInterval(loadCourses);
					}
				}
			});
			//initOtherClassesControl();
			return false;
		});

		$j(window).scroll(function() {
			if(($j('#content').innerHeight() + $j("#showMore").height()) < ($j(window).scrollTop() + $j(window).height() - $j('#header').height()) && _processing == false) {
				$j('#showMoreCourses').click();
			}
		});

		function IpadScroll() {
			if(($j('#content').innerHeight() + $j("#showMore").height()) < ($j(window).scrollTop() + $j(window).height() + $j('#header').height()) && _processing == false) {
				$j('#showMoreCourses').click();
			}
		}

		/*if( /iPhone|iPad/i.test(navigator.userAgent)) {
			document.addEventListener("scroll", IpadScroll, false);
		}*/

		var loadCourses = setInterval(function() {
			if( /iPhone|iPad/i.test(navigator.userAgent)) {
				IpadScroll();
			}
		}, 1000);

	});

})(jQuery);

function initHints(parentBlockId){
	parentBlockToUpdate='';
	if(parentBlockId!=null){
		parentBlockToUpdate = '#'+parentBlockId+' ';
	}

	// Show us hints on entering the field if the input is not invalid
	jQuery(document).bind('focus blur', parentBlockToUpdate+'span.valid input', function() {
		var parent = $j(this).siblings('.validate-text');
		if(parent.find('.hint').length==0) {
			//for the inputs that have one more image before hint (like calendar tapestry component)
			parent=parent.next();
		}
		parent.find('.hint').toggleClass('hidden-text');
		//$j(this).parent().nextAll('.hint:first').toggle();
	});

	// Show us reasons for errors on entering the field if the input IS invalid
	jQuery(document).bind('focus blur', parentBlockToUpdate+'span.validate input', function() {
		var parent = $j(this).siblings('.validate-text');
		if(parent.find('.reason').length==0){
			//for the inputs that have one more image before hint (like calendar tapestry component)
			parent=parent.next();
		}
		parent.find('.reason').toggleClass('hidden-text');
	});

	jQuery(document).bind('focus blur', parentBlockToUpdate+'span.validate select', function() {
		var parent = $j(this).siblings('.validate-text');
		if(parent.find('.reason').length==0){
			//for the inputs that have one more image before hint (like calendar tapestry component)
			parent=parent.next();
		}
		parent.find('.reason').toggleClass('hidden-text');
	});
}

function initOtherClassesControl(){

	// display other location classes
	$j(document.body).on('click', '.other-classes-control', function() {
		$j(this).hide();
		$j(this).nextAll('.other-classes').slideToggle(400);
	});

	// display hidden cancelled or full classes
	$j(document.body).on('click', '.full-classes-control', function() {
		$j(this).hide();
		$j(this).nextAll('.full-classes').slideToggle(400);
	});
}


// ------------------------ Timeline display ------------------

// displays a Timeline view by fetching class data.
// uses parameters defined in the (link) that was clicked:
// 		href = the url that will fetch the XML data
// 		rel = 'session' or 'class' determines which will be fetched and displayed
// 		timeZone = the time zone that will be used to position the classes
/*function displayTimeline(link) {
		var xmldata = link.href;
		var reldata = String(link.getAttribute('rel'));

		var arrayPageSize = getPageSize();
		$j('overlay').style.width = arrayPageSize[0] + 'px';
		$j('overlay').style.height = arrayPageSize[1] + 'px';
		var arrayPageScroll = getPageScroll();
		var padding = 10; // $j('timeline-wrap').style.padding; // hmm doesn't seem to be available
		$j('timeline-wrap').style.top = (arrayPageScroll[1] + (padding * 4)) + 'px';
		$j('timeline-wrap').style.bottom = '0px';
		$j('timeline-wrap').style.margin = '0px 0pt 0pt -375px';
		//--------------------------------------------------------

		new Effect.Appear('overlay', { duration: 0.2, from: 0.0, to: 0.4,
				afterFinish: function() {
						$j('timeline-wrap').style.visibility = 'visible';
				}
		});

		// Sept 2009 - add table of sessions underneath the timeline bands in the lightbox
		if (reldata == 'session') {
				// copy the session table contents from the hidden div in the CourseClassListItem and display below bands
				var classID = '#sessions_for_class_' + xmldata.substring(xmldata.indexOf('?ids=') + 5);
		$j('#timeline').html("");
				$j('#timeline').append('<div class="timeline_stuffer"/>&nbsp;</div><div class="sessions_in_timeline" style="overflow:auto; height:387px;">' + $j(classID).html() + '</div>');
		}
		return false;
	}*/

// Timeline support ----------------------



function getPageScroll() {

	var xScroll, yScroll;

	if (self.pageYOffset) {
		yScroll = self.pageYOffset;
		xScroll = self.pageXOffset;
	} else if (document.documentElement && document.documentElement.scrollTop) {     // Explorer 6 Strict
		yScroll = document.documentElement.scrollTop;
		xScroll = document.documentElement.scrollLeft;
	} else if (document.body) {// all other Explorers
		yScroll = document.body.scrollTop;
		xScroll = document.body.scrollLeft;
	}

	arrayPageScroll = new Array(xScroll, yScroll)
	return arrayPageScroll;
}

function getPageSize() {

	var xScroll, yScroll;

	if (window.innerHeight && window.scrollMaxY) {
		xScroll = window.innerWidth + window.scrollMaxX;
		yScroll = window.innerHeight + window.scrollMaxY;
	} else if (document.body.scrollHeight > document.body.offsetHeight) { // all but Explorer Mac
		xScroll = document.body.scrollWidth;
		yScroll = document.body.scrollHeight;
	} else { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
		xScroll = document.body.offsetWidth;
		yScroll = document.body.offsetHeight;
	}

	var windowWidth, windowHeight;

	// console.log(self.innerWidth);
	// console.log(document.documentElement.clientWidth);

	if (self.innerHeight) {    // all except Explorer
		if (document.documentElement.clientWidth) {
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
	if (yScroll < windowHeight) {
		pageHeight = windowHeight;
	} else {
		pageHeight = yScroll;
	}

	// console.log("xScroll " + xScroll)
	// console.log("windowWidth " + windowWidth)

	// for small pages with total width less then width of the viewport
	if (xScroll < windowWidth) {
		pageWidth = xScroll;
	} else {
		pageWidth = windowWidth;
	}

	// console.log("pageWidth " + pageWidth)

	arrayPageSize = new Array(pageWidth, pageHeight, windowWidth, windowHeight)
	return arrayPageSize;
}
