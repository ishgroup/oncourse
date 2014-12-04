goog.provide('main');

goog.require('cms');

jQuery.noConflict();


var cmsOpts = {
    header: 60, // header height
    menu: 41 // menu height
};

function wrapHeight() {
    var mainTop = cmsOpts.header + cmsOpts.menu;
    var mainHeight = jQuery('body').height() - mainTop;

    jQuery('#edHeader, #edMenuWr').width(jQuery('body').width());
    jQuery('#edMain').css('min-height', mainHeight).width(jQuery('body').width());

}

// Open editor: toggle = true; Close editor: toggle = false.
function animLayout(toggle) {

	// Animation speed
	var speed = 700;


	// CSS3 Animation

	if (toggle) {

		jQuery('#edHeader').removeClass('show');
		jQuery('#edMenuWr').removeClass('top fix');
		jQuery('#edMain').removeClass('show fix');
		setTimeout("jQuery('#edControls').removeClass('hide')", speed);

	} else {

		jQuery('.cms-btn-control.active').click();

		jQuery('#edControls').addClass('hide');
		jQuery('#edHeader').addClass('show');
		jQuery('#edMenuWr').addClass('top');
		jQuery('#edMain').addClass('show');
		setTimeout("jQuery('#edMenuWr, #edMain').addClass('fix')", speed);
		wrapHeight();
	}

}


