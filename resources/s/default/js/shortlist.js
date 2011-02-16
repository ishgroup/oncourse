/**
 *  shortlist.js
 *
 *  Events and AJAX responses used to create/add/delete to the shortlist.
 * 
 */


function refreshShortList(){
	/**
	 * Refreshes the shortList component and if the courseClassToRefresh!=null, 
	 * refreshes the shortlistControl for this class.
	 */
	$j.ajax({
		type: "GET",
		url:  '/refreshShortList',
		success: function(msg){
					$j('#shortlist').replaceWith(msg);	
			}
	});	
}

$j(document).ready(function() {
	

	/* New Shortlist functionality */
	/* Show/Hide shortlist */
	$j(".shortlistActionShow a").live("click", function() {

			$j("#shortlist .shortlistChoices").fadeToggle("fast").delay(5000);
			return false;
		});	
	
	$j(".closebutton").live("click", function() {
		$j(this).parents(".dialogContainer").fadeOut("fast");
	});
	
	// Add items to the shortlist return a response to the user
	$j('.enrolAction').live("click", function(){
		//grab the classid of the enrol button that's just been clicked.  
		var listid = $j(this).parents(".classItem").data("classid");
		var buttonPos = $j('.classItem[data-classid=' + listid + ']').position();
		var link=this.href;
		
		// does it already exist in the shortlist? if not, make that ajax call.
		if (($j('.shortlistChoices li[data-classid=' + listid + ']') == null) | ($j('.shortlistChoices li[data-classid=' + listid + ']').length == 0)) { 
			
			$j.ajax({
				type: "GET",
				url:  link,
				success: function(msg){
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
							$j('#shortlist').replaceWith(msg);	
							
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
		var link=this.href;
		$j.ajax({
			type: "GET",
			url:  link,
			success: function(msg){
				$j('#shortlist').replaceWith(msg);	
			}
		});	
		return false;
	});

	
	// Drop our shortlisted items in the shortlist box
	$j('li.onshortlist-x a.cutitem').live("click", function() {
		var link=this.href;
		var itemId = this.id.match(/(\d+)/)[1];	
			$j.ajax({
				type: "GET",
				url:  link,
				success: function(){
							window.parent.location.reload(true);
						}
			});
		return false;
	});

});	
	