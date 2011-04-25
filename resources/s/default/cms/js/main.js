jQuery.noConflict();

function wrapHeight() {
    jQuery('body').css('overflow', 'hidden');
    var head = jQuery('#edHeader').outerHeight();
    var menu = jQuery('#edMenuWr').outerHeight();
    var sumElems = jQuery(window).height() - (head + menu);
    var contWr = jQuery(window).height() - menu - 44;
    jQuery('#edMain').height(sumElems + 1);

}



function hideEdit() {
    var topScroll = jQuery(window).height() - jQuery('#edMenuWr').height() - 1;
    jQuery('#editSite').bind('click', function(){
        jQuery('.cms-menu-shadow').hide();
        jQuery('#edMenu').children('li').removeClass('active');
        jQuery(this).parents('li').addClass('active');

        jQuery('#edHeader').slideUp(100);
        jQuery('#edMain').slideUp(700);
        jQuery('#edMenuWr').animate({
            top: topScroll
        }, {
            duration: 700,
            complete: function() {
              jQuery('#edControls').slideDown();
            }
        });            


        return false;
    });
}

function pageBar() {

    var flag = false;
    var ietrue;
    if (jQuery('.ie').size() > 0) {
        ietrue = true;
    } else {
        ietrue = false;
    }

    jQuery('.cms-close-pane').bind('click', function() {
        jQuery('.cms-btn-control.active').click();
    });


    jQuery('#edControls').find('.cms-btn-control').toggle(function() {

        var actBtn = jQuery('.cms-btn-control.active');
        if (actBtn.size() > 0) {
            actBtn.click();
        }

        if (jQuery(this).children().is('.cms-pane-small')) {
            flag = true;
            if (ietrue) {
                jQuery(this).find('.cms-pane-small').slideDown('fast');
            } else {
                jQuery(this).find('.cms-pane-small').addClass('cms-pane-small-show');
            }

        } else {
            flag = false;
            if (ietrue) {
                jQuery(this).next('.cms-pane-wide').slideDown('fast');
            } else {
                jQuery(this).next('.cms-pane-wide').addClass('cms-pane-wide-show');
            }

        }

        jQuery(this).addClass('active');
        if(jQuery('.cms-darkover').size() == 0) {
            jQuery('<div>').addClass('cms-darkover').prependTo('body');
        } else {
            jQuery('.cms-darkover').show();
        }

    }, function() {
        jQuery('.cms-darkover').hide();
        jQuery(this).removeClass('active');
        if (flag === true) {
            if (ietrue) {
                jQuery(this).find('.cms-pane-small').slideUp('fast');
            } else {
                jQuery(this).find('.cms-pane-small').removeClass('cms-pane-small-show');
            }

            flag = false;
        } else {
            if (ietrue) {
                jQuery(this).next('.cms-pane-wide').slideUp('fast');
            } else {
                jQuery(this).next('.cms-pane-wide').removeClass('cms-pane-wide-show');
            }
            flag = false;
        }

    });


    jQuery('.cms-pane-small').click(function(event) {
        event.stopPropagation();
    });

}

function isChrome() {
    return Boolean(window.chrome);
}




function customForms() {
    jQuery('.cms-onoff').iphoneStyle();
    jQuery("select, input:checkbox, input:radio, input:file").uniform();

    if(isChrome() != true) {
        jQuery('#scrollbar1').tinyscrollbar();
    }    

}

function toolTips() {
    jQuery('span[title]').qtip({
        style: {
            background: '#ffda8b',
            color: '#613c00',
            border: {
                 width: 1,
                 color: '#ffda8b'
              },
            tip: true
        },
        position: {
          corner: {
             tooltip: 'leftMiddle'
          }
       }
    });
}

/*function modals() {
    jQuery(".cms-popup-edit").dialog({
        width:534,
        open: function(event, ui) {
            //jQuery('<div>').addClass('darkover').prependTo('body');
            jQuery(".ui-dialog-titlebar-close").hide();
        },
        zIndex:2,
        dialogClass: 'alert'
    });
    jQuery('.cms-close-popup').click(function() {
        jQuery(".cms-popup-edit").dialog('close');
        //jQuery('.darkover').remove();
    });
}*/

function tabsContent() {
    jQuery('#edMenu').delegate('li', 'click', function() {
        var i = jQuery(this).index();
        jQuery(this).addClass("active");
        if(jQuery(this).children('em').size() == 0) {
            var f = document.createElement('em');
            f.className='cms-m-active';
            jQuery(this).append(f);
        }
        jQuery(this).siblings().removeClass("active").children('em').remove();
        jQuery('#edMain').find('.cms-tabs-cont').hide().eq(i).fadeIn(150);

    })

}


function editArea() {
	wrapEditAreas();

	jQuery('#showEdit').change(function() {
		if (this.checked) {
			jQuery('.cms-edit-area').show();
		} else {

			jQuery('.cms-edit-area').hide();

		}
	});
	jQuery('.cms-edit-area').hide();

}

function wrapEditAreas() {
	$j(".cms-edit-area-in").each(
			function() {
				editBlockArea = $j(this);
				editBlock = editBlockArea.parent().parent().parent();
				editBlockArea.css('width', editBlock.width() + "px").css(
						'height', editBlock.height() + "px");
			});
}

// Load all
jQuery(document).ready(function () {
    isChrome();

    //jQuery('#edControls').slideUp();

    toolTips();

    customForms();
    if(jQuery('.ie').size() > 0) {
        jQuery('.cms-pane-small').hide();
        jQuery('.cms-pane-wide').hide();
    }

    hideEdit();
    pageBar();
    wrapHeight();
    tabsContent();

    jQuery('#signIn').click(function(){
        jQuery('.cms-login').remove();
        jQuery('body').css('overflow', 'auto');
    });

    
    editArea();

    jQuery('.cms-close-popup').click(function(){
        jQuery(this).parents('.cms-popup-edit').hide();
    });


});



