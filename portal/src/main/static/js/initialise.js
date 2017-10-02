goog.provide('initialise');

goog.require('custom');
goog.require('jquery.placeholder');


var $j = jQuery.noConflict();

function sendAjaxWithData(actionLink, formData, jdata, callback)
{
	var refreshId = '#loginZone';
	$j.ajax({
		type: "GET",
		url: actionLink,
		data: formData,
		async: true,
		//if the parameter is not set internet explorer loads content from cache
		cache: true,
		success: function(data) {
			if (data.content)
			{
				$j(refreshId).html(data.content);
				populate(jdata);

				if (callback)
					callback();
				initHandles();

			}
		},
		error:  function(data) {
            window.location.reload();
		}
	});
}

/**
 * The method fill data to this form
 * @param data
 */
function populate(data) {
	$j(data).each(function(){
		//it is workaround to avoid exception for t:formdata key
		if ( $j(this)[0].name != "t:formdata")
			$j("[id*=loginForm]").find("input[name=" + $j(this)[0].name + "]").val($j(this)[0].value);
	});
}

function initHints()
{
    /**
     * unbind all functions which were binded to inputs by other scripts
     */
    $j('div.form-group input').unbind("focus blur");
    $j('div.form-group input').unbind("focus blur");

    $j(".valid").mouseenter(function() {
        $j(this).find('.hint').removeClass('hidden-text');
    });

    $j(".valid").mouseleave(function() {
        $j(this).find('.hint').addClass('hidden-text');
    });

    $j(".form-group").mouseenter(function() {
        $j(this).find('.reason').removeClass('hidden-text');
    });

    $j(".form-group").mouseleave(function() {
        $j(this).find('.reason').addClass('hidden-text');
    });
}

function initLogOut() {

	$j("#logout").click(function() {
		document.cookie = "PORTAL_SESSION=; expires=Thu, 01 Jan 1970 00:00:00 UTC;";
		location.reload();
	});
}


function initHandles()
{
	initHints();
	initLogOut();
}

// placeholder polyfill for browsers that not support them
function initPlaceholder()
{
    $j('input, textarea').placeholder();
}

$j(document).ready(function() {
	initHandles();
	initPlaceholder();
});
