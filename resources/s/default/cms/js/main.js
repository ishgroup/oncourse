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
	jQuery('#editSite').bind('click', function() {
		jQuery('.cms-menu-shadow').hide();
		jQuery('#edMenu').children('li').removeClass('active');
		jQuery(this).parents('li').addClass('active');

		jQuery('#edHeader').slideUp(100);
		jQuery('#edMain').slideUp(700);
		jQuery('#edMenuWr').animate({
			top : topScroll
		}, {
			duration : 700,
			complete : function() {
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

	if (isChrome() != true) {
		jQuery('#scrollbar1').tinyscrollbar();
	}

	jQuery('#scrollTheme').tinycarousel({
		axis : 'y'
	});

}

function toolTips() {
	jQuery('span[title]').qtip({
		style : {
			background : '#ffda8b',
			color : '#613c00',
			border : {
				width : 1,
				color : '#ffda8b'
			},
			tip : true
		},
		position : {
			corner : {
				tooltip : 'leftMiddle'
			}
		}
	});
}

function modals(formId) {
    jQuery("#dialog").dialog({
       modal : true,
       //height : 430,
       width : 540,
       dialogClass: 'cms-dialog',
       buttons : [
         {
          text : "Cancel",
          click : function() {
           jQuery(this).dialog("close");
          },
          'class' : "cms-but cms-btn-prev alignCenter"
         },
         {
          text : "Save",
          click : function() {
           jQuery(this).dialog("close");
           $(formId).fire(Tapestry.FORM_PROCESS_SUBMIT_EVENT);
           //$('${regionForm.clientId}').fire(Tapestry.FORM_PROCESS_SUBMIT_EVENT);
          },
          'class' : "cms-but cms-btn-next alignCenter"
         } ]
      });
    jQuery('.cms-close-popup').click(function(){
	      jQuery(this).parents('.cms-popup-edit').dialog('close');
	 });
    //jQuery("#dialog").dialog('close');

}

function tabsContent() {
    jQuery('#edMenu').delegate('li', 'click', function(event) {


        var ev = event.target;
        if(ev.id != 'editSite') {

            // Slide content area
            if(jQuery('#editSite').parent().parent().hasClass('active')){
                var topScroll = jQuery('#edHeader').height();

                jQuery('#edControls').slideUp();
                jQuery('#edHeader').slideDown(100);
                jQuery('#edMain').slideDown(700);

                jQuery('#edMenuWr').animate({
                    top: topScroll
                }, {
                    duration: 700
                });
            }

            // Open tab

            var i = jQuery(this).index() - 1;
            jQuery(this).addClass("active");
            if(jQuery(this).children('em').size() == 0) {
                var f = document.createElement('em');
                f.className='cms-m-active';
                jQuery(this).append(f);
            }
            jQuery(this).siblings().removeClass("active").children('em').remove();
            jQuery('#edMain').find('.cms-tabs-cont').hide().eq(i).fadeIn(150);
        }

    });


}

function editArea() {
	wrapEditAreas();

	jQuery('#showEdit').change(function() {
		if (this.checked) {
				jQuery('.cms-edit-area').removeClass('hover');
				jQuery('.cms-edit-area').show();
		} else {
			jQuery('.cms-edit-area').hide();
		}
	});

}

function wrapEditAreas() {
	$j(".cms-edit-area-in").each(
			function() {
				editBlockArea = $j(this);
				editBlock = editBlockArea.parent().parent().parent();
				editBlock.hover(function() {
					if (document.getElementById('showEdit').checked == false) {
							jQuery('.cms-edit-area').addClass('hover');
							jQuery('.cms-edit-area').show();
					}
				}, function() {
					if (document.getElementById('showEdit').checked == false) {
						jQuery('.cms-edit-area').removeClass('hover');
						jQuery('.cms-edit-area').hide();
					}
				});
				
				editBlockArea.css('width', editBlock.width() + "px").css(
						'height', editBlock.height() + "px");
			});
	if (document.getElementById('showEdit').checked == false) {
		jQuery('.cms-edit-area').hide();
	}
	
}

function editThemes() {
	var list = jQuery('#cmsThemeList');
	var key = jQuery('#cmsThemeKey');
	var themeId = jQuery('#cmsThemeKeyId');

	themeId.val(list.children('.selected').attr('rel'));
	list.children('.selected').clone().appendTo(key);
	key.toggle(function() {
		list.addClass('show');
	}, function() {
		list.removeClass('show');
	});
	list.children('span').click(
			function() {
				key.html('');
				jQuery(this).clone().appendTo(key);
				jQuery(this).siblings().removeClass('selected').end().addClass(
						'selected');
				key.click();
				themeId.val(jQuery(this).attr('rel'));
			});

	// Auto resize modules in table cell
	/*var themeLayout = function() {
	    jQuery('.cms-theme-layout').find('.cms-theme-module').each(function(){
	        var module = jQuery(this);
	        var modH = module.parent('td').innerHeight() / (module.siblings('.cms-theme-module').size() + 1) - 5;
	       
	    module.css({'height':modH});
	    });
	};
	themeLayout();*/

}

function highlightMenuItem(menuItem){
	menuItem.mouseover(
            function(e) {
                jQuery(this).addClass("over").parents().removeClass("over");
                e.stopPropagation();
            }).mouseout(function(e) {
            	jQuery(this).removeClass("over");
            });
}

function sortThemes(){
	$j( "#b_header, #b_left, #b_content, #b_right, #b_footer, #b_unassigned" ).sortable({
		connectWith: ".sortableThemes",
		stop: function(event, ui) { 
			var itemId = $(ui.item).attr('id').substring(3);
			var parent=$(ui.item).parent();
			var s = parent.sortable('toArray');
			var index = 0;
			
			for (var i=0; i< s.length; i++) {
				
				if(s[i].substring(3) == itemId) {
					break;
				} 
				
				index++;
			}
			$j.post('cms/ui/textiletags.pagestructure.cmsnavigation.pagetypes.pagetypeedit.sort?t:cp=ui/page', 
					s{id: itemId, region: parent.attr('id').substring(2), w:index});
		}

	});
}

// Load all
jQuery(document).ready(function() {
	isChrome();

 jQuery('#edControls').slideUp();

	toolTips();

	customForms();
	if (jQuery('.ie').size() > 0) {
		jQuery('.cms-pane-small').hide();
		jQuery('.cms-pane-wide').hide();
	}

	hideEdit();
	pageBar();
	wrapHeight();
	tabsContent();

	jQuery('#signIn').click(function() {
		jQuery('.cms-login').remove();
		jQuery('body').css('overflow', 'auto');
	});

	editArea();

	editThemes();

	 // Menu list (Menus tab).
	highlightMenuItem(jQuery(".cms-menu-pages li"));
});

