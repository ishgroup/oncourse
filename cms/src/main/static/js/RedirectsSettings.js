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
    loadItemHtml: function (callBack, isNew) {
        $j.ajax({
            type: 'POST',
            url: '/ui/internal/page.pagestructure.cmsnavigation.sitesettings.redirectssettings:newitem',
            async: false,
            cache: false,
            dataType: 'json',
            success: function (data, status, jqXHR) {
                var item = $j(data.content);
                if (isNew) {
                    RedirectsSettings.prototype.hideDeleteControl(item);
                }
                callBack(item);
            }
        });
    },

    //init even handling for delete action
    initDelete: function (item) {
        item.children('.cms-redirect-delete').click(function () {

            //we don't need to delete new item.
            if (RedirectsSettings.prototype.isNew(item)) {
                return;
            }

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

    isNew: function (item) {
        return (item.children('input[name=id]').attr('value') ? false : true);
    },

    showDeleteControl: function (item) {
        item.children('.cms-ico-del').removeClass('cms-hidden');
        item.children('.cms-redirect-delete').removeClass('cms-hidden');

    },

    hideDeleteControl: function (item) {
        item.children('.cms-ico-del').addClass('cms-hidden');
        item.children('.cms-redirect-delete').addClass('cms-hidden');

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
                        var isNew = RedirectsSettings.prototype.isNew(item);
                        RedirectsSettings.prototype.fillItem(item, data.value);
                        RedirectsSettings.prototype.showDeleteControl(item);
                        item.effect('highlight');
                        //only if new item we saved we need controls for new item
                        if (isNew) {
                            RedirectsSettings.prototype.loadItemHtml(RedirectsSettings.prototype.addItem, isNew);
                        }
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
    fillItem: function (item, data) {
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
    addItem: function (item) {
        $j('#cms-redirect-items').prepend(item);
        RedirectsSettings.prototype.initDelete(item);
        RedirectsSettings.prototype.initSave(item);
        item.children('input[name=urlPath]').focus();
        item.effect('highlight');
    },

    loadItems: function () {
        $j.ajax({
            type: 'POST',
            url: '/ui/internal/page.pagestructure.cmsnavigation.sitesettings.redirectssettings:loadItems',
            async: false,
            cache: false,
            dataType: 'json',
            success: function (data, status, jqXHR) {
                var rItem;
                //load html source code for redirect item
                RedirectsSettings.prototype.loadItemHtml(function (value) {
                    rItem = value;
                });

                //appends all existing redirect items
                $j(data).each(function (index, value) {
                    var item = rItem.clone();
                    RedirectsSettings.prototype.fillItem(item, value);
                    RedirectsSettings.prototype.addItem(item);
                });
                //appends new item
                RedirectsSettings.prototype.loadItemHtml(RedirectsSettings.prototype.addItem, true);
            }
        });

    }


};

jQuery(document).ready(
    function () {
        var settings = new RedirectsSettings();
        settings.loadItems();
    });