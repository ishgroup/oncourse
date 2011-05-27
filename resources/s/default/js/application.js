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
		$j(".suburb-autocomplete").autocomplete({source: '/ui/autocomplete.sub', minLength: 3, 
			select: function(event, ui) {
				setPostcodeAndStateFromSuburb(this.form, ui.item.value);
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
	initAddStudentButton();
	initSuburbAutoComplete();
	initAddConcessionButtons();
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
	
	// display hidden cancelled or full classes
	$j('.other-classes-control').click(function() {
		$j(this).hide();
		$j(this).nextAll('.other-classes').slideToggle(400);
	});
	
	
  
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
		 $j('.nyromodal').nyroModal( {debug:false } );
		 
		 // open an iFrame box, so forms will submit inside the box.
		 // note that for the iframe to be generated, the link must include target="_blank"
		 $j('.nyromodaliframe').nyroModal( {
			 type:'iframe'
		 } );

		 // open an iFrame box and reload the parent page on closing
		 $j('.nyromodalreload').nyroModal( {
			 type:'iframe', 
			 endRemove: function() {window.location.reload(true);}
		 } );
		 
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
        $j('#toggle-results-map').click(function() {
            $j('#sitesMap').toggle();
            $j(this).toggleClass('clicked');
            loaded = $j(this).attr('loaded');
            if (!loaded) {
                $j(this).attr({loaded:true});
                mapLoadForID('map');
            }
            google.maps.event.trigger(map, 'resize');
            return false;
        });
		
		$j('body').css('border-bottom', '5px solid #f7f7f7');
});

function initHints(parentBlockId){
	parentBlockToUpdate='';
		if(parentBlockId!=null){
			parentBlockToUpdate = '#'+parentBlockId+' ';
		}
	 	// Show us hints on entering the field if the input is not invalid
		$j(parentBlockToUpdate+'span.valid input').bind("focus blur", function() {
			parent=$j(this).next().next();
			if(parent.children('.hint').length==0){
				//for the inputs that have one more image before hint (like calendar tapestry component)
				parent=parent.next();
			}
			parent.children('.hint').toggleClass('hidden-text');
	 		//$j(this).parent().nextAll('.hint:first').toggle();
	 	});
	 
	 	// Show us reasons for errors on entering the field if the input IS invalid
		$j(parentBlockToUpdate+'span.validate input').bind("focus blur", function() {
			parent=$j(this).next().next();
			if(parent.children('.reason').length==0){
				//for the inputs that have one more image before hint (like calendar tapestry component)
				parent=parent.next();
			}
			parent.children('.reason').toggleClass('hidden-text');
	 	});
		$j(parentBlockToUpdate+'span.validate select').bind("focus blur", function() {
			parent=$j(this).next().next();
			if(parent.children('.reason').length==0){
				//for the inputs that have one more image before hint (like calendar tapestry component)
				parent=parent.next();
			}
			parent.children('.reason').toggleClass('hidden-text');
	 	});
	 }
