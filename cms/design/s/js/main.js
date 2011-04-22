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


    var ietrue;
    if (jQuery('.ie').size() > 0) {
        ietrue = true;
    } else {
        ietrue = false;
    }


    var fThis = jQuery('#welcome').children('p');

    var offset = fThis.offset();
        var left = offset.left;
        var top = offset.top;
        var fHeight = fThis.height();
        var fWidth = fThis.width();
        var fHeightM = -fHeight + 'px';
        var backWr, backIn;


        if (ietrue) {
            backWr = 'url(s/cms/img/edit-area.png) left top';
            backIn = 'url(s/cms/img/edit-label.png) no-repeat center center';
        } else {
            backWr = 'rgba(255, 180, 0, 0.8)';
            backIn = 'rgba(255, 182, 0, 0.1) url(s/cms/img/edit-label.png) no-repeat center center';
        }


        var ediBlock = jQuery('<div/>', {
            'class': 'cms-edit-area',
            css: {
                border: '1px solid #ff6c00',
                background: backWr,
                padding: '2px',
                position: 'absolute',
                zIndex: '15',
                boxShadow: '0 0 7px rgba(0, 0, 0, 0.4)',
                marginTop: fHeightM
            },
            html: '<div class="cms-edit-area-in" style="border: 1px solid #ff6c00;background: ' + backIn + ';width:' + fWidth + 'px;height:' + fHeight + 'px"></div>'
        });



    jQuery(fThis).hover(function() {
        if (document.getElementById('showEdit').checked == false) {
            if (jQuery('.cms-edit-area').size() == 0) {
                jQuery(fThis).append(ediBlock);
            } else {
                jQuery('.cms-edit-area').show();
            }
        }
    }, function() {
        if (document.getElementById('showEdit').checked == false) {
            jQuery('.cms-edit-area').hide();
        }
    });





    jQuery('#showEdit').change(function(){
        if(this.checked) {
            

            if (jQuery('.cms-edit-area').size() == 0) {
                jQuery(fThis).append(ediBlock);
            } else {
                jQuery('.cms-edit-area').show();
            }

        } else {
           
            jQuery('.cms-edit-area').hide();

        }
    });






}



// Load all
jQuery(document).ready(function () {
    isChrome();

    jQuery('#edControls').slideUp();

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



