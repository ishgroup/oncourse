var $j = jQuery.noConflict();

$j(document).ready(function() {
	// Show the "add a student" section
	$j('#addstudent').click(function() {
	$j('#addstudent-block').slideToggle(400);
	});
	
	// if you hit the enter key in the EnrolmentContactEntry component, click "enrol" instead of paying
	// but not in the suburb autocomplete, where enter will select the suburb
	$j("fieldset#student_enrol_credentials input,fieldset#student_enrol_details input").keydown(function(e) { 
	  if (e.keyCode == 13 && $j(this).attr('class').indexOf('suburb-autocomplete')==-1) {
		  $j("#add-student-enrol").click(); 
	  } 
	}); 

	// Toggle elements that we do want to see, but only sometimes
	//		$j('.toggler').click(function() {
			// these conflict with liveClick
	//			$j(this).toggleClass('clicked');
	//			$j(this).toggleClass('toggler-expanded');
	//			$j(this).nextAll('.collapse:first').slideToggle(400);
	//		});

	// for ajax-inserted elements, register them to do the slide toggle
	$j('.toggler').live("click", function() {
	  	$j(this).toggleClass("clicked");
		$j(this).toggleClass('toggler-expanded');
	    $j(this).nextAll('.post-collapse:first').slideToggle(400);
		$j(this).nextAll('.collapse:first').slideToggle(400);
	});
	
	// show popup when mouse over class details
	// done this way because CSS-based hover reveal gives z-index problems in MSIE
	$j('.class_details').live("mouseover", function() {
		$j(this).children('.bubbleInfo').show();
	});
	
	// hide popup when mouse leaves class details
	$j('.class_details').live("mouseout", function() {
		$j(this).children('.bubbleInfo').hide();
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
	
	// highlight all the search criteria when hovering over the clear button
	$j('span.clear-search a').hover(
		  function() {$j('.search-criteria').addClass('clearing-search');},
		  function() {$j('.search-criteria').removeClass('clearing-search');}
	);
  
	$j('#toggle-results-map').click( function() {
		$j('#focus-map').slideToggle(400);
		$j(this).toggleClass('clicked');
		loaded = $j(this).attr('loaded');
		if (!loaded) {
			$j(this).attr({loaded:true});
			mapLoadForID('mapDelayed');
		}
		return false;
	});
	// hide the hints and errors at the start
	$j('.reason', '.hint').toggleClass('hidden-text');
  
	initHints();
	
	// When a user logs in, show them the company fields or the individual's fields
	$j("#iscompany").click ( function() {
		$j(this).nextAll('.company-details:first').slideToggle(400);
		$j(this).nextAll('.user-details:first').slideToggle(400);
	});   

	// set up the date picker fields
	//birthdate is disabled because the earliest date datepicker shows is 1981.
	//if ($j("#birthdate")) {
	//	$j("#birthdate").datepicker({ dateFormat:'dd/mm/yy', goToCurrent:true, 
	//		maxDate:0, minDate:-10000,  yearRange: '1900:2010',  hideIfNoPrevNext:true });
	//}

	if ($j("#datepicker")) {
		$j("#datepicker").datepicker();
	}
    
	// Google maps
	try {
		if ($j('#map').length) mapLoad(); 
	} catch(e) {
		// ignore this-- it means there was no real map on the page after all
	}
	if (document.getElementById("dirmap")) dirLoad();

	// Add items to the shortlist & update the add/remove link
	$j('li.class_addorder a').live("click", function(){
		var listid = this.id.match(/(\d+)/)[1];
		$j.ajax({
			type: "GET",
			url:  '/addToCookies?courseClassId=' + listid,
			success: function(){
						$j.ajax({
							type: "GET",
							url:  '/refreshShortList',
							success: function(msg){
										$j('#shortlist').replaceWith(msg);	
											$j.ajax({
												type: "GET",
												url:'/refreshShortListControl?courseClassId=' + listid ,
												success: function(msg){
													$j('#m' + listid).replaceWith(msg);
													// set up the 'email a friend' modal link for ajax-loaded content
													$j('.nyromodaliframe').nyroModal( {type:'iframe'} );
												}
											});
							}
						});		 	
					}
		});
		return false; 
	});
	
	
	// Drop our shortlisted items in the shortlist box
	$j('li.onshortlist a.cutitem').live("click", function() {
		var itemId = this.id.match(/(\d+)/)[1];			
		// this line is commented because otherwise the shortlist remove is not working
		//$j(this).parent('li:first').css({'background-color':'666666'}).hide("drop", { direction: "up" }, 300, function () { 
			//$j(this).hide("slide", { direction: "down" }, 300);
			$j.ajax({
				type: "GET",
				url:  '/removeFromCookies?courseClassId=' + itemId,
				success: function(){
							$j.ajax({
								type: "GET",
								url: '/refreshShortList',
								success: function(msg){
											$j('#shortlist').replaceWith(msg);
											$j.ajax({
												type: "GET",
												url: '/refreshShortListControl?courseClassId=' + itemId,
												success: function(msg){		
															$j('#m' + itemId).replaceWith(msg);
															// set up the 'email a friend' modal link for ajax-loaded content
															$j('.nyromodaliframe').nyroModal( { type:'iframe' } );
														}
											});
										}
							});
						}
			});
		//});
		return false;
	});

	// Drop our short listed items from the class description
	$j('li.shortlisted a').live("click", function() {
		var itemId = this.id.match(/(\d+)/)[1];
		// this line is commented because otherwise the shortlistControl remove is not working
		//$j('#shortlist a#x' + itemId).parent('li:first').css({'background-color':'666666'}).hide("drop", { direction: "up" }, 300, function () {
			$j(/*this*/'#shortlist a#x' + itemId).hide("slide", { direction: "down" }, 300);
			$j.ajax({
				type: "GET",
				url:  '/removeFromCookies?courseClassId=' + itemId,
				success: function(){
							$j.ajax({
								type: "GET",
								url:  '/refreshShortList',
								success: function(msg){
											$j('#shortlist').replaceWith(msg);	
											$j.ajax({
												type: "GET",
												url:  '/refreshShortListControl?courseClassId=' + itemId,
												success: function(msg){
															$j('#m' + itemId).replaceWith(msg);
															// set up the 'email a friend' modal link for ajax-loaded content
															$j('.nyromodaliframe').nyroModal( { type:'iframe'} );
															}
											});
										}
							});		 
						}
			});
 		//});	
 		return false;
	});
	
	$j('li.class_enrol a').live("click", function(){
		var listid = this.id.match(/(\d+)/)[1];
		var link=this.href;
		$j.ajax({
			type: "GET",
			url:  '/addToCookies?courseClassId=' + listid,
			success: function(){
				window.location=link;		 	
			}
		});
		return false; 
	});
	

	// form 
	if ($j(".suburb-autocomplete")) {
		var data = "/advanced/suburbs";
		$j(".suburb-autocomplete").autocomplete(data, 
					{	mustMatch:false, 
						matchContains:true, 
						autoFill:false, 
						scrollHeight: 220, 
						selectFirst:false, 
			
						formatResult:function(item){
	        				if(item[0].startsWith('<li>')){
	        					return item[0].replace('<li>', '').replace('</li>', "");
	        				}else{
	        					return item;
	        				}
	        			}
					});
	}

	
	$j(".suburb-autocomplete").result(function(event, data, formatted) {
		if (formatted && formatted.length>0) {
			setPostcodeAndStateFromSuburb($j(this).attr('id'), 'postcode', 'state');
		}
	});
	
	// return selects the suburb from the popup list. Eat it so it doesn't submit the form.
	$j(".suburb-autocomplete").keydown(function(key) {
		if (key.which == 13) {
			key.preventDefault();
			return false;
		}
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
		
	$j('.search-terms,.show-advanced-search,div#advanced-search-background').click(function() {
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
	
	/*
	if ($j('#webContent')) {
		editWebContentWithCKEditor( 'webContent');
	}
*/
	var monday = 1;
	var tuesday = 2;
	var wednesday = 3;
	var thursday = 4;
	var friday = 5;
	var saturday = 6;
	var sunday = 7;
	var weekday = 8;
	var weekend = 9;
	var time = 10;
	var near = 11;
	var price = 12;
	
	$j.each([monday, tuesday,wednesday,thursday,friday,saturday,sunday, weekday, weekend, time, near, price], function(i,n) { 
		
		$j(".match-" +n).mouseover(function() { 
			$j(".match-" +n).addClass('search-highlight');  
		});    
		
		$j(".match-" +n).mouseout(function() { 
			$j(".match-" +n).removeClass('search-highlight');  
		}); 
	});

	// turn on all week days when "week days" is checked
	$j('#weekday-parent').click (
		function() {
		$j("#weekdays INPUT[type='checkbox']").attr('checked', $j('#weekday-parent').is(':checked'));
	});
	
	// turn off "week days" when any day is turned off, turn it on when all week days are on
	$j("#weekdays :checkbox").change( function(){
		if ( !$j(":checkbox:checked").length ) {
			//
		} else if ( $j("#weekdays :checkbox:not(:checked)").length ) {
			$j("#weekday-parent").attr('checked', false);      
		} else {
			$j("#weekday-parent").attr('checked', true);
		}
	});
	
	// turn on both weekend days when "weekends" is checked
	$j('#weekend-parent').click (
		function() {
		$j("#weekend INPUT[type='checkbox']").attr('checked', $j('#weekend-parent').is(':checked'));
	});
	
	// turn off "weekends" when any weekend day is turned off, turn it on when both weekend days are on
	$j("#weekend :checkbox").change( function(){
		if ( !$j(":checkbox:checked").length ) {
		}    
		
		else if ( $j("#weekend :checkbox:not(:checked)").length ) {
				$j("#weekend-parent").attr('checked', false);      
			}
			else {
				$j("#weekend-parent").attr('checked', true);
		}
	});	
	

	// display the Timeline when you click on any link with 'timeline' class. Needs to have the timeline content in the page.
	$j("a.timeline").click(function(e) {
		displayTimeline(this);
		return false;
	});
	$j('#overlay').click(function(e) {
		$('overlay').hide();
		$('timeline-wrap').style.visibility = 'hidden';
		return false;
	});
	
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
			 
});
	
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
