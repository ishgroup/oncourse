jQuery(document).ready(function(){
	var ils = jQuery("#classes_ul li");
	var ils_attendance = jQuery("#classes_ul_for_attendance li");
	var i   = 0;
	var j   = 0;
	
    // Next click
	jQuery("#nextButton").click(function(){
		
		for(i=0; i<=ils.length; i++)
		{
			if(jQuery(ils[i]).is(":visible"))
			{
				j = i+1;
				break;
			}	
		}
		if(j === 0) 
		{
			jQuery("#prevButton").hide();
			jQuery(ils[j]).show();
			jQuery(ils_attendance[j]).show();
			initAttendanceButtons(j);
		} else {
			jQuery("#prevButton").show();
		}
		if(j === (ils.length - 1))
		{
			jQuery("#nextButton").hide();
		}
		
		jQuery(ils[i]).hide("slide",{direction: 'right'},500,function(){  
			jQuery(ils[j]).show("slide",{},500);});
			
		jQuery(ils_attendance[i]).hide("slide",{direction: 'right'},500,function(){
			jQuery(ils_attendance[j]).show("slide",{},500);
		});
		
		
	});
	
	// Previous click
	jQuery("#prevButton").click(function(){
		jQuery("#nextButton").show();
		for(i=0; i<=ils.length; i++)
		{
			if(jQuery(ils[i]).is(":visible"))
			{
				j = i-1;
				break;
			}	
		}
		if(j === 0) 
		{
			jQuery("#prevButton").hide();
		}
		
		jQuery(ils[i]).hide("slide",{},500,function(){  
			jQuery(ils[j]).show("slide",{direction: 'right'},500);});
			
		jQuery(ils_attendance[i]).hide("slide",{},500,function(){
			jQuery(ils_attendance[j]).show("slide",{direction: 'right'},500);
		});
	});
	
	jQuery(".attended").click(function(){
		var id = jQuery(this).attr('id');
		var attendedList = jQuery('.attended', ils_attendance[j]);
		var absentList = jQuery('.absent', ils_attendance[j]);
		for(t=0; t<=attendedList.length; t++)
		{
			if(jQuery(attendedList[t]).attr('id')===id){
				
				
				jQuery.post("/portal/tutor/classroll.attendance", { attendanceId: id.split('_')[1], action: id.split('_')[0] }).complete(function(data) { 
					var response = data.responseText;
					if(response === "SUCCESS"){
						checkAttendanceButtons(absentList[t], attendedList[t]);
					} 
				}) ;
				
				break;
			}
		}
	});
	
	jQuery(".absent").click(function(){
		var id = jQuery(this).attr('id');
		var attendedList = jQuery('.attended', ils_attendance[j]);
		var absentList = jQuery('.absent', ils_attendance[j]);
		for(t=0; t<=absentList.length; t++)
		{
			if(jQuery(absentList[t]).attr('id')===id){
				
				jQuery.post("/portal/tutor/classroll.attendance", { attendanceId: id.split('_')[1], action: id.split('_')[0] }, function(data) {},
						"text/json").complete(function(data) { 
					var response = data.responseText;
					if(response === "SUCCESS"){
						checkAbsentButtons(absentList[t], attendedList[t]);
					} 
				}) ;
	
				break;
			}
		}
	});
	
	function checkAbsentButtons(absent, attended){
		if(jQuery(absent).attr('value')==="true")
		{
			jQuery(absent).attr('value', "false");
			jQuery(absent).removeClass('act');
			
		} else {
			jQuery(absent).attr('value', "true");
			jQuery(absent).addClass('act');
			if(jQuery(attended).attr('value')==="true"){
				jQuery(attended).attr('value', "false");
				jQuery(attended).addClass('mark-y').removeClass('act');
			}
		}	
	}
	
	function checkAttendanceButtons(absent, attended){
		if(jQuery(attended).attr('value')==="true")
		{
			jQuery(attended).attr('value', "false");
			jQuery(attended).addClass('mark-y').removeClass('act');
			
		} else {
			jQuery(attended).attr('value', "true");
			jQuery(attended).addClass('act').removeClass('mark-y');
			if(jQuery(absent).attr('value')==="true"){
				jQuery(absent).attr('value', "false");
				jQuery(absent).removeClass('act');
			}
		}	
	}
	
	function initAttendanceButtons(j) {
		var attendedList = jQuery('.attended', ils_attendance[j]);
		for(t=0; t<=attendedList.length; t++)
		{
			if(jQuery(attendedList[t]).attr('value')==="true")
			{
				jQuery(attendedList[t]).addClass('act').removeClass('mark-y');
			} else {
				jQuery(attendedList[t]).addClass('mark-y').removeClass('act');
			}	
		}
		var absentList = jQuery('.absent', ils_attendance[j]);
		for(t=0; t<=absentList.length; t++)
		{
			if(jQuery(absentList[t]).attr('value')==="true")
			{
				jQuery(absentList[t]).addClass('act');
			} else {
				jQuery(absentList[t]).removeClass('act');
			}	
		}
	}
	
	jQuery(ils).hide();
	jQuery(ils_attendance).hide();
	jQuery("#nextButton").trigger('click');
});