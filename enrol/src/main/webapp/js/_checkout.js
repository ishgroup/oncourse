var $j = jQuery.noConflict();

function initSelectPayerHandle(){
    $j("select[id*=contact]").change(function()
        {
            //var $actionLink = $j(this).next('a')[0].pathname;
            var actionLink = '/enrol/payment.paymenteditor:changepayerevent';
            var f = $j("[id*=paymentform]")[0];
            var data = $j(f).serialize();
            sendAjaxWithData(actionLink, data);
        }
    );
}


function initEnrolmentListHandle()
{
	$j('.enrolmentSelect').click(function()
	{
        $j(document).tooltip("open");
		var $actionLink = $j(this).next('a.selectEnrolmentLink')[0].pathname;
		sendAjax($actionLink);
	});

    $j("[id*=discountedPrice]").tooltip({
                items: "[id*=discountedPrice]",
                content: function() {
                    var index = $j(this).data("index");
                    var element = $j("#discountToolTip"+"_"+index);
                    return element.html();
				},
				position: {
					my: "left top+5",
					at: "left bottom",
					collision: "flipfit"
				}
        }
    );
}



function initProductItemListHandle()
{
	$j('.productItemCheckbox').click(function()
	{
		var $actionLink = $j(this).next('a')[0].pathname;
		sendAjax($actionLink);
	});
}


function initAddConcessionHandle()
{
	$j('.add-concession').click(function()
	{
		var $actionLink = $j(this).next('a')[0].pathname;
		sendAjax($actionLink);
	});
}

function initConcessionEditorHandle()
{
	$j('#cancelConcession').click(function()
	{
		var $actionLink = $j(this).next('a')[0].pathname;
		sendAjax($actionLink);
	});

	$j('#saveConcession').click(function()
	{
		var f = $j("[id*=concessionForm]")[0];
		var data = $j(f).serialize();
		var actionLink = $j(this).next('a')[0].pathname;
		sendAjaxWithData(actionLink,data);
	});


	$j('#deleteConcession').click(function()
	{
		var $actionLink = $j(this).next('a')[0].pathname;
		sendAjax($actionLink);
	});

	$j('#concessionTypes').change(function()
	{
		var $value =  $j("#concessionTypes option:selected").attr("value");
		var $actionLink = $j(this).next('a')[0].pathname +'/'+$value;
		var $refreshId = $j(this).data('refreshid');

		sendAjaxWithDataAndRefreshId($actionLink, null, $refreshId, false);
		var $formId = $j(this).data('formid');
		if($formId)
		{
			location.reload();
		}
	});

	$j('[id*=expiresOn]').datepicker({
		changeMonth: true,
		changeYear: true,
		dateFormat: 'dd/mm/yy'
	});
}


function initCheckoutHandle()
{
	$j('#addContact').click(function()
	{
		var $actionLink = $j(this).next('a')[0].pathname ;
		sendAjax($actionLink);
	});
}


function initAddCodeHandle()
{
	$j('#addCode').click(function()
	{
		var actionLink = $j(this).next('a')[0].pathname ;
		var f = $j("[name=add_code]")[0];
		var data = $j(f).serialize();
		sendAjaxWithData(actionLink,data);
	});
}

function initCreditAccessHandle()
{
	$j('#creditAccessLink').click(function()
	{
		var $p = $j(this).position();
		var $H = $j(this).height();


		$j("#access-password-popup").css('left',$p.left + 'px');
		$j("#access-password-popup").css('top',$p.top  + $H + 'px');
		$j("#access-password-popup").css('display','block');
	});
	$j('#submitPassword').click(function()
	{
		$j("#access-password-popup").css('display','none');
	});

	$j('#submitPassword').click(function()
	{
		var actionLink = $j(this).next('a')[0].pathname ;
		var f = $j("[name=password]")[0];
		var data = $j(f).serialize();

		sendAjaxWithData(actionLink,data);
	});

	$j('#removeOwingLink').click(function()
	{
		var actionLink = $j(this).next('a')[0].pathname ;
		sendAjax(actionLink);
	});
}

function sendAjaxWithData($actionLink,$data)
{
	sendAjaxWithDataAndRefreshId($actionLink,$data,null,true);
}

function sendAjaxWithDataAndRefreshId($actionLink,$data,$refreshId,$async)
{
	if (!$refreshId)
		$refreshId = '#checkout';
	$j($refreshId).block({
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
        async: $async,
        success: function(data) {
            $j($refreshId).unblock();
			if (data.content)
            	$j($refreshId).html(data.content);
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
	initSelectPayerHandle();
	initEnrolmentListHandle();
	initAddConcessionHandle();
	initConcessionEditorHandle();
	initCheckoutHandle();
	initProductItemListHandle();
	initAddCodeHandle();
	initCreditAccessHandle();
}


$j(document).ready(function() {
	initHandles();
});
