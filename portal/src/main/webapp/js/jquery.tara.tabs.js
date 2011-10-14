(function( $ ){

	$.fn.taraTabs = function() {
  
		this.each(function() {
    			jQuery(this).click(function() {
    					active=jQuery(".tabs a.act");
    					if(active.length!=0){
    						active.removeClass("act");
    						jQuery("#"+active[0].id+"Content").hide();
    					}
		
    					clicked=jQuery(this);
    					clicked.addClass("act");
	
    					jQuery("#"+clicked[0].id+"Content").load(clicked[0].href);
		
    					jQuery("#"+clicked[0].id+"Content").show();
    					return false;
	
    			});
		});

	};
	
})( jQuery );
