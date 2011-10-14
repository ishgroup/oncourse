jQuery(document).ready(function() {
	
	jQuery('input[type=checkbox]').prettyCheckboxes();
	
    /*lightbox*/
	var my_light = jQuery('.lightbox-pos');
	jQuery('.light').mouseover(function(){
		jQuery(this).next(my_light).show();
	}).mousemove(function(kmouse){
		jQuery(this).next(my_light).css({left:kmouse.pageX, top:kmouse.pageY});
	}).mouseout(function(){
		jQuery(this).next(my_light).hide();
	});
	/*class*/
	jQuery('.class tr:first-child').addClass('first');
	/*messages*/
	jQuery('.messages div:first-child').addClass('first');
	/*mark*/
	jQuery('.mark .mark-item:first').addClass('first');
	/*surveys*/
	jQuery('.surveys .surveys-item:first').addClass('first');
	/*timetable*/
	jQuery('.timetable .timetable-item:first-child').addClass('first');
});