/**
 * Created by akoyro on 9/4/14.
 *
 * RedirectsSettings is java script contoller for redirect item functionality
 */
goog.provide('RedirectsSettings');

var $j = jQuery.noConflict();

RedirectsSettings = function () {
};

RedirectsSettings.prototype = {

    //load html source for redirect item
    loadItemHtml: function(callBack)
    {
        $j.ajax({
            type: 'POST',
            url: '/ui/internal/page.pagestructure.cmsnavigation.sitesettings.redirectssettings:newitem',
            async: false,
            cache: false,
            dataType: 'json',
            success: function (data, status, jqXHR) {
                var item = $j(data.content);
                callBack(item);
            }
        });
    },

    //init even handling for delete action
    initDelete: function (item) {
        item.children('.cms-redirect-delete').click(function () {
            var data = item.children('input').serialize();

            $j.ajax({
                type: 'POST',
                url: '/ui/internal/page.pagestructure.cmsnavigation.sitesettings.redirectssettings:deleteItem',
                async: false,
                cache: false,
                data: data,
                dataType: 'json',
                success: function (data, status, jqXHR) {
                    item.remove();
                }
            });
        });
    },

    //init event handling for save action
    initSave: function (item) {
        item.children('.cms-redirect-save').click(function () {
            var data = item.children('input').serialize();
            $j.ajax({
                type: 'POST',
                url: '/ui/internal/page.pagestructure.cmsnavigation.sitesettings.redirectssettings:saveItem',
                async: false,
                cache: false,
                data: data,
                dataType: 'json',
                success: function (data, status, jqXHR) {
                    var children;
                    RedirectsSettings.prototype.cleanValidation(item);
                    if (data.error) {
                        if (data.error.key) {
                            children = item.children('input[name=' + data.error.key + ']');
                            children.addClass('cms-error');
                            children.attr('title', data.error.message);
                            children.tooltip({tooltipClass: 'cms-redirect-tooltip-error'});
                            children.focus();
                        }
                    } else {
                        RedirectsSettings.prototype.fillItem(item, data.value);
                        item.effect('highlight');
                        RedirectsSettings.prototype.loadItemHtml(RedirectsSettings.prototype.addItem);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    $j(item).tooltip('option', 'show', { content: "Unexpected error!" });
                    console.log(textStatus, errorThrown);
                }
            });
        });
    },

    //fill data for redirect item
    fillItem: function(item, data)
    {
        item.children('input[name=id]').attr('value', data.id);
        item.children('input[name=urlPath]').attr('value', data.urlPath);
        item.children('input[name=redirectTo]').attr('value', data.redirectTo);
    },

    cleanValidation: function (item) {
        var children = item.children('input');
        children.removeClass('cms-error');
        children.attr('title', null);
    },

    //add html element for redirect
    addItem: function(item)
    {
        $j('#cms-redirect-items').prepend(item);
        RedirectsSettings.prototype.initDelete(item);
        RedirectsSettings.prototype.initSave(item);
        item.children('input[name=urlPath]').focus();
        item.effect('highlight');
    },

    loadItems: function() {
        $j.ajax({
            type: 'POST',
            url: '/ui/internal/page.pagestructure.cmsnavigation.sitesettings.redirectssettings:loadItems',
            async: false,
            cache: false,
            dataType: 'json',
            success: function (data, status, jqXHR) {
                var rItem;
                //load html source code for redirect item
                RedirectsSettings.prototype.loadItemHtml(function(value)
                {
                    rItem = value;
                });

                //add all existing redirect items
                $j(data).each(function (index, value)
                {
                    var item = rItem.clone();
                    RedirectsSettings.prototype.fillItem(item, value);
                    RedirectsSettings.prototype.addItem(item);
                });
                //add all existing redirect items
                RedirectsSettings.prototype.addItem(rItem);
            }
        });

    }


};

jQuery(document).ready(
    function () {
        var settings = new RedirectsSettings();
        settings.loadItems();
    });