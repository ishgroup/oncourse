goog.provide('checkout');

goog.require('common');

var $j = jQuery.noConflict();

function initSelectPayerHandle(){

    $j('.payer-selection a.button').click(function(){
        $j(this).next('ul').toggle();
        return false;
    });

    $j('.payer-selection ul.new-payer-option  li a').click(function(){
        $j('ul.new-payer-option').hide();
//        var actionLink = '/enrol/payment.paymenteditor:changepayerevent';
//        var f = $j("[id*=paymentform]")[0];
//        var data = $j(f).serialize();
//        sendAjaxWithData(actionLink, data);
        return false;
    });
}


function initEnrolmentListHandle()
{
	$j('.enrolmentSelect').click(function()
	{
		var actionLink;
		if ($j.browser.msie)
			//workaround for internet explorer
			actionLink = $j(this).next().next().attr('href');
		else
			actionLink = $j(this).next('a.selectEnrolmentLink').attr('href');
		sendAjax(actionLink);
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
        var data = $j(this).parents('tr').find('.priceValue').serialize();
        var actionLink = $j(this).next('a').attr('href');
        sendAjaxWithData(actionLink, data);
	});
}


function initAddConcessionHandle()
{
	$j('.add-concession').click(function()
	{
		var actionLink = $j(this).next('a').attr('href');
		sendAjax(actionLink);
	});
}

function initAddGuardianHandle()
{
    $j('.add-Guardian').click(function()
    {
        var actionLink = $j(this).next('a').attr('href');
        sendAjax(actionLink);
    });
}


function initConcessionEditorHandle()
{
	$j('#cancelConcession').click(function()
	{
		var actionLink = $j(this).next('a').attr('href');
		sendAjax(actionLink);
	});

	$j('#saveConcession').click(function()
	{
		var f = $j("[id*=concessionForm]")[0];
		var data = $j(f).serialize();
		var actionLink = $j(this).next('a').attr('href');
		sendAjaxWithData(actionLink,data);
	});


	$j('#deleteConcession').click(function()
	{
		var actionLink = $j(this).next('a').attr('href');
		sendAjax(actionLink);
	});

	$j('#concessionTypes').change(function()
	{
		var value =  $j("#concessionTypes option:selected").attr("value");
		var actionLink = $j(this).next('a').attr('href') +'/'+value;
		var refreshId = $j(this).data('refreshid');

		sendAjaxWithDataAndRefreshId(actionLink, null, refreshId, false);
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
		var actionLink = $j(this).next('a').attr('href') ;
		sendAjax(actionLink);
	});
}


function initAddCodeHandle()
{
	$j('#addCode').click(function()
	{
		var actionLink = $j(this).next('a').attr('href') ;
		var f = $j("[name=add_code]")[0];
		var data = $j(f).serialize();
		sendAjaxWithData(actionLink,data);
	});
}

function initRedeemedVoucherHandle()
{
    $j('.redeemedVoucherCheckbox').click(function()
    {
        var data = $j(this).serialize();
        var actionLink = $j(this).next('a').attr('href');
        sendAjaxWithData(actionLink,data);
    });
}


function initCreditAccessHandle()
{
	$j('#creditAccessLink').click(function()
	{
		var p = $j(this).position();
		var H = $j(this).height();


		$j("#access-password-popup").css('left',p.left + 'px');
		$j("#access-password-popup").css('top',p.top  + H + 'px');
		$j("#access-password-popup").css('display','block');
	});
	$j('#submitPassword').click(function()
	{
		$j("#access-password-popup").css('display','none');
	});

	$j('#submitPassword').click(function()
	{
		var actionLink = $j(this).next('a').attr('href') ;
		var f = $j("[name=password]")[0];
		var data = $j(f).serialize();

		sendAjaxWithData(actionLink,data);
	});

	$j('#removeOwingLink').click(function()
	{
		var actionLink = $j(this).next('a').attr('href') ;
		sendAjax(actionLink);
	});
}

function initCorporatePassHandle()
{
	$j("#addCorporatePass").click(function()
	{
		var actionLink = $j(this).next('a').attr('href');
		var f = $j("[name=corporatePass]")[0];
		var data = $j(f).serialize();
		sendAjaxWithData(actionLink, data);
	});
}

function initPaymentEditorsHandle()
{
    $j("a[href^='#credit-card']").click(function()
    {
        var actionLink = "/enrol/payment:selectcardeditor";
        sendAjax(actionLink);
    });

    $j("a[href^='#corporate-pass']").click(function()
    {
        var actionLink = "/enrol/payment:selectcorporatepasseditor";
        sendAjax(actionLink);
    });
}

function sendAjaxWithData(actionLink,data)
{
	sendAjaxWithDataAndRefreshId(actionLink,data,null,true);
}

function sendAjaxWithDataAndRefreshId(actionLink,data,refreshId,async)
{
	if (!refreshId)
		refreshId = '#checkout';
	$j(refreshId).block({
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
        url: actionLink,
        data: data,
		async: async,
		//if the parameter is not set internet explorer loads content from cache
		cache: false,
        success: function(data) {
            $j(refreshId).unblock();
			if (data.content)
            {
                $j(refreshId).html(data.content);
                initHandles();
            }
        },
		error:  function(data) {
			$j(refreshId).unblock();
		}
    });
}

function sendAjax(actionLink)
{
    sendAjaxWithData(actionLink,null);
}

function initHandles()
{
	initSelectPayerHandle();
	initEnrolmentListHandle();
	initAddConcessionHandle();
    initAddGuardianHandle();
	initConcessionEditorHandle();
	initCheckoutHandle();
	initProductItemListHandle();
	initAddCodeHandle();
	initCreditAccessHandle();
	initPaymentEditorsHandle();
	initCorporatePassHandle();
    initRedeemedVoucherHandle();
}


$j(document).ready(function() {
	initHandles();
});
