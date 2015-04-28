function updateClientTimezone(){ 
	var offset = new Date().getTimezoneOffset();
	var tz = jstz.determine();
		$j.ajax({
			type: "POST",
			data: "offset="+(-offset)+"&timezoneName="+tz.name(),
			url: '/ui/timezoneHolder.setupOffset',
			complete: function(data){
			},
			async:true
		});
		return false;
}

$j(document).ready(function() {
    updateClientTimezone();
});