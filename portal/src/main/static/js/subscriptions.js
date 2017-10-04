goog.provide('subscriptions');

goog.require('initialise');

var $j = jQuery.noConflict();

function initSubscriptions()
{
		$j('[id*=tab-]').click(function()
		{
			var actionLink = "/portal/subscriptions:setactivetab/"+$j(this).attr('id');
			$j.ajax({	
				url: actionLink,
				async: false,
				cache: false,
				success:function(data){
				},
                error: function (jqXHR, textStatus,errorThrown) {
                    window.location.reload();
                }
			});
		});
}

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
	initSubscriptions();
});
