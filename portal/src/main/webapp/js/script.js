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

function initHints(parentBlockId){
    parentBlockToUpdate='';
    if(parentBlockId!=null){
        parentBlockToUpdate = '#'+parentBlockId+' ';
    }
    // Show us hints on entering the field if the input is not invalid
    jQuery(parentBlockToUpdate+'span.valid input').bind("focus blur", function() {
        parent=jQuery(this).next().next();
        if(parent.children('.hint').length==0){
            //for the inputs that have one more image before hint (like calendar tapestry component)
            parent=parent.next();
        }
        parent.children('.hint').toggleClass('hidden-text');
        //$j(this).parent().nextAll('.hint:first').toggle();
    });

    // Show us reasons for errors on entering the field if the input IS invalid
    jQuery(parentBlockToUpdate+'span.validate input').bind("focus blur", function() {
        parent=jQuery(this).next().next();
        if(parent.children('.reason').length==0){
            //for the inputs that have one more image before hint (like calendar tapestry component)
            parent=parent.next();
        }
        parent.children('.reason').toggleClass('hidden-text');
    });
    jQuery(parentBlockToUpdate+'span.validate select').bind("focus blur", function() {
        parent=jQuery(this).next().next();
        if(parent.children('.reason').length==0){
            //for the inputs that have one more image before hint (like calendar tapestry component)
            parent=parent.next();
        }
        parent.children('.reason').toggleClass('hidden-text');
    });
}
