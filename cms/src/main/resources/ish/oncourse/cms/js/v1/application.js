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
	$j('#addstudent').click(function() {
		$j('#addstudent-block').slideToggle(400);
	});
}

function initAddConcessionButtons(){
	$j('.add-concession').click(function() {
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
	$j(".suburb-autocomplete").keydown(function(key) {
		if (key.which == 13) {
			key.preventDefault();
			return false;
		}
	}); 

}
function initAddDiscountForm()
{

	$j('#addDiscountButton').click(function()
	{
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


var $j = jQuery.noConflict();

$j(document).ready(function() {

	//	General navigation: Hide empty dropdown
	if ($j.browser.msie && parseInt($j.browser.version) <= 8) {
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
	$j("fieldset#student_enrol_credentials input,fieldset#student_enrol_details input").keydown(function(e) { 
	  if (e.keyCode == 13 && $j(this).attr('class').indexOf('suburb-autocomplete')==-1) {
		  $j("#add-student-enrol").click(); 
	  } 
	}); 


	// for ajax-inserted elements, register them to do the slide toggle
	$j('.toggler').live("click", function() {
	  	$j(this).toggleClass("clicked");
		$j(this).toggleClass('toggler-expanded');
	    $j(this).nextAll('.post-collapse:first').slideToggle(400);
		$j(this).nextAll('.collapse:first').slideToggle(400);
	});
	

	// display hidden imperfectly matching classes
	$j('.more-classes-link').click(function() {
		$j(this).hide();
		$j(this).nextAll('.more-classes').slideToggle(400);
	});
	
	initOtherClassesControl();
	
	// hide the hints and errors at the start
	$j('.reason', '.hint').toggleClass('hidden-text');
  
	initHints();
	
	// When a user logs in, show them the company fields or the individual's fields
	$j("#iscompany").click ( function() {
		$j(this).nextAll('.company-details:first').slideToggle(400);
		$j(this).nextAll('.user-details:first').slideToggle(400);
	});   

	// show default text in text fields with class defaultText using "default" attribute
	$j(".defaultText").focus(function(srcc)
    {
        if ($j(this).val() == $j(this).attr("default"))
        {
            $j(this).removeClass("defaultTextActive");
            $j(this).val("");
        }
    });
    
    $j(".defaultText").blur(function()
    {
        if ($j(this).val() == "")
        {
            $j(this).addClass("defaultTextActive");
            $j(this).val($j(this).attr("default"));
        }
    });
    $j(".defaultText").blur();        

	// clear default values when submitting any form
	$j('form').submit(function() {
		clearDefaultValues();
		return true;
	}); 
        
	// Advanced search
		
	$j('.show-advanced-search,div#advanced-search-background').click(function() {
		toggleAdvancedSearch();
	}); 
	
	$j('#cancel-search').click(function() {
		toggleAdvancedSearch();
	}); 
	
	// make the search string in the advanced search update the basic form search input area
	$j("#adv_keyword").keyup(function(e) { 
		text = $j(this).val();
		$j("form#search > input[type=text]").val(text);
		if (e.keyCode == 13) $j('form#search2').submit();
	});
	
	if ($j('input.quicksearch')) {
		$j('input.quicksearch').quickSearch("/advanced/keyword");
	}
	
	
		
	// reload the tell-a-friend form using JQuery ajax. 
	// Have to do it this way because thickbox is incompatible with Wonder Ajax
	  $j("#submitTellAFriendLink").live("click", function() {  
		  submitTellAFriend();
		return false;  
	  });  

	  // on the course detail page, display the class info if it is small, or collapse it if it is too big, with a twiddle to expand
		 $j("#course_detail .detail_for_class").each( function(i) { 
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
		$j('.tooltip').tooltip( {
			top: 0,
		    track: true, 
		    delay: 0, 
		    showURL: false, 
		    showBody: " - ", 
		    fade: 250 
		} );
		
		 // Convenience methods for nyroModal lightbox library, the successor to thickbox. 
		 // Add one of these classes to a link, with the href pointing to the content to be loaded.
		 // See http://nyromodal.nyrodev.com/ for documentation
		 
		 // open an inline lightbox using ajax content fetched from the link.
		 //$j('.nyromodal').nyroModal( {debug:false } );

		$j('.nyromodal').live('click', function(e) {
			e.preventDefault();
			var href = $j(this).attr('href');
			if(href != '') {
				$j.nmManual(href);
			}

		});
		 
		initNyromodal();
		 
		// hide the location map after it has been filled by Google (above), then reveal it when its control is clicked.
		$j('.collapsedLocationMap').hide();
		$j('.showLocationMap').click( function() {
			$j('.showLocationMap').hide();
			$j('#location').show();
			$j('#location').removeClass('collapsedLocationMap');
			mapLoadForID('map');
			return false;
		});

// Show map in search results

        $j('.toggle_locations').click(function(e){
	        e.preventDefault();
	        if ($j('#gmapCanvas').length&&!mapLoaded) {
				mapLoad('gmapCanvas', gMapSites, gMapOptions);
			}
            $j('#sitesMap').toggleClass('show');
        });

	$j('.location-course').on('click', function() {
		var rel = $j(this).attr('rel');
		if(rel != '') {
			zoomMapForSite(rel);
		}
	});

    $j('body').on('click','a.timeline', function(e) {
        e.preventDefault();
        e.stopPropagation();
		var id = '#sessions_for_class_'+$j(this).parents('.classItem').attr('data-classid');		
		$j.nmObj({sizes: { minW: 940, minH: 580 }});
		$j.nmData($j(id).html(), {autoSizable: true});
    });

    $j('#overlay, #timelineClose').on('click', function(e) {
	    $('overlay').hide();
	    $('timeline-wrap').style.visibility = 'hidden';
        return false;
    });

    // Add HTML5 placeholder in ie8
    $j(".ie8 input[type='text'], .ie9 input[type='text']").each(
        function() {
            if ($j(this).val() == "" && $j(this).attr("placeholder") != "") {
                $j(this).val($j(this).attr("placeholder"));
                $j(this).focus(function() {
                    if ($j(this).val() == $j(this).attr("placeholder")) $j(this).val("");
                });
                $j(this).blur(function() {
                    if ($j(this).val() == "") $j(this).val($j(this).attr("placeholder"));
                });
            }
        });

	
    var _processing = false;
    $j('#showMoreCourses').live("click", function() {
    	$j('#showMore').append("<div class='message'></div>");
		var link = this.href;
		_processing = true;
		$j.ajax({
			type: "GET",
            url:  link,
            success: function(msg) { 
            	$j('#showMore').replaceWith(msg);
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
		initNyromodal();
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
	
	var loadCourses = setInterval(function() {
		if( /iPhone|iPad/i.test(navigator.userAgent)) {
			IpadScroll();
		}
	}, 1000);
	
	
	
});

function initHints(parentBlockId){
	parentBlockToUpdate='';
		if(parentBlockId!=null){
			parentBlockToUpdate = '#'+parentBlockId+' ';
		}
	 	// Show us hints on entering the field if the input is not invalid
		$j(parentBlockToUpdate+'span.valid input').bind("focus blur", function() {
			var parent = $j(this).siblings('.validate-text');
			if(parent.find('.hint').length==0){
				//for the inputs that have one more image before hint (like calendar tapestry component)
				parent=parent.next();
			}
			parent.find('.hint').toggleClass('hidden-text');
	 		//$j(this).parent().nextAll('.hint:first').toggle();
	 	});
	 
	 	// Show us reasons for errors on entering the field if the input IS invalid
		$j(parentBlockToUpdate+'span.validate input').bind("focus blur", function() {
            var parent = $j(this).siblings('.validate-text');
			if(parent.find('.reason').length==0){
				//for the inputs that have one more image before hint (like calendar tapestry component)
				parent=parent.next();
			}
			parent.find('.reason').toggleClass('hidden-text');
	 	});
		$j(parentBlockToUpdate+'span.validate select').bind("focus blur", function() {
            var parent = $j(this).siblings('.validate-text');
			if(parent.find('.reason').length==0){
				//for the inputs that have one more image before hint (like calendar tapestry component)
				parent=parent.next();
			}
			parent.find('.reason').toggleClass('hidden-text');
	 	});
}

function initNyromodal(){
	 // open an iFrame box, so forms will submit inside the box.
	 // note that for the iframe to be generated, the link must include target="_blank"
	 $j('.nyromodaliframe').nyroModal( {
		 type:'iframe'
	 } );

	 // open an iFrame box and reload the parent page on closing



	 $j('.nyromodalreload').nyroModal({
		 type:'iframe',
		 endRemove: function() {window.location.reload(true);}
	 });
}

function initOtherClassesControl(){

    // display other location classes
    $j('.other-classes-control').live('click',function() {
		$j(this).hide();
		$j(this).nextAll('.other-classes').slideToggle(400);
	});

    // display hidden cancelled or full classes
    $j('.full-classes-control').live('click',function() {
		$j(this).hide();
		$j(this).nextAll('.full-classes').slideToggle(400);
	});

}


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

    // for small pages with total width less then width of the viewport
    if (xScroll < windowWidth) {
        pageWidth = xScroll;
    } else {
        pageWidth = windowWidth;
    }

    arrayPageSize = new Array(pageWidth, pageHeight, windowWidth, windowHeight)
    return arrayPageSize;
}





