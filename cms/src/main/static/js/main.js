goog.provide('main');

goog.require('qtip');
goog.require('cms');
goog.require('tinyscrollbar');
goog.require('themeEditor');


jQuery.noConflict();


// Functions toggle on click  event solution
// According to new jQuery 1.11.1 spec  
jQuery.fn.toggleClick = function( fn, fn2 ) {

    // Don't mess with animation or css toggles
    if ( !jQuery.isFunction( fn ) || !jQuery.isFunction( fn2 ) ) {
        return oldToggle.apply( this, arguments );
    }
    // migrateWarn("jQuery.fn.toggle(handler, handler...) is deprecated");
    // Save reference to arguments for access in closure
    var args = arguments,
        guid = fn.guid || jQuery.guid++,
        i = 0,
        toggler = function( event ) {
            // Figure out which function to execute
            var lastToggle = ( jQuery._data( this, "lastToggle" + fn.guid ) || 0 ) % i;
            jQuery._data( this, "lastToggle" + fn.guid, lastToggle + 1 );

            // Make sure that clicks stop
            event.preventDefault();

            // and execute the function
            return args[ lastToggle ].apply( this, arguments ) || false;
        };

    // link all the functions, so any of them can unbind this click handler
    toggler.guid = guid;
    while ( i < args.length ) {
        args[ i++ ].guid = guid;
    }

    return this.click( toggler );
};

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


    if (jQuery('.ie').length > 0) {
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
    if (jQuery('.ie').length > 0) {
        ietrue = true;
    } else {
        ietrue = false;
    }

    jQuery('.cms-close-pane').bind('click', function() {
        jQuery('.cms-btn-control.active').click();
    });


    jQuery('#edControls').find('.cms-btn-control').toggleClick(function() {
    	var actionLink = this.children[0].children[0];
    	Tapestry.findZoneManager( actionLink ).updateFromURL(actionLink.href);
        var actBtn = jQuery('.cms-btn-control.active');
        if (actBtn.length > 0) {
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
        if (jQuery('.cms-darkover').length == 0) {
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

function toolTips() {
    jQuery('label[title]').qtip({
        // style: 'qtip-light',
        position: {
            my: 'bottom left',  // Position my top left...
            at: 'top left' // at the bottom right of...
        }
    });

//	jQuery('label[title]').tooltip();

}

function modals(formId) {
    var element = jQuery("#dialog");
    element.dialog({
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
                    if (element.dialog('isOpen') === true) {
                        jQuery(this).dialog("close");
                    }
                },
                'class' : "cms-btn"
            },
            {
                text : "Save",
                click : function() {
                    if (element.dialog('isOpen') === true) {
                        jQuery(this).dialog("close");
                        $(formId).fire(Tapestry.FORM_PROCESS_SUBMIT_EVENT);
                    }
                },
                'class' : "cms-btn cms-btn-primary"
            }
        ]
    });
    element.dialog('open');
    fixDialogButtons(element);
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
            if (jQuery(this).children('em').length() == 0) {
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
                if (jQuery(this).children('ul').children().length)
                {
                    jQuery(this).find("span.cms-menu-pages-dl").css("visibility", "hidden");
                }
                e.stopPropagation();
            }).mouseout(function(e) {
                jQuery(this).removeClass("over");
                jQuery(this).find("span.cms-menu-pages-dl").removeAttr("style");
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

function customForms() {
    jQuery('.cms-onoff').iphoneStyle();

    if (isChrome() != true) {
        jQuery('#scrollbar1').tinyscrollbar();
    }

    jQuery('#scrollTheme').tinyscrollbar();
}


/**
 * the function is used to show modal dialog when an user tries to delete page,block, menu or theme
 * it was introduced as a workaround for jquery jquery-1.11.1.js and jquery-ui-1.9.1.min.js combination
 * it should be delete after the application will be migrated to new version of  jquery-ui
 */
function deleteDialog(id, htmlId, title, callback)
{
    var element = jQuery(htmlId);
    element.dialog({
        autoOpen: false,
        resizable: false,
        width: 420,
        modal: true,
        title: title,
        buttons: {
            "Delete": function() {
                if (element.dialog('isOpen') === true)
                {
                    element.dialog("close");
                    callback(id);
                }
            },
            Cancel: function() {
                if (element.dialog('isOpen') === true)
                {
                    element.dialog("close");
                }
            }
        }
    });
    fixDialogButtons(element)
    element.dialog("open");
}

function fixDialogButtons(element) {
    var buttons = element.dialog().nextAll('.ui-dialog-buttonpane').children().children('button');
    jQuery.each(buttons, function(i, b)
    {
        if (jQuery(b).attr('text'))
        {
            jQuery(b).children('span').text(jQuery(b).attr('text'));
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

