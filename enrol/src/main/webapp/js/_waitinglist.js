var $j = jQuery.noConflict();

function initHandles()
{
	initAddWaitingListHandle();
	initHints($j("[id*=waitingListForm]").attr('id'));
}


$j(document).ready(function() {
    initHandles();
});
