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

function reloadPageOnSessionTimeout(){
	 jQuery.ajaxSetup({async:false});
	 jQuery.post('/enrol/enrolcourses.checksession', function (data) {
		 if(data.status!='session alive'){
			 window.location.reload();
		 }else{
			 jQuery.ajaxSetup({async:true});
		 }
	 });
}
$j(document).ready(function() {

	jQuery(".enrolmentCheckbox").click(function() {  
		id=this.id;
		suffix=id.split("_")[1]+"_"+id.split("_")[2];
		isChecked=this.checked;
		
		jQuery.ajax({
			type: "POST",
			data: "data="+suffix+"_"+isChecked,
			url: '/enrol/enrolcourses.contactenrolmentlist.tick',
			complete:function(data){
				 if(data.responseText!='succeed'){
					 window.location.reload();
				 }
			}	
		});
		
		discountedPrice=getNumber(jQuery("#discountedPrice_"+suffix).text());
		discount=parseFloat(jQuery("#discount_"+suffix).text().replace(",",""));
		if(isChecked){
			if(jQuery("#discounttotal").length!=0){
				jQuery("#discounttotal").text(numberFormat(getNumber(jQuery("#discounttotal").text())+discount, "$"));
			}
			jQuery("#total").text(numberFormat(getNumber(jQuery("#total").text())+discountedPrice, "$"));
			jQuery("#total").animate({
				opacity: 0.2
			  }, 500, function() {
				jQuery("#total").css('opacity','1');
			  });
			jQuery("#cardtotalstring").text(numberFormat(getNumber(jQuery("#cardtotalstring").text())+discountedPrice, "$"));
		}else{
			if(jQuery("#discounttotal").length!=0){
				jQuery("#discounttotal").text(numberFormat(getNumber(jQuery("#discounttotal").text())-discount, "$"));
			}
			jQuery("#total").text(numberFormat(getNumber(jQuery("#total").text())-discountedPrice, "$"));
			jQuery("#total").animate({
				opacity: 0.2
			}, 500, function() {
				jQuery("#total").css('opacity', '1');
			});
			jQuery("#cardtotalstring").text(numberFormat(getNumber(jQuery("#cardtotalstring").text())-discountedPrice, "$"));
		}
		if(!isChecked){
			checkboxes=jQuery(".enrolmentCheckbox");
			isAnySelected=false;
			if(jQuery(".enrolmentsSelected").is(":visible")){
				for(i=0; i<checkboxes.length;i++){
					if(checkboxes[i].checked){
						isAnySelected=true;
						break;
					}
				}
				if(!isAnySelected){
					jQuery(".enrolmentsSelected").hide();
				}
			}
		}else{
			if(!jQuery(".enrolmentsSelected").is(":visible")){
				jQuery(".enrolmentsSelected").show();
			}
		}
		
	});
});
 
function getNumber(formatted){
	return parseFloat(formatted.substring(1).replace(",",""));
}
			
function numberFormat(nStr,prefix){
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
