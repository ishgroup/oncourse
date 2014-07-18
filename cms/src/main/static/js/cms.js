goog.provide('cms');

goog.require('plugin');
/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
document.observe(Tapestry.FORM_VALIDATE_FIELDS_EVENT, function(event) {reloadPageOnSessionTimeout();});

function reloadPageOnSessionTimeout(){
    jQuery.ajaxSetup({async:false});
    jQuery.post('/cms/checksession', function (data) {
        if(data!='session alive'){
            window.location.reload();
        }else{
            jQuery.ajaxSetup({async:true});
        }
    });
}
jQuery(document).ready(
    function() {
        jQuery(".cms_editor_tabs").tabs();
        initDeploy();
    });

function customForms() {
    jQuery('.cms-onoff').iphoneStyle();
    jQuery("select, input:checkbox, input:radio, input:file").uniform();

    if(isChrome() != true) {
        jQuery('#scrollbar1').tinyscrollbar();
    }
}

function initDeploy()
{

    jQuery('#cms-deploy').click(function() {
        var action = jQuery("#cms-deploy-action");
        if (action.hasClass("cms-disabled"))
            return;

        action.addClass("cms-disabled");
        deploy();

        jQuery("#cms-deploy-dialog").dialog({
            modal : true,
            resizable: false,
            draggable: false,
            width : '50%',
            buttons : [
                {
                    text : "OK",
                    click : function() {
                        jQuery(this).dialog("close");
                        location.reload();
                    },
                    'class' : "cms-btn"
                }
            ]
        });

    });
}

function deploy()
{
    jQuery.ajax({
        type: "GET",
        url: "/ui/internal/page.pagestructure.cmsnavigation.sitesettings.websitesettings:deploysite",
        async: false,
        //if the parameter is not set internet explorer loads content from cache
        cache: false,
        success: function(data) {
        },
        error:  function(data) {
            jQuery("#cms-deploy-error").dialog({
                modal : true,
                resizable: false,
                draggable: false,
                width : '50%',
                buttons : [
                    {
                        text : "OK",
                        click : function() {
                            jQuery(this).dialog("close");
                        },
                        'class' : "cms-btn"
                    }
                ]
            });
        }
    });
}