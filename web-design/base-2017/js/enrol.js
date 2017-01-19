/**
*  enrol.js
*
*  Enrolment engine functionality.
*
*
*/

var url = window.location.href;
var host = window.location.host;
if(url.indexOf('http://' + host + '/enrol') != -1) {
	document.observe(Tapestry.FORM_VALIDATE_FIELDS_EVENT, function(event) {reloadPageOnSessionTimeout();});
}
if(url.indexOf('https://' + host + '/enrol') != -1) {
	document.observe(Tapestry.FORM_VALIDATE_FIELDS_EVENT, function(event) {reloadPageOnSessionTimeout();});
}

function reloadPageOnSessionTimeout() {
	jQuery.ajaxSetup({async:false});
	jQuery.post('/enrol/enrolcourses.checksession', function (data) {
		if(data.status!='session alive') {
			window.location.reload();
		} else {
			jQuery.ajaxSetup({async:true});
		}
	});
}

(function($) {
	$(document).ready(function() {
		$(document).on("click", ".enrolmentCheckbox", function() {
			id = this.id;
			suffix = id.split("_")[1]+"_"+id.split("_")[2];
			isChecked = this.checked;

			$.ajax({
				type: "POST",
				data: "data="+suffix+"_"+isChecked,
				url: '/enrol/enrolcourses.contactenrolmentlist.tick',
				complete:function(data) {
					if(data.responseText != 'succeed') {
						window.location.reload();
					}
				}
			});

			discountedPrice = getNumber($("#discountedPrice_"+suffix).text());
			discount = parseFloat($("#discount_"+suffix).text().replace(",",""));
			var _discounttotal = 0, _total = 0, _cardtotalstring = 0;

			if(isChecked) {
				_discounttotal = getNumber($("#discounttotal").text())+discount;
				_total = getNumber($("#total").text())+discountedPrice;
				_cardtotalstring = getNumber($("#cardtotalstring").text())+discountedPrice;

			} else {
				_discounttotal = getNumber($("#discounttotal").text())-discount;
				_total = getNumber($("#total").text())-discountedPrice;
				_cardtotalstring = getNumber($("#cardtotalstring").text())-discountedPrice;
			}

			if($("#discounttotal").length != 0) {
				$("#discounttotal").text(numberFormat(_discounttotal, "$"));
			}

			$("#total").text(numberFormat(_total, "$"));

			$("#total").animate({
				opacity: 0.2
			}, 500, function() {
				$("#total").css('opacity','1');
			});

			$("#cardtotalstring").text(numberFormat(_cardtotalstring, "$"));

			if(!isChecked) {
				checkboxes = $(".enrolmentCheckbox");
				isAnySelected = false;

				if($(".enrolmentsSelected").is(":visible")) {

					for(i=0; i<checkboxes.length;i++) {
						if(checkboxes[i].checked) {
							isAnySelected = true;
							break;
						}
					}

					if(!isAnySelected) {
						$(".enrolmentsSelected").hide();
					}
				}

			} else {
				if(!$(".enrolmentsSelected").is(":visible")) {
					$(".enrolmentsSelected").show();
				}
			}

		});
	});
})(jQuery);

function getNumber(formatted) {
	return parseFloat(formatted.substring(1).replace(",",""));
}

function numberFormat(nStr,prefix) {
	//rounding
	nStr = Math.round(1000*nStr)/1000;
	var value = nStr.toString().split('.');
	var cent  = value.length==1 ? "" : (value[1].length == 1 ? value[1]+'0' : value[1].substring(0,2));
	nStr = value[0] + (cent.length == 0 ? "" : ('.' + cent));
	var prefix = prefix || '';
	nStr += '';
	x = nStr.split('.');
	x1 = x[0];
	x2 = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	while (rgx.test(x1))
		x1 = x1.replace(rgx, '$1' + ',' + '$2');
	return prefix + x1 + x2;
}
