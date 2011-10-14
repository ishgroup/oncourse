//# jQuery - Horizontal Accordion
//# Version 2.00.00 Alpha 1
//#
//# portalZINE(R) - New Media Network
//# http://www.portalzine.de
//#
//# Alexander Graef
//# portalzine@gmail.com
//#
//# Copyright 2007-2009

(function($) {
	$.hrzAccordion = {
       
	   
	   setOnEvent: function(i, container, finalWidth, settings){
			$("#"+container+"Handle"+i).bind(settings.eventTrigger,function() {			 
			   			
						var status = $('[rel='+container+'ContainerSelected]').data('status');
						
						if(status ==1 && settings.eventWaitForAnim === true){
						 return false;	
						}
						
						if( $("#"+container+"Handle"+i).attr("rel") != container+"HandleSelected"){
			    		
						  settings.eventAction;
							
							$('[id*='+container+'Handle]').attr("rel","");			   				
			   				
							$('[id*='+container+'Handle]').attr("class",settings.handleClass);
		
			   				$("#"+container+"Handle"+i).addClass(settings.handleClassSelected);
										   
			   		
							$("."+settings.contentWrapper).css({width: finalWidth+"px" });
							
							switch(settings.closeOpenAnimation)
							{
							case 1:
      
				   
						if($('[rel='+container+'ContainerSelected]').get(0)  ){
						$('[rel='+container+'ContainerSelected]').data('status',1);
							
							//current_width = $('[rel='+container+'ContainerSelected]').width();
							
							$('[rel='+container+'ContainerSelected]').animate({width: "0px",opacity:"0"}, { queue:true, duration:settings.closeSpeed ,easing:settings.closeEaseAction,complete: function(){	
				 																																	
							$('[rel='+container+'ContainerSelected]').data('status',0);																																											} ,step: function(now){
																																																		 							width = $(this).width();
						
							//new_width = finalWidth- (finalWidth  * (width/current_width));
							new_width = finalWidth - width;
							$('#'+container+'Content'+i).width(Math.ceil(new_width)).css("opacity","1");
							
							}});
		
						}else{
							$('[rel='+container+'ContainerSelected]').data('status',1);
								
							$('#'+container+'Content'+i).animate({width: finalWidth,opacity:"1"}, { queue:false, duration:settings.closeSpeed ,easing:settings.closeEaseAction,complete: function(){
																																																	  $('[rel='+container+'ContainerSelected]').data('status',0);	
																																																	  }});
							
								
							}
							
							break;
							case 2:
								$('[id*='+container+'Content]').css({width: "0px"});
								$('#'+container+'Content'+i).animate({width: finalWidth+"px",opacity:"1"}, { queue:false, duration:settings.openSpeed ,easing:settings.openEaseAction, complete: 
 settings.completeAction																																																									});						
							
							break;
							}

							$('[id*='+container+'Content]').attr("rel","");			
							$("#"+container+"Handle"+i).attr("rel",container+"HandleSelected");
							$("#"+container+"Content"+i).attr("rel",container+"ContainerSelected");					
							
						
						}
						
					});	
}
	    };
	
	$.fn.extend({
	   
		hrzAccordionLoop: function(options) {
			return this.each(function(a){  
				
				var container = $(this).attr("id") || $(this).attr("class");
				var elementCount = $('#'+container+' > li, .'+container+' > li').size();
				var settings = $(this).data('settings');
				
				variable_holder="interval"+container ;
				var i =0;
				var loopStatus  = "start";
				
				variable_holder = window.setInterval(function(){							
				
				$("#"+container+"Handle"+i).trigger(settings.eventTrigger);
				
				if(loopStatus =="start"){
						i = i + 1;
					}else{
						i = i-1;	
					}
					
					if(i==elementCount && loopStatus  == "start"){
						loopStatus  = "end";
						i=elementCount-1;

					}
					
					if(i==0 && loopStatus  == "end"){
						loopStatus  = "start";
						i=0;

					}
												},settings.cycleInterval);
				
				
				});
			},
		hrzAccordion: function(options) {
			this.settings = {
			eventTrigger	   		: "click",
			containerClass     		: "container",
			listItemClass      		: "listItem",					
			contentContainerClass  	: "contentContainer",
			contentWrapper     		: "contentWrapper",
			contentInnerWrapper		: "contentInnerWrapper",
			handleClass        		: "handle",
			handleClassOver    		: "handleOver",
			handleClassSelected		: "handleSelected",
			handlePosition     		: "right",
			handlePositionArray		: "", // left,left,right,right,right
			closeEaseAction    		: "swing",
			closeSpeed     			: 500,
			openEaseAction     		: "swing",
			openSpeed      			: 500,
			openOnLoad		   		: 2,
			hashPrefix		   		: "tab",
			eventAction		   		: function(){
								 	//add your own extra clickAction function here
								 	},
			completeAction	   		: function(){
								 	//add your own onComplete function here
								 	},
			closeOpenAnimation 		: 1,// 1 - open and close at the same time / 2- close all and than open next
			cycle			   		: false, // not integrated yet, will allow to cycle through tabs by interval
			cycleInterval	   		: 10000,
			fixedWidth				: "",
			eventWaitForAnim		: true
				
		};
	
		if(options){
			$.extend(this.settings, options);
		}
			var settings = this.settings;
			
			
			
			return this.each(function(a){    		
				
				var container = $(this).attr("id") || $(this).attr("class");			
				
				$(this).data('settings', settings);
				
				$(this).wrap("<div class='"+settings.containerClass+"'></div>");
			
				var elementCount = $('#'+container+' > li, .'+container+' > li').size();
												
				var containerWidth =  $("."+settings.containerClass).width();
				
				var handleWidth = $("."+settings.handleClass).css("width");
		
				handleWidth =  handleWidth.replace(/px/,"");
			    var finalWidth;
				var handle;
				
				if(settings.fixedWidth){
					finalWidth = settings.fixedWidth;
				}else{
					finalWidth = containerWidth-(elementCount*handleWidth)-handleWidth;
				}
				
				$('#'+container+' > li, .'+container+' > li').each(function(i) {
			
					$(this).attr('id', container+"ListItem"+i);
			   		$(this).attr('class',settings.listItemClass);
		       		$(this).html("<div class='"+settings.contentContainerClass+"' id='"+container+"Content"+i+"'>"
								 +"<div class=\""+settings.contentWrapper+"\">"
								 +"<div class=\""+settings.contentInnerWrapper+"\">"
								 +$(this).html()
								 +"</div></div></div>");
			   		
					if($("div",this).hasClass(settings.handleClass)){
					
					var html = $("div."+settings.handleClass,this).attr("id",""+container+"Handle"+i+"").html();
					$("div."+settings.handleClass,this).remove();
					
					 handle = "<div class=\""+settings.handleClass+"\" id='"+container+"Handle"+i+"'>"+html+"</div>";
					}else{
					 handle = "<div class=\""+settings.handleClass+"\" id='"+container+"Handle"+i+"'></div>";
					}
					
				
					
					if(settings.handlePositionArray){
						splitthis 				= settings.handlePositionArray.split(",");
						settings.handlePosition = splitthis[i];
					}
					
					switch(settings.handlePosition ){
						case "left":
						$(this).prepend( handle );
						break;
						case "right":	
						$(this).append( handle );	
						break;
						case "top":	
						$("."+container+"Top").append( handle );	
						break;
						case "bottom":	
						$("."+container+"Bottom").append( handle );	
						break;
					}					
				
					$("#"+container+"Handle"+i).bind("mouseover", function(){
						$("#"+container+"Handle"+i).addClass(settings.handleClassOver);
					});
			    
					$("#"+container+"Handle"+i).bind("mouseout", function(){
						if( $("#"+container+"Handle"+i).attr("rel") != "selected"){
							$("#"+container+"Handle"+i).removeClass(settings.handleClassOver);
						}
					});
					
				
					$.hrzAccordion.setOnEvent(i, container, finalWidth, settings);				
					
					if(i == elementCount-1){
						$('#'+container+",."+container).show();					
					}
					
					
								
					if(settings.openOnLoad !== false && i == elementCount-1){
							var location_hash = location.hash;
							location_hash  = location_hash.replace("#", "");	
							if(location_hash.search(settings.hashPrefix) != '-1' ){
							var tab = 1;
							location_hash  = location_hash.replace(settings.hashPrefix, "");
							}
							
							if(location_hash && tab ==1){
						 		$("#"+container+"Handle"+(location_hash)).attr("rel",container+"HandleSelected");
								$("#"+container+"Content"+(location_hash)).attr("rel",container+"ContainerSelected");		
								$("#"+container+"Handle"+(location_hash-1)).trigger(settings.eventTrigger);
												
							}else{
								$("#"+container+"Handle"+(settings.openOnLoad)).attr("rel",container+"HandleSelected");
							    $("#"+container+"Content"+(settings.openOnLoad)).attr("rel",container+"ContainerSelected");	
								$("#"+container+"Handle"+(settings.openOnLoad-1)).trigger(settings.eventTrigger);
							}					
					}	
				});	
				
				if(settings.cycle === true){
					$(this).hrzAccordionLoop();
				}
			});				
		}		
	});
})(jQuery);	
