goog.provide('cms');

goog.require('plugin');
goog.require('RedirectsSettings');
/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
document.observe(Tapestry.FORM_VALIDATE_FIELDS_EVENT, function (event) {
    reloadPageOnSessionTimeout();
});

function reloadPageOnSessionTimeout() {
    jQuery.ajaxSetup({async: false});
    jQuery.post('/cms/checksession', function (data) {
        if (data != 'session alive') {
            window.location.reload();
        } else {
            jQuery.ajaxSetup({async: true});
        }
    });
}
jQuery(document).ready(
    function () {
        jQuery(".cms_editor_tabs").tabs();
        initPublish();
        initAddNewPage();
    });

function customForms() {
    jQuery('.cms-onoff').iphoneStyle();
    jQuery("select, input:checkbox, input:radio, input:file").uniform();

    if (isChrome() != true) {
        jQuery('#scrollbar1').tinyscrollbar();
    }
}

function initPublish() {

    jQuery('#cms-publish').click(function () {
        var action = jQuery("#cms-publish-action");
        if (action.hasClass("cms-disabled"))
            return;

        action.addClass("cms-disabled");
        publish();

        jQuery("#cms-publish-dialog").dialog({
            autoOpen: false,
            modal: true,
            resizable: false,
            draggable: false,
            width: '50%',
            buttons: [
                {
                    text: "OK",
                    click: function () {
                        jQuery(this).dialog("close");
                        location.reload();
                    },
                    'class': "cms-btn"
                }
            ]
        }).dialog('open');

    });
}

function publish() {
    jQuery.ajax({
        type: "GET",
        url: "/ui/internal/page.pagestructure.cmsnavigation.sitesettings.websitesettings:publish",
        async: false,
        //if the parameter is not set internet explorer loads content from cache
        cache: false,
        success: function (data) {
        },
        error: function (data) {
            jQuery("#cms-publish-error").dialog({
                 autoOpen: false,
                    modal: true,
                resizable: false,
                draggable: false,
                width: '50%',
                buttons: [
                    {
                        text: "OK",
                        click: function () {
                            jQuery(this).dialog("close");
                        }
                    }
                ]
            }).dialog('open');
        }
    });
}

//even handler for page visible checkbox
function initChangeVisibility() {
    //finds checkbox inputs in PageItem.tml
    jQuery("tr[id*=page_row]").find("input[name=cms-page-published]").click(function () {
        var nodeNumber = jQuery(this).data('nodeid');
        var data = {
            'id': nodeNumber,
            'published': jQuery(this).prop('checked')
        };
        jQuery.ajax({
            type: "POST",
            url: "/cms/ui/internal/page.pagestructure.cmsnavigation.pagescomponent.pageitem:changevisibility",
            data: data,
            async: false,
            //if the parameter is not set internet explorer loads content from cache
            cache: false,
            success: function (data) {
                var json = jQuery.parseJSON(data.responseText);
                if(json.status != 'OK'){
                    window.location.reload();
                }
            },
            error: function (data) {
                window.location.reload();
            }
        });
    });
}

function initAddNewPage() {
    jQuery('#newPage').click(function () {
        jQuery.ajax({
            type: "POST",
            url: "/ui/internal/page.pagestructure.cmsnavigation.pagesComponent:newPage",
            async: true,
            cache: false,
            success: function (data) {
                if (data.path == null) {
                    jQuery("#cms-addPage-error").dialog({
                        autoOpen: false,
                        modal: true,
                        resizable: false,
                        draggable: false,
                        width: '50%',
                        buttons: [
                            {
                                text: "OK",
                                click: function () {
                                    jQuery(this).dialog("close");
                                },
                                'class': "cms-btn"
                            }
                        ]
                    }).dialog('open');
                } else {
                    window.location.href = data.path;                    
                }
            }
        });
    });
}
		