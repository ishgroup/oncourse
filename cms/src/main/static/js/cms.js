goog.provide('cms');


goog.require('tinyscrollbar');
goog.require('iphoneStyleChckbx');
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

                        jQuery("#cms-publish-dialog").dialog("close");
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

function fireDeleteMenu(elemID) {
    jQuery.ajax({
        type: "POST",
        data: "id="+elemID,
        url: '/ma.remove',
        complete: function(data){
            if(data.responseText=="{status: 'OK'}"){
                jQuery(".cms_navmenu_list #m_" + elemID).remove();
            }else if(data.responseText=="{status: 'session timeout'}"){
                window.location.reload();
            } else{
                alert("The node with children cannot be removed.");
            }
        },
        async:true
    });
    return false;
}

function showDeleteMenuConfirmation(id) {
    deleteDialog(id, "#deleteMenuModal"+id, 'Are you sure you wish to delete this item?', fireDeleteMenu);
}

function fireDeleteBlock(elementId) {
    var actionId = elementId.substring(7);
    var actionLink = jQuery("a[clientId='deleteBlock"+actionId+"']");
    var actionHref = actionLink.attr("href");
    jQuery.ajax({
        type: "POST",
        data: "id="+actionId,
        url: actionHref,
        complete: function(data){
            if(data.responseText=="{status: 'OK'}"){
                var id = this.data.substring(3);
                jQuery("tr[id='block_row_"+id+"']").remove();
            } else if(data.responseText=="{status: 'session timeout'}"){
                window.location.reload();
            } else if(data.responseText=="{status: 'FAILED'}") {
                alert("This block cannot be deleted since it is has been used by a theme(s).");
            }else{
                alert("The block cannot be removed due some exception.");
            }
        },
        async:true
    });
    return false;
}

function showDeleteBlockConfirmation(id) {
    deleteDialog(id, "#deleteBlockModal"+id, 'Are you sure you wish to delete this item?', function()
    {
        jQuery("a[id='dBlock_"+id+"']").trigger('click');
    });
}



function fireDeletePage(elementId) {
    var actionId = elementId.substring(6);
    var actionLink = jQuery("a[clientId='deletePage"+actionId+"']");
    var actionHref = actionLink.attr("href");
    jQuery.ajax({
        type: "POST",
        dataType: "json",
        data: "id="+actionId,
        url: actionHref,
        complete: function(data){
            var json = jQuery.parseJSON(data.responseText);
            if(json.status == 'OK'){
                var id = this.data.substring(3);
                jQuery("tr[id='page_row_"+id+"']").remove();
                if (window.location.pathname == ("/page/"+id)) {
                    var oldPath = "/page/"+id;
                    var newHref = window.location.href.replace(oldPath,"/");
                    window.location = newHref;
                    return false;
                }
            } else if(json.status == 'ERROR' || json.status == 'WARNING'){
                window.location.reload();
            }
        },
        async:true
    });
    return false;
}

function showDeletePageConfirmation(id) {
    deleteDialog(id, "#deleteModal" + id, 'Are you sure you wish to delete this item?', function () {
        jQuery("a[id='dPage_" + id + "']").trigger('click');
    });
}

function fireDeleteTheme(elementId) {
    var actionId = elementId.substring(7);
    var actionLink = jQuery("a[clientId='deleteTheme"+actionId+"']");
    var actionHref = actionLink.attr("href");
    jQuery.ajax({
        type: "POST",
        data: "id="+actionId,
        url: actionHref,
        complete: function(data){
            if(data.responseText=="{status: 'OK'}"){
                var id = this.data.substring(3);
                jQuery("tr[id='theme_row_"+id+"']").remove();
            } else if(data.responseText=="{status: 'session timeout'}"){
                window.location.reload();
            } else if(data.responseText.split(',').length==2) {
                var splittedData = data.responseText.split(',');
                if (splittedData[0] == "{status: 'FAILED'") {
                    alert("This theme cannot be deleted since it is has been used by a page.");
                } else {
                    alert("The theme cannot be removed due some exception.");
                }
            } else{
                alert("The theme cannot be removed due some exception.");
            }
        },
        async:true
    });
    return false;
}

function showDeleteThemeConfirmation(id) {
    deleteDialog(id, "#deleteThemeModal" + id, 'Are you sure you wish to delete this item?', function () {
        jQuery("a[id='dTheme_"+id+"']").trigger('click');
    });
}







		