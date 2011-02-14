/**
 *  shortlist.js
 *
 *  Events and AJAX responses used to create/add/delete to the shortlist.
 * 
 */


function refreshShortList(courseClassToRefresh){
	/**
	 * Refreshes the shortList component and if the courseClassToRefresh!=null, 
	 * refreshes the shortlistControl for this class.
	 */
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

$j(document).ready(function() {
	

	/* New Shortlist functionality */
	/* Show/Hide shortlist */
	$j(".shortlistActionShow a").live("click", function() {

			$j("#shortlist .shortlistChoices").fadeToggle("fast");
			return false;
		});	

	// Add items to the shortlist return a response to the user
	$j('.enrolAction').live("click", function(){
		//grab the classid of the enrol button that's just been clicked.  
		var listid = $j(this).parents(".classItem").data("classid");
		var buttonPos = $j('.classItem[data-classid=' + listid + ']').position();
		
		// does it already exist in the shortlist? if not, make that ajax call.
		if (($j('.shortlistChoices li[data-classid=' + listid + ']') == null) | ($j('.shortlistChoices li[data-classid=' + listid + ']').length == 0)) { 
			
			$j.ajax({
				type: "GET",
				url:  '/addToCookies?key=shortlist&itemId=' + listid,
				success: function(){
							alert('success add');
							
							//Make the order confirmation box appear
							$j(".confirmOrderDialog p:first").text("Thanks for adding: ");
							$j(".confirmOrderDialog .className").show().text($j('.classItem[data-classid=' + listid + '] .classItemName + dd').text());
							$j(".confirmOrderDialog .classDate").show().text($j('.classItem[data-classid=' + listid + '] .classItemDate + dd').text());
							
							$j('.classItem [data-classid=' + listid + '] .enrolAction').text("Added");
							
							$j(".confirmOrderDialog").css({
								top: buttonPos.top,
								right: "150px"
							});
							
							$j(".confirmOrderDialog").stop(true, false).fadeIn("fast").delay(5000).fadeOut("fast");
							refreshShortList(listid);
							
						}
			});
		} else {
			// Else, let them know that it's already on their shortlist and get them to go to checkout
			$j(".confirmOrderDialog p:first").text("You've already added this class to your shortlist. Do you want to proceed to checkout?");
			$j(".confirmOrderDialog .className").empty();
			$j(".confirmOrderDialog .classDate").empty()
			
			$j(".confirmOrderDialog").css({
				top: buttonPos.top,
				right: "150px"
			});
			
			$j(".confirmOrderDialog").stop(true, false).fadeIn("fast").delay(5000).fadeOut("fast");
		}
		return false; 
	});
	
	
	/* Remove an item from the shortlist */
	$j("#shortlist .deleteItem a").live("click", function() {
		// This is accessing the custom data attribute set in Shortlist.tml - See http://html5doctor.com/html5-custom-data-attributes/
		var itemId = $(this).parent().parent().data('classid');
		
		$j.ajax({
			type: "GET",
			url:  '/removeFromCookies?key=shortlist&itemId='+ itemId,
			success: function(){
				refreshShortList(itemId);
			}
		});	
	});

	
	// Drop our shortlisted items in the shortlist box
	$j('li.onshortlist a.cutitem,li.onshortlist-x a.cutitem').live("click", function() {
		isPromotion=this.parentNode.className.endsWith("x");
		key=isPromotion?"promotions":"shortlist";
		var itemId = this.id.match(/(\d+)/)[1];	
		link=this.href;
		// this line is commented because otherwise the shortlist remove is not working
		//$j(this).parent('li:first').css({'background-color':'666666'}).hide("drop", { direction: "up" }, 300, function () { 
			//$j(this).hide("slide", { direction: "down" }, 300);
			$j.ajax({
				type: "GET",
				url:  '/removeFromCookies?key='+key+'&itemId='+ itemId,
				success: function(){
							if(isPromotion){
								window.location=link;
							}else{
								refreshShortList(itemId);
							}
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
				url:  '/removeFromCookies?key=shortlist&itemId=' + itemId,
				success: function(){
							refreshShortList(itemId);	 
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
			url:  '/addToCookies?key=shortlist&itemId=' + listid,
			success: function(){
				window.location=link;		 	
			}
		});
		return false; 
	});
});	
	