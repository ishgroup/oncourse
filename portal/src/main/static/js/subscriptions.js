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

$j(document).ready(function() {
	initSubscriptions();
});
