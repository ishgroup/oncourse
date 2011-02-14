$j(document).ready(function($) {


	/* Shortlist */
	/*$j(".shortlistActionShow a").live("click", function() {
			//alert("!!!");
			$j("#shortlist .shortlistChoices").fadeToggle("fast");
			return false;
		});	
	
	$j("#shortlist .deleteItem a").live("click", function() {
		// This is accessing the custom data attribute set in Shortlist.tml - See http://html5doctor.com/html5-custom-data-attributes/
		var itemId = $(this).parent().parent().data('classid');
		
		//$j(".shortlistChoices li[data-classid=" + itemId + "]").css("outline", "1px solid red");
		
		$j.ajax({
			type: "GET",
//			url:  '/removeFromCookies?key=shortlist&itemId='+ itemId,
			url:  '/ShortList?removeClass='+ itemId,
			success: function(data){
				$j('#shortlist').replaceWith(data);
//				refreshShortList(itemId);
				$j('.classItem[data-classid=' + itemId + '] .enrolAction').text("Enrol Now");
			}
		});	
	});*/
	
	$j(".closebutton").live("click", function() {
		$j(this).parents(".dialogContainer").fadeOut("fast");
	});
	
	$j("#frontpage_slider").cycle('fade');
	
});    

