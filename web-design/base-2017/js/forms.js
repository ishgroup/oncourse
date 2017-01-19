
function initTextileFormHandle(){
	$(document).on("click", "button.form-submit-button", function() {
		var form = $(this).parent("form")[0];
		var formId = form.id;
		//get redirect url from data-url attr
		var url = $(form).data("url");

		//next line responsible for formName send
		var formName = form.name;

		//prepare data for ajax request. Method serialize() should be run before valid() otherwise it doesn't work on IE
		var dataString = $(form).serialize() + "&formName=" + formName + "&pagePath="+window.location;

		if($(form).valid()) {
			//get sendMail html block to show waiting message for user.
			var sendingHtml = $('#sendingMail').html();
			$('#emailFormBlock_'+formId).html(sendingHtml);

			$.ajax({
				type: "POST",
				data: dataString,
				url: '/textile/textileform.send',
				complete: function(data) {
					if(url) {
						window.location=url;
					} else {
						$('#emailFormBlock_'+formId).html(data.responseText);
					}
				}
			});

		} else {
			$('#errorMessage_'+formId).html("Please, fill all the required fields");
		}
		return false;
	});
}

(function($) {
	$(document).ready(function() {
		initTextileFormHandle();
	});
})(jQuery);
