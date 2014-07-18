goog.provide('attendances');

goog.require('initialise');



var $j = jQuery.noConflict();

function init()
{
		$j(".session-list > li .mark-roll").click(function()
		{
			var actionLink = "/portal/class.classdetails:onsetsession/"+event.target.id;
			$j.ajax({
				
				url: actionLink,
				async: false,
				cache: false,
				success:function(data){
				show()
				}
			});
		});

		$j('.finish').click(function(){
			hide(); 
		});
}


function fillAttendences(data){




		$j.each(data, function(key, value) {
			if(key=='session'){
				$j.each(value, function(key1, value1){
					$j.each(value1, function(key2, value2){
                                        	if(key2=='startDate'){
							$j('#sessionDate').html(value2)
                                        	}
					});
				});

                        }

		});


		$j('.attendance-button-field').each(function(index, element)
		{     
	        
                if($j(element).children('.btn-ok').hasClass('btn-primary active')){
                   $j(element).children('.btn-ok').removeClass('btn-primary active');
                   $j(element).children('.btn-ok').addClass('btn-default'); 
		}
		else if($j(element).children('.attendance').children('.btn-cancel').hasClass('btn-danger active')){
                        $j(element).children('.attendance').children('.btn-cancel').removeClass('btn-danger active');
			$j(element).children('.attendance').children('.btn-cancel').addClass('btn-default')
                      
		}
		});





		$j.each(data, function(key, value) {
            
            $j('#'+key).attr({"value":value});
            
			buttonOk = $j('#'+key).children('.attendance').children('.btn-ok');
			buttonCancel = $j('#'+key).children('.attendance').children('.btn-cancel');
		
			if (value == 1)
			{
				buttonOk.removeClass('btn-default');
				buttonOk.addClass('btn-primary active');
			} 
			else if (value > 1)
			{
				buttonCancel.removeClass('btn-default');
				buttonCancel.addClass('btn-danger active');
			}
		});
}



function hide() 
{


	data = new Object;
	$j('.attendance-button-field').each(function(index, element)
	{     
	        var id=element.id;
        
                if($j(element).children('.attendance').children('.btn-ok').hasClass('btn-primary active')){
                 
                  data[id]=1    
		        }
		        else if($j(element).children('.attendance').children('.btn-cancel').hasClass('btn-danger active')){

                    if($j(element).attr("value") == 1 || $j(element).attr("value") == 0) {
                        data[id]=3
                    } else {
                        data[id]=$j(element).attr("value")
                    }
                       
		        }
		        else {
                  data[id]=0  
                }
                 
		
	});



	$j(".session-wrapper, .course-detail").slideDown();		
	$j('.class-roll-content .info-text').hide();
	$j(".session-list > li").removeClass('active');
	$j(".session-info").show();
	//$j(".past-sessions").show();
	$j('.attendance-content').hide('slow');

	var el = $j('.attendance');
	var index = 0;
	el.removeClass('act');

	

	$j.ajax({
		type: "POST",
		url: '/portal/class.classdetails:setAttendences',
		data: data,
		dataType: "json",
		async: false,
		cache: false
	});

	return false;
}


function show()
{
	$j(".session-wrapper, .course-detail").slideUp();
	$j(".class-roll-content .info-text").show();
	//$j(".past-sessions").hide();
	$j(".session-info").hide();
	$j('.attendance-content').fadeIn('slow');

	var el = $j('.attendance');
	el.addClass('act');

	$j.ajax({
		cache: false,
		async: false,
		dataType: "json",
		url: '/portal/class.classdetails:getAttendences',
		type: 'GET',
		success: function(data) 
		{
			fillAttendences(data);
		}
		
	});

	return false;
}



$j(document).ready(function() {
	init();
});