function hideEdit() {


    if (jQuery('.ie').size() > 0) {
        jQuery('.cms-pane-small').hide();
        jQuery('.cms-pane-wide').hide();
    }


    // Open Content Editor
    jQuery('#editSite').bind('click', function() {

        // Menu (Tabs) manipulation.
        jQuery('.cms-menu-shadow').hide();
        jQuery('#edMenu').children('li').removeClass('active');
        jQuery(this).parents('li').addClass('active');


        animLayout(true);


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
    	var actionLink = this.children[0].children[0];
    	Tapestry.findZoneManager( actionLink ).updateFromURL(actionLink.href);
        var actBtn = jQuery('.cms-btn-control.active');
        if (actBtn.size() > 0) {
            actBtn.click();
        }

        if (jQuery(this).children().is('.cms-pane-small')) {
            flag = true;
            /*if (ietrue) {
                jQuery(this).find('.cms-pane-small').slideDown('fast');
            } else {
                jQuery(this).find('.cms-pane-small').addClass('cms-pane-small-show');
            }*/
	        jQuery(this).find('.cms-pane-small').addClass('cms-pane-small-show');

        } else {
            flag = false;
            /*if (ietrue) {
                jQuery(this).next('.cms-pane-wide').slideDown('fast');
            } else {
                jQuery(this).next('.cms-pane-wide').addClass('cms-pane-wide-show');
            }*/
	        jQuery(this).next('.cms-pane-wide').addClass('cms-pane-wide-show');

        }

        jQuery(this).addClass('active');
        if (jQuery('.cms-darkover').size() == 0) {
            jQuery('<div>').addClass('cms-darkover').prependTo('body');
        } else {
            jQuery('.cms-darkover').show();
        }

    }, function() {
        jQuery('.cms-darkover').hide();
        jQuery(this).removeClass('active');
        if (flag === true) {
            /*if (ietrue) {
                jQuery(this).find('.cms-pane-small').slideUp('fast');
            } else {
                jQuery(this).find('.cms-pane-small').removeClass('cms-pane-small-show');
            }*/
	        jQuery(this).find('.cms-pane-small').removeClass('cms-pane-small-show');

            flag = false;
        } else {
            /*if (ietrue) {
                jQuery(this).next('.cms-pane-wide').slideUp('fast');
            } else {
                jQuery(this).next('.cms-pane-wide').removeClass('cms-pane-wide-show');
            }*/
	        jQuery(this).next('.cms-pane-wide').removeClass('cms-pane-wide-show');
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
//    jQuery("select, input:checkbox, input:radio, input:file").uniform();

    if (isChrome() != true) {
        jQuery('#scrollbar1').tinyscrollbar();
    }

    jQuery('#scrollTheme').tinycarousel({
        axis : 'y'
    });

}

function toolTips() {
    jQuery('label[title]').qtip({
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
				target: 'topLeft',
			 	tooltip: 'bottomLeft'
            }
        }
    });

//	jQuery('label[title]').tooltip();

}

function modals(formId) {
    jQuery("#dialog").dialog({
        autoOpen: false,
        modal : true,
	    resizable: true,
	    draggable: true,
        width : '70%',
        dialogClass: 'cms-dialog',
        buttons : [
            {
                text : "Cancel",
                click : function() {
                    jQuery("#dialog").dialog("close");
                },
                'class' : "cms-btn"
            },
            {
                text : "Save",
                click : function() {
                    jQuery("#dialog").dialog("close");
                    $(formId).fire(Tapestry.FORM_PROCESS_SUBMIT_EVENT);
                    //$('${regionForm.clientId}').fire(Tapestry.FORM_PROCESS_SUBMIT_EVENT);
                },
                'class' : "cms-btn cms-btn-primary"
            }
        ]
    }).open();
    jQuery('.cms-close-popup').click(function() {
        jQuery(this).parents('.cms-popup-edit').dialog('close');
    });
}

function tabsContent() {
    jQuery('#edMenu').delegate('li', 'click', function(event) {


        var ev = event.target;
        if (ev.id != 'editSite') {

            jQuery('.cms-btn-control.active').click();

            // Slide content area
            if (jQuery('#editSite').parent().parent().hasClass('active')) {

                // Animation

                animLayout(false);

                wrapHeight();
            }

            // Open tab

            window.scrollTo(0,0);

            var i = jQuery(this).index() - 1;
            jQuery(this).addClass("active");
            if (jQuery(this).children('em').size() == 0) {
                var f = document.createElement('em');
                f.className = 'cms-m-active';
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
    jQuery(".cms-edit-area-in").each(
            function() {
                var editBlockArea = jQuery(this);
                var editBlock = jQuery(this).parents('#z_content');
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
    if (document.getElementById('showEdit') != null && document.getElementById('showEdit').checked == false) {
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

/**
 * The function initializes mouse listeners for a menu item which show/hide
 * additonal contrals (like delete and drag&drop) for the menu item
 */
function highlightMenuItem(menuItem) {
    menuItem.mouseover(
            function(e) {
                jQuery(this).addClass("over").parents().removeClass("over");
                /*the code finds 'ul' child and if it exists and content of the ul element is not empty
                the code hides delete button for the menu item*/
                if (jQuery(this).children('ul').children().size())
                {
                    jQuery(this).find("span.cms-menu-pages-dl").css("visibility", "hidden");
                }
                e.stopPropagation();
            }).mouseout(function(e) {
                jQuery(this).removeClass("over");
                jQuery(this).find("span.cms-menu-pages-dl").removeAttr("style");
            });
}

function sortThemes() {
	jQuery("#b_header, #b_left, #b_content, #b_right, #b_footer, #b_unassigned").sortable({
        connectWith: ".sortableThemes",
        stop: function(event, ui) {
            var itemId = jQuery(ui.item).attr('id').substring(3);
            var parent = jQuery(ui.item).parent();
            var s = parent.sortable('toArray');
            var index = 0;
            var parSubstr = parent.attr('id').substring(2);

            for (var i = 0; i < s.length; i++) {

                if (s[i].substring(3) == itemId) {
                    break;
                }

                index++;
            }

	        jQuery.ajax({
                async: false,
                cache: false,
				type: "POST",
				data: "id="+itemId+"&region="+parSubstr+"&w="+index,
				url: '/cms/ui/internal/page.pagestructure.cmsnavigation.pagetypes.pagetypeedit.sort?t:cp=ui/internal/page',
				complete: function(data){
					if(data.responseText=="{status: 'session timeout'}"){
						window.location.reload();
					}
				}
			});

        }

    });
}


function newTabs() {

	var status = true;

	jQuery('#edMain').find('.cms-main-content').prepend('<div id="cms-tabs-cont-0"></div>');

	// Set IDs for tabs
	 var t = 1, elem;
	jQuery('#edMain .cms-tabs-cont').each(function() {
		elem = jQuery(this).attr('class') + '-' + t;
		this.id = elem;
		t++;
	});

	// Start  jQuery UI tabs functional
	jQuery("#cmsTabs").tabs({
		selected: 0,
		select: function(event, ui) {
			actionLink=ui.tab.next();
			if(actionLink != null){
				Tapestry.findZoneManager( actionLink ).updateFromURL(actionLink.href);
			}
			if(ui.index == 0) { // Editor tab clicked
				window.scrollTo(0, 0);// Scroll window to Top
				animLayout(true); // Open editor
				status = true;

			} else {
				if(status) {
					animLayout(false); // Close Editor
				}
				status = false;
			}

		}
	});

}



// Load all
jQuery(document).ready(function() {
    isChrome();// Check Google Chrome

    toolTips(); // Tooltips

    customForms(); // Styling form controls

    //hideEdit(); // Show Content Editor

    pageBar(); // Menu with pages options

    //wrapHeight(); // Size & Position of all wrappers

    //tabsContent(); // Main menu (Tabs)

    editArea(); // Show Edit area

    //editThemes();

    // Menu list (Menus tab).
    highlightMenuItem(jQuery(".cms-menu-pages li"));


//    jQuery('#editSite').click();


	newTabs();




});

jQuery(window).resize(function() {
    wrapHeight();
});

