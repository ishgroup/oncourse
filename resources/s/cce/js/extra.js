/*$j(document).ready(function($) {


	
	$j(".shortlistActionShow a").live("click", function() {
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
	});
	
	$j(".closebutton").live("click", function() {
		$j(this).parents(".dialogContainer").fadeOut("fast");
	});
	
	$j("#frontpage_slider").cycle('fade');
	
	
	
});
*/

//var $j = jQuery.noConflict();

jQuery(document).ready(function(){

	//expandable subject heirarchy
	jQuery( "#sidebarLeft .courses-list-sub li.parent_tag, #sidebarLeft .courses-list-sub li.active_tag" ).children('ul').show();

	
	// Add a toggler span
	//$j( "#sidebarLeft .courses-list-sub .hasChildren > h2" ).append("<figure class=\"expander\"></figure>");
	jQuery( "#sidebarLeft .courses-list-sub .hasChildren:has(ul)" ).append("<figure class=\"expander ico-expand\"></figure>");
	
	jQuery( "#sidebarLeft .courses-list-sub .hasChildren .expander" ).bind( "click", function expandoList() {	
		jQuery(this).parent().children('ul').slideToggle("fast");
		return false; //stop normal href from working
	});

});    

