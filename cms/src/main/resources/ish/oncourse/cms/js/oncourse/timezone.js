function updateClientTimezone(){ 
	var offset = new Date().getTimezoneOffset();
	var tz = jstz.determine();
	//tz.name();
		$j.ajax({
			type: "POST",
			data: "offset="+(-offset)+"&timezoneName="+tz.name(),
			url: '/ui//timezoneHolder.setupOffset',
			complete: function(data){
				if(data.responseText=="{status: 'OK'}"){
					//possible page reload?
				} else{
					//alert("Unexpected behaviour.");
				}
			}, 
			async:true
		});
		return false;
}

$j(document).ready(function() {
    updateClientTimezone();
});