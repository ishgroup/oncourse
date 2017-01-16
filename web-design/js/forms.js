var $j = jQuery.noConflict();

function initTextileFormHandle(){ 
	$j("button.form-submit-button").click(function(){ 
		var form = $j(this).parent("form")[0]; 
		var formId = form.id; 
		//get redirect url from data-url attr 
		var url = $j(form).data("url"); 
	
		//next line responsible for formName send 
		var formName = form.name; 
	
		//prepare data for ajax request. Method serialize() should be run before valid() otherwise it doesn't work on IE 
		var dataString = $j(form).serialize() + "&formName=" + formName + "&pagePath="+window.location;

		if ($j(form).valid()) 
		{ 
			//get sendMail html block to show waiting message for user. 
			var sendingHtml = $j('#sendingMail').html(); 
			$j('#emailFormBlock_'+formId).html(sendingHtml);

			$j.ajax({ 
				type: "POST", 
				data: dataString, 
				url: '/textile/textileform.send', 
				complete: function(data) { 
					if(url){ 
						window.location=url; 
					}else{ 
						$j('#emailFormBlock_'+formId).html(data.responseText); 
					} 
				} 
			}); 
		} 
		else 
		{ 
			$j('#errorMessage_'+formId).html("Please, fill all the required fields"); 
		} 
		return false; 
	}); 
}

$j(document).ready(function() {
    initTextileFormHandle();
});
