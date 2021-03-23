import jQuery, * as $ from "./jquery-1.11.2";
import "./initialise";

var $j = jQuery.noConflict();

function initSaveMalingLists() {

	$j("input[name*='mailingList_']").click(function(){
		$j("#mailingLists-saved").hide()
	});

	$j('#saveMailingLists').click(function(event) {
		event.preventDefault();
		var actionLink = "/portal/subscriptions.mailingLists:saveMailingListForm";
		var f = $j("#mailingListForm");
		var data = $j(f).serialize();
		$j.ajax({
			type: "GET",
			url: actionLink,
			data: data,
			async: false,
			cache: true,
			success: function() {
				$j("#mailingLists-saved").removeClass('hidden');
				$j("#mailingLists-saved").show();
			},
			error:  function(data) {
				window.location.reload();
			}
		});
	});
}

$j(document).ready(function() {
	initSaveMalingLists();
});
