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
}


function sendAjaxWithData($actionLink,$data)
{
	$j('#waitingListBlock').block({
		fadeIn: 700,
		fadeOut: 700,
		showOverlay: true,
		message:null,
		overlayCSS:  {
			opacity:         0,
			cursor:          'wait'
		}
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
