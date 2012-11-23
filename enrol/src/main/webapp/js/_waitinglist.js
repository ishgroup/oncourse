var $j = jQuery.noConflict();

function initAddWaitingListHandle()
{

	$j('#addWaitingList').click(function()
	{

		var f = $j("[id*=waitingListForm]")[0];
		var data = $j(f).serialize();
		var actionLink = $j(this).next('a')[0].pathname;

		sendAjaxWithData(actionLink,data);
	});

	$j('.dateOfBirth').datepicker();


	if ($j(".suburb")) {
		$j(".suburb").autocomplete({source: '/ui/internal/autocomplete.sub', minLength: 3,
			select: function(event, ui) {
				var value = ui.item.value;
				var suburb = suburbFromString(value);
				var postcode = postcodeFromString(value); // otherwise it thinks it's a number
				var state = postcode.length==0 ? '' : stateFromPostcode(postcode);
				$j(this).attr("value",suburb);
				$j(".postcode").attr("value",postcode);
				$j(".state").attr("value",state);

			},
			close: function(event, ui) {
				var value = $j(this).attr("value");
				var suburb = suburbFromString(value);
				$j(this).attr("value",suburb);
			}
		});
	}
}


function sendAjaxWithData($actionLink,$data)
{
	$j('#waitingListBlock').block({
		fadeIn: 700,
		fadeOut: 700,
		showOverlay: true
	});
	$j.ajax({
		type: "GET",
		url: $actionLink,
		data: $data,
		success: function(data) {
			$j('#waitingListBlock').unblock();
			$j('#waitingListBlock').html(data.content);
			initHandles();
		}
	});
}

function sendAjax($actionLink)
{
	sendAjaxWithData($actionLink,null);
}


function initHandles()
{
	initAddWaitingListHandle();
	initHints($j("[id*=waitingListForm]").attr('id'));
}


$j(document).ready(function() {
	initHandles();
});
