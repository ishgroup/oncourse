var $j = jQuery.noConflict();

function initSelectPayerHandle(){
	//close opened popup on any event
	$j('html').live('click dblclick', function(e){
		var $dropDown = $j('.drop-down');
		if ($dropDown.lenght > 0)
		{
			var W = $dropDown.width();
			var H = $dropDown.height();
			var X = $dropDown.offset().left;
			var Y = $j('.drop-down').offset().top;
			if ($j(".drop-down-content").hasClass('visible'))
			{
				var x = e.pageX;
				var y = e.pageY;

				if((x >= X && x <= X+W) &&
					(y >= Y && y <= Y+H))
				{

				}
				else
				{
					$j(".drop-down-content").removeClass("visible")
				}
			}
		}
	});

	$j('.drop-down').click(function()
	{
		var $p = $j('.drop-down').position();
		var $H = $j('.drop-down').height();


		$j(".drop-down-content").css('top',$p.top  + $H + 'px');
		$j(".drop-down-content").addClass("visible")
	});

	$j('.contact').click(function()
	{
		//set the value to tapestry component
		var $actionLink = $j(this).next('a.payerSelectLink')[0].pathname;
		sendAjax($actionLink);
	});
}


function initEnrolmentListHandle()
{
	$j('.enrolmentCheckbox').click(function()
	{
		var $actionLink = $j(this).next('a.selectEnrolmentLink')[0].pathname;
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
		sendAjax($actionLink);
	});

	$j('[id*=expiresOn]').datepicker();
}


function initCheckoutHandle()
{
	$j('#addContact').click(function()
	{
		var $actionLink = $j(this).next('a')[0].pathname ;
		sendAjax($actionLink);
	});
}


function initAddContactHandle()
{

	$j('#saveAddContact').click(function()
	{

		var f = $j("[id*=addContactForm]")[0];
		var data = $j(f).serialize();
		var actionLink = $j(this).next('a')[0].pathname;

		sendAjaxWithData(actionLink,data);
	});

	$j('#cancelAddContact').click(function()
	{
		var $actionLink = $j(this).next('a')[0].pathname ;
		sendAjax($actionLink);
	});

}


function initContactEditorHandle()
{
	$j('#saveEditContact').click(function()
	{
		var actionLink = $j(this).next('a')[0].pathname ;
		var f = $j("[id*=contactEditorForm]")[0];
		var data = $j(f).serialize();

		sendAjaxWithData(actionLink,data);
	});

	$j('#cancelEditContact').click(function()
	{
		var $actionLink = $j(this).next('a')[0].pathname ;
		sendAjax($actionLink);
	});

	$j('#dateOfBirth').datepicker();
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

function initProceedToPaymentHandle()
{
	$j("#proceedToPayment").click(function()
	{
		var actionLink = $j(this).next('a')[0].pathname ;
		sendAjax(actionLink);
	});

	$j("#paymentSubmit").click(function()
	{
		var actionLink = $j(this).next('a')[0].pathname ;
		var f = $j("[id*=paymentform]")[0];
		var data = $j(f).serialize();
		sendAjaxWithData(actionLink,data);
	});

	$j("#tryAgain").click(function()
	{
		var actionLink = $j(this).next('a')[0].pathname ;
		sendAjax(actionLink);
	});

	$j("#abandon").click(function()
	{
		var actionLink = $j(this).next('a')[0].pathname ;
		sendAjax(actionLink);
	});

	initHints($j("[id*=paymentform]").attr('id'));

}



function sendAjaxWithData($actionLink,$data)
{
	$j('#checkout').block({
		fadeIn: 700,
		fadeOut: 700,
		showOverlay: true
	});
	$j.ajax({
		type: "GET",
		url: $actionLink,
		data: $data,
		success: function(data) {
			$j('#checkout').unblock();
			$j('#checkout').html(data.content);
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
	initAddContactHandle();
	initContactEditorHandle();
	initAddCodeHandle();
	initCreditAccessHandle();
	initProceedToPaymentHandle();
}


$j(document).ready(function() {
	initHandles();

});
